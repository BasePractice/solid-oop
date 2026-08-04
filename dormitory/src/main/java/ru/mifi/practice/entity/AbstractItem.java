package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.util.UUID;

/**
 * Общая часть предметов: имя, иконка и вежливые отказы на всё, чего предмет не умеет.
 * Наследник переопределяет только то, ради чего он существует, — свеча светит,
 * тапок бьёт.
 *
 * <p>Координаты нулевые: предмет в руках своего места в комнате не занимает.
 * Это следствие того, что {@link Item} наследует {@link Entity}, — вопрос,
 * который стоит пересобрать, когда предметы начнут ещё и лежать на полу.
 */
abstract class AbstractItem implements Item {
    private final UUID id = UUID.randomUUID();
    private final String name;
    private final int color;

    protected AbstractItem(String name, int color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public final UUID id() {
        return id;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final int getX() {
        return 0;
    }

    @Override
    public final int getY() {
        return 0;
    }

    @Override
    public final int getZ() {
        return 0;
    }

    @Override
    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, 0, color, 0);
    }

    @Override
    public boolean interact(Human player, Entity entity, int attackDir) {
        return false;
    }

    @Override
    public boolean interactOn(Tile tile, Room room, int xt, int yt, Human player, int attackDir) {
        return false;
    }

    @Override
    public boolean isDepleted() {
        return false;
    }

    @Override
    public boolean canAttack() {
        return false;
    }

    @Override
    public boolean reaches(Entity entity) {
        return true;
    }

    @Override
    public int getAttackDamageBonus(Entity entity) {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
