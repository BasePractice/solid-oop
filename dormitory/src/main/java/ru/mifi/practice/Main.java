package ru.mifi.practice;

import ru.mifi.practice.ui.Model;

/**
 * Запуск общаги. Флаг включает отладочную панель поверх кадра — счётчики тиков,
 * координаты игрока и границы отрисовки.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Model.start(true);
    }
}
