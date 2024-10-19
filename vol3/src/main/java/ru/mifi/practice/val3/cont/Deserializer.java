package ru.mifi.practice.val3.cont;

import com.google.gson.Gson;

public interface Deserializer {
    <T> T deserialize(String text, Class<T> clazz);

    final class GsonDeserializer implements Deserializer {
        private final Gson gson = new Gson();

        public <T> T deserialize(String text, Class<T> clazz) {
            return gson.fromJson(text, clazz);
        }
    }
}
