package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Screen;

import java.util.UUID;

@SuppressWarnings("MethodName")
public interface Entity {

    UUID id();

    int getX();

    int getY();

    int getZ();

    default int getLightRadius() {
        return 0;
    }

    default void render(Screen screen) {
        //Ignore
    }

    default boolean use(Human player, int attackDir) {
        return false;
    }

    default void hurt(Human player, int attackDamage, int attackDir) {
        //Ignore
    }

    default boolean interact(Human player, Item activeItem, int attackDir) {
        return false;
    }

    default boolean intersects(int x0, int y0, int x1, int y1) {
        return false;
    }

    default void touchedBy(Entity entity) {
        //Ignore
    }

    default boolean blocks(Entity entity) {
        return false;
    }

    record Data(int index, Entity entity) {

    }

}
