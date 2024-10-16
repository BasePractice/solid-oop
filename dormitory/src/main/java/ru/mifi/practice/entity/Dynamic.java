package ru.mifi.practice.entity;

import java.awt.Point;

public interface Dynamic extends Entity {
    /**
     * Метод действия над объектом
     */
    void tick();

    boolean isRemoved();

    Point move();

    int health();
}
