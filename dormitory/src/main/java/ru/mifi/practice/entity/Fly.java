package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Color;
import ru.mifi.practice.ui.Screen;

/**
 * Муха. Кружит вокруг источника света: раз в несколько тактов выбирает новую тягу —
 * сумму направления на свет и случайного отклонения, — а дальше летит по инерции
 * с трением. Из-за отклонения траектория не сходится в точку, а вьётся вокруг неё.
 *
 * <p>Тапком муху не достать вовсе — она в воздухе. Промах решается не здесь,
 * а в самом предмете: за это отвечает {@code Item.reaches}.
 *
 * <p>О стены муха отскакивает: летит она по координатам, минуя проверку клеток,
 * поэтому границы комнаты учитываются вручную.
 */
final class Fly extends AbstractDynamicEntity implements Bug {
    private static final float SPEED = 0.05f;
    private static final float FRICTION = 0.98f;
    private static final float TURN = 0.2f;
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
        wander(player.getLightRadius() * 2, player.getX(), player.getY());
    }

    @Override
    public void render(Screen screen) {
        screen.setPixel(x, y, Color.get(0));
    }

    @Override
    public boolean flying() {
        return true;
    }

    @Override
    public void hurt(Human player, int attackDamage, int attackDir) {
        this.health -= attackDamage;
    }

    private void wander(float radius, float x, float y) {
        if (random.nextFloat() < TURN) {
            Vector wobble = new Vector(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
            force = new Vector(x, y).subtract(position)
                .multiply(0.5f / radius)
                .add(wobble)
                .normalize()
                .multiply(SPEED);
        }
        speed = speed.add(force).multiply(FRICTION);
        Vector next = position.add(speed);
        Vector inside = clamp(next);
        if (inside.x() != next.x()) {
            speed = new Vector(-speed.x(), speed.y());
        }
        if (inside.y() != next.y()) {
            speed = new Vector(speed.x(), -speed.y());
        }
        position = inside;
        this.x = (int) position.x();
        this.y = (int) position.y();
    }

    /**
     * Загоняет точку внутрь стен комнаты. Муха ходит не через {@code move2},
     * а прямо по координатам, поэтому стены для неё приходится учитывать здесь.
     */
    private Vector clamp(Vector point) {
        float low = Room.CELL;
        return new Vector(
            Math.min(Math.max(point.x(), low), (room.width() - 1) * Room.CELL - 1),
            Math.min(Math.max(point.y(), low), (room.height() - 1) * Room.CELL - 1));
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
