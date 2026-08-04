package ru.mifi.practice.vol8.streaming;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Потоковая заливка большого JSON в PostgreSQL. Генератор пишет в канал, параллельный
 * поток читает из того же канала прямо в INSERT — документ целиком в памяти не оказывается.
 * Порядок такой: сначала текст попадает в TEXT-колонку, затем приводится к jsonb уже внутри
 * базы, потому что разбор двухсот мегабайт на стороне клиента дороже.
 *
 * <p>Адрес базы берётся из аргумента, системного свойства {@code jsonb.url} или переменной
 * окружения {@code JSONB_URL} — учётные данные в исходном коде не хранятся.
 *
 * <p>Схема, которую ожидает пример:
 * <pre>
 * CREATE TABLE IF NOT EXISTS streaming (
 *   id      SERIAL CONSTRAINT streaming_pk PRIMARY KEY,
 *   content TEXT NOT NULL
 * );
 * CREATE TABLE IF NOT EXISTS finished (
 *   id      SERIAL CONSTRAINT finished_pk PRIMARY KEY,
 *   content jsonb NOT NULL
 * );
 * CREATE INDEX IF NOT EXISTS finished_content_index ON finished USING GIN (content);
 * </pre>
 */
public final class JsonB {
    private static final String URL_PROPERTY = "jsonb.url";
    private static final int LARGE = 8_000_000;
    private static final long TIMEOUT_SECONDS = 600;
    private final String url;
    private final int size;

    public JsonB(String url, int size) {
        this.url = url;
        this.size = size;
    }

    public static void main(String[] args) throws Exception {
        new JsonB(url(args), LARGE).run();
    }

    private static String url(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        String property = System.getProperty(URL_PROPERTY, System.getenv("JSONB_URL"));
        if (property == null) {
            throw new IllegalStateException(
                "Database url is unknown, pass it as an argument or set -D" + URL_PROPERTY);
        }
        return property;
    }

    public void run() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        PipedInputStream input = new PipedInputStream();
        try {
            Future<Long> loaded;
            try (PipedOutputStream output = new PipedOutputStream(input);
                 JsonGenerator generator = new JsonFactory().createGenerator(output)) {
                loaded = executor.submit(() -> load(input));
                write(generator);
            }
            System.out.printf("Loaded row %d%n", loaded.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
            input.close();
        }
    }

    private void write(JsonGenerator generator) throws IOException {
        generator.writeStartArray();
        for (int i = 0; i < size; i++) {
            generator.writeStartObject();
            generator.writeNumberField("id", i);
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    private long load(PipedInputStream input) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);
            long key = stream(connection, input);
            finish(connection, key);
            connection.commit();
            return key;
        }
    }

    private long stream(Connection connection, PipedInputStream input) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO streaming(content) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setCharacterStream(1, new InputStreamReader(input, StandardCharsets.UTF_8));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Streaming insert into " + url + " returned no key");
                }
                return keys.getLong(1);
            }
        }
    }

    private void finish(Connection connection, long key) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO finished(content) SELECT content::jsonb FROM streaming WHERE id = ?")) {
            statement.setLong(1, key);
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM streaming WHERE id = ?")) {
            statement.setLong(1, key);
            statement.executeUpdate();
        }
    }
}
