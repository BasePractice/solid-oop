package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;

import java.awt.*;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

abstract class AbstractDynamicEntity implements Entity.Dynamic {
    protected final Random random = new Random();
    private final UUID id;
    protected int xr = 6;
    protected int yr = 6;
    protected int x;
    protected int y;
    protected int z;
    protected int side;

    protected AbstractDynamicEntity(int x, int y, int z, int side) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int z() {
        return z;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public void tick() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRemoved() {
        return false;
    }

    @Override
    public boolean intersects(int x0, int y0, int x1, int y1) {
        return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1);
    }

    @Override
    public Point move() {
        return new Point(0, 0);
    }

    @Override
    public int health() {
        return 0;
    }

    public boolean move(Room room, int xa, int ya) {
        if (xa != 0 || ya != 0) {
            boolean stopped = true;
            if (xa != 0 && move2(room, xa, 0)) {
                stopped = false;
            }
            if (ya != 0 && move2(room, 0, ya)) {
                stopped = false;
            }
            if (!stopped) {
                int xt = x >> 4;
                int yt = y >> 4;
                room.getTile(xt, yt).steppedOn(room, xt, yt, this);
            }
            return !stopped;
        }
        return true;
    }

    protected boolean move2(Room room, int xa, int ya) {
        if (xa != 0 && ya != 0) throw new IllegalArgumentException("Move2 can only move along one axis at a time!");

        int xto0 = ((x) - xr) >> 4;
        int yto0 = ((y) - yr) >> 4;
        int xto1 = ((x) + xr) >> 4;
        int yto1 = ((y) + yr) >> 4;

        int xt0 = ((x + xa) - xr) >> 4;
        int yt0 = ((y + ya) - yr) >> 4;
        int xt1 = ((x + xa) + xr) >> 4;
        int yt1 = ((y + ya) + yr) >> 4;
        boolean blocked = false;
        for (int yt = yt0; yt <= yt1; yt++)
            for (int xt = xt0; xt <= xt1; xt++) {
                if (xt >= xto0 && xt <= xto1 && yt >= yto0 && yt <= yto1) continue;
                room.getTile(xt, yt).bumpedInto(room, xt, yt, this);
                if (!room.getTile(xt, yt).mayPass(room, xt, yt, this)) {
                    blocked = true;
                    return false;
                }
            }
        if (blocked) {
            return false;
        }

        Set<Entity> wasInside = room.getEntities(x - xr, y - yr, x + xr, y + yr);
        Set<Entity> isInside = room.getEntities(x + xa - xr, y + ya - yr, x + xa + xr, y + ya + yr);
        for (Entity e : isInside) {
            if (e == this) {
                continue;
            }
            e.touchedBy(this);
        }
        isInside.removeAll(wasInside);
        for (Entity e : isInside) {
            if (e == this) {
                continue;
            }
            if (e.blocks(this)) {
                return false;
            }
        }
        x += xa;
        y += ya;
        return true;
    }
}
