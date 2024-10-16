package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

public interface Item extends Entity {
    boolean interact(Human player, Entity entity, int attackDir);

    void renderIcon(Screen screen, int x, int y);

    boolean interactOn(Tile tile, Room room, int xt, int yt, Human player, int attackDir);

    boolean isDepleted();

    boolean canAttack();

    int getAttackDamageBonus(Entity entity);

    interface FurnitureItem extends Item {
        void update(int x, int y);
    }

    interface LightItem extends Item {
        @Override
        int getLightRadius();
    }

    interface ToolItem extends Item {

    }
}
