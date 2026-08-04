package ru.mifi.practice.entity;

import java.awt.Point;

/**
 * Сущность, которая живёт во времени: двигается, получает урон и в какой-то момент
 * исчезает. Комната опрашивает такие сущности каждый такт.
 */
public interface Dynamic extends Entity {
    /**
     * Метод действия над объектом
     */
    void tick();

    boolean isRemoved();

    Point move();

    int health();
}
