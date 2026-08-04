package ru.mifi.practice.vol6.transport;

import java.io.PrintStream;

/**
 * Приёмник вывода. Обычный и ошибочный потоки разделены, чтобы сообщения об ошибках
 * не смешивались с меню.
 */
public interface Output {

    static Output standard() {
        return new Standard();
    }

    void print(String format, Object... args);

    default void println(String format, Object... args) {
        print(format + "%n", args);
    }

    void error(String format, Object... args);

    default void errorln(String format, Object... args) {
        error(format + "%n", args);
    }

    final class Standard implements Output {
        private final PrintStream output;
        private final PrintStream error;

        private Standard() {
            output = System.out;
            error = System.err;
        }

        @Override
        public void print(String format, Object... args) {
            output.printf(format, args);
            output.flush();
        }

        @Override
        public void error(String format, Object... args) {
            error.printf(format, args);
            error.flush();
        }
    }
}
