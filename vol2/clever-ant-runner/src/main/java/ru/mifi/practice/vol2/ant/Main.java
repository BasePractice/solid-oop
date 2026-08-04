package ru.mifi.practice.vol2.ant;

import java.io.IOException;

/**
 * Запуск сравнения стратегий: все реализации подхватываются через ServiceLoader,
 * поэтому добавление нового муравья не требует правки этого класса.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        Engine engine = new Engine();
        engine.all();
    }
}
