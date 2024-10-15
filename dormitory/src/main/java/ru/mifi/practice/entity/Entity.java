package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.awt.*;
import java.util.UUID;

public interface Entity {

    UUID id();

    int x();

    int y();

    int z();

    default int getLightRadius() {
        return 0;
    }

    default void render(Screen screen) {

    }

    default boolean use(Human player, int attackDir) {
        return false;
    }

    default void hurt(Human player, int attackDamage, int attackDir) {

    }

    default boolean interact(Human player, Item activeItem, int attackDir) {
        return false;
    }

    default boolean intersects(int x0, int y0, int x1, int y1) {
        return false;
    }

    default void touchedBy(Entity entity) {

    }

    default boolean blocks(Entity entity) {
        return false;
    }

    interface Item extends Entity {
        boolean interact(Human player, Entity entity, int attackDir);

        void renderIcon(Screen screen, int x, int y);

        boolean interactOn(Tile tile, Room room, int xt, int yt, Human player, int attackDir);

        boolean isDepleted();

        boolean canAttack();

        int getAttackDamageBonus(Entity entity);
    }

    interface Static extends Entity {

    }

    interface Dynamic extends Entity {
        /**
         * Метод действия над объектом
         */
        void tick();

        boolean isRemoved();

        Point move();

        int health();
    }

    interface Bug extends Dynamic {

    }

    sealed interface Human extends Dynamic permits Player {
        int stamina();

        int staminaRechargeDelay();

        State state();

        enum State {
            STAY,
            WALK,
            ATCK
        }
    }

    record Data(int index, Entity entity) {

    }

}
