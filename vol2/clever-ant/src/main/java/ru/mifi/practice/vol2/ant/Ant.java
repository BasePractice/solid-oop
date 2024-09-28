package ru.mifi.practice.vol2.ant;

public interface Ant {

    String getName();

    Step step(Sensor sensor);

    enum Step {
        /**
         * Move forward and eat(if eat exists)
         * Движение вперед (относительно текущего положения и направления взгляда)
         * Если движение осуществляется на клетку которая является едой, еда съедается
         */
        FORWARD,
        /**
         * Spin left 90
         * Поворот направо на 90 градусов
         */
        SPIN_LEFT,
        /**
         * Spin right 90
         * Поворот направо на 90 градусов
         */
        SPIN_RIGHT,
        /**
         * Noop
         * Ничего не делаем
         */
        NOOP,
        /**
         * Завершение работы
         */
        STOP
    }

    interface Sensor {
        boolean hasFood();
    }
}
