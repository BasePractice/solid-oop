package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Color;
import ru.mifi.practice.ui.Screen;

/**
 * Муха. Кружит вокруг источника света: раз в несколько тактов выбирает новую тягу —
 * сумму направления на свет и случайного отклонения, — а дальше летит по инерции
 * с трением. Из-за отклонения траектория не сходится в точку, а вьётся вокруг неё.
 *
 * <p>Тапком муху почти не достать: удар засчитывается лишь в десятой части случаев,
 * для неё нужна мухобойка.
 */
final class Fly extends AbstractDynamicEntity implements Bug {
    private static final float SPEED = 0.05f;
    private static final float FRICTION = 0.98f;
    private static final float TURN = 0.2f;
    private static final float HIT = 0.1f;
    private static final int HEALTH = 20;
    private final Room room;
    private Vector position;
    private Vector speed = new Vector(0, 0);
    private Vector force = new Vector(0, 0);

    Fly(int x, int y, int z, Room room) {
        super(x, y, z, 1);
        this.room = room;
        this.position = new Vector(x, y);
        this.health = HEALTH;
    }

    @Override
    public void tick() {
        Human player = room.player();
        flying(player.getLightRadius() * 2, player.getX(), player.getY());
    }

    @Override
    public void render(Screen screen) {
        screen.setPixel(x, y, Color.get(0));
    }

    @Override
    public void hurt(Human player, int attackDamage, int attackDir) {
        if (random.nextFloat() < HIT) {
            this.health -= attackDamage;
        }
    }

    private void flying(float radius, float x, float y) {
        if (random.nextFloat() < TURN) {
            Vector wobble = new Vector(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
            force = new Vector(x, y).subtract(position)
                .multiply(0.5f / radius)
                .add(wobble)
                .normalize()
                .multiply(SPEED);
        }
        speed = speed.add(force).multiply(FRICTION);
        position = position.add(speed);
        this.x = (int) position.x();
        this.y = (int) position.y();
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
            float length = length();
            if (length == 0) {
                return this;
            }
            return new Vector(x / length, y / length);
        }
    }
}
