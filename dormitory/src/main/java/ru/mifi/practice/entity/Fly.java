package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Color;
import ru.mifi.practice.ui.Screen;

final class Fly extends AbstractDynamicEntity implements Entity.Bug {
    private final Room room;
    private final float speed = 0.05f;
    private final float friction = 0.98f;
    private Vector p = new Vector(0, 0);
    private Vector s = new Vector(0, 0);
    private Vector f = new Vector(0, 0);

    Fly(int x, int y, int z, Room room) {
        super(x, y, z, 1);
        this.room = room;
    }

    @Override
    public void tick() {
        Human player = room.player();
        int radius = player.getLightRadius() * 2;
        flying(radius, player.x(), player.y());
    }

    @Override
    public void render(Screen screen) {
        screen.setPixel(x, y, Color.get(0));
    }

    private void flying(float radius, float x, float y) {
        if (random.nextFloat() < 0.2f) {
            Vector center = new Vector(x, y);
            Vector v = new Vector(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
            f = center.subtract(p).multiply(0.5f / radius).add(v).normalize().multiply(speed);
        }
        s = s.add(f).multiply(friction);
        p = p.add(s);

        this.x = (int) p.x;
        this.y = (int) p.y;
    }

    private record Vector(float x, float y) {

        Vector add(Vector vector) {
            return new Vector(x + vector.x, y + vector.y);
        }

        Vector subtract(Vector vector) {
            return new Vector(x - vector.x, y - vector.y);
        }

        Vector multiply(float factor) {
            return new Vector(x * factor, y * factor);
        }

        float length() {
            return (float) Math.sqrt(x * x + y * y);
        }

        Vector normalize() {
            return new Vector(x / length(), y / length());
        }
    }
}
