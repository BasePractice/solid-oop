package ru.mifi.practice.val3.cont;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public interface Http {
    <T> Optional<T> get(String url, Class<T> clazz);

    String urlencode(String url);

    final class Ok implements Http {
        private final OkHttpClient client = new OkHttpClient.Builder().build();
        private final Deserializer deserializer;

        public Ok(Deserializer deserializer) {
            this.deserializer = deserializer;
        }

        @Override
        public <T> Optional<T> get(String url, Class<T> clazz) {
            Request request = new Request.Builder().url(url).get().build();
            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    try (ResponseBody body = responseBody) {
                        String content = body.string();
                        return Optional.of(deserializer.deserialize(content, clazz));
                    }
                } else {
                    System.err.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }

        @Override
        public String urlencode(String url) {
            return Objects.requireNonNull(HttpUrl.parse(url)).toString();
        }
    }
}
