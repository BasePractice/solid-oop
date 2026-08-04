package ru.mifi.practice.entity;

import java.util.UUID;

/**
 * Общая часть неподвижных сущностей: место в комнате и размеры. Наследование здесь
 * показано намеренно — это итоговый модуль курса.
 *
 * <p>{@code intersects} обязателен: комната отбирает соседей именно им, и без него
 * мебель не попадала бы в выборку, а значит никого бы не задерживала.
 */
abstract class AbstractStaticEntity implements Static {
    protected final int x;
    protected final int y;
    protected final int z;
    protected final int width;
    protected final int height;
    protected final int depth;
    private final UUID id;

    protected AbstractStaticEntity(int x, int y, int z, int width, int height, int depth) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final int getZ() {
        return z;
    }

    @Override
    public final UUID id() {
        return id;
    }

    @Override
    public boolean intersects(int x0, int y0, int x1, int y1) {
        return x + width >= x0 && y + height >= y0 && x <= x1 && y <= y1;
    }
}
