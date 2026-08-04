package ru.mifi.practice.entity;

import java.awt.Point;
import java.util.UUID;

/**
 * Насекомое-фейк: отвечает только на вопрос «летаешь ли ты» и не тянет за собой
 * ни комнату, ни экран. Ровно этого хватает, чтобы проверять предметы.
 */
record FakeBug(boolean flying) implements Bug {

    @Override
    public UUID id() {
        return UUID.randomUUID();
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getZ() {
        return 0;
    }

    @Override
    public void tick() {
        //Фейк во времени не живёт
    }

    @Override
    public boolean isRemoved() {
        return false;
    }

    @Override
    public Point move() {
        return new Point(0, 0);
    }

    @Override
    public int health() {
        return 1;
    }
}
