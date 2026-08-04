package ru.mifi.practice.val3.cont.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.mifi.practice.val3.cont.Deserializer;
import ru.mifi.practice.val3.cont.Http;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Optional;

/**
 * Тот же контракт {@link Http}, но на OkHttp. Клиент подменяется целиком, а ни
 * бизнес-логика, ни сервисы об этом не знают — ради этого интерфейс и заведён.
 */
public final class Ok implements Http {
    private static final Duration CONNECT = Duration.ofSeconds(5);
    private static final Duration READ = Duration.ofSeconds(10);
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(CONNECT)
        .readTimeout(READ)
        .build();
    private final Deserializer deserializer;

    public Ok(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute();
             ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                throw new UncheckedIOException(
                    new IOException("Service " + url + " answered " + response.code()));
            }
            return Optional.ofNullable(deserializer.deserialize(body.string(), clazz));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot reach " + url, e);
        }
    }
}
