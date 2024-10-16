package ru.mifi.practice.entity;

import java.util.Random;
import java.util.UUID;

abstract class AbstractStaticEntity implements Static {
    protected final Random random = new Random();
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
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public UUID id() {
        return id;
    }
}
