package ru.mifi.practice.entity;

public interface Room {

    void tick();

    final class Default implements Room {
        private final int width;
        private final int height;

        private Default(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void tick() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
