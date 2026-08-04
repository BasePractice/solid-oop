package ru.mifi.practice.val3.cont.http;

import ru.mifi.practice.val3.cont.Deserializer;
import ru.mifi.practice.val3.cont.Http;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * HTTP поверх клиента из JDK. Ожидание ограничено с обеих сторон — на соединение
 * и на ответ, иначе один зависший сервис останавливает всю программу. Ошибка сети
 * не прячется под пустым {@code Optional}: пустота значит «ответ пуст», а не «всё сломалось».
 */
public final class Jdk implements Http {
    private static final Duration CONNECT = Duration.ofSeconds(5);
    private static final Duration READ = Duration.ofSeconds(10);
    private static final int OK = 200;
    private final HttpClient client;
    private final Deserializer deserializer;

    public Jdk(Deserializer deserializer) {
        this.client = HttpClient.newBuilder().connectTimeout(CONNECT).build();
        this.deserializer = deserializer;
    }

    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(READ)
            .build();
        try {
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != OK) {
                throw new UncheckedIOException(
                    new IOException("Service " + url + " answered " + response.statusCode()));
            }
            return Optional.ofNullable(deserializer.deserialize(response.body(), clazz));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot reach " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for " + url, e);
        }
    }
}
