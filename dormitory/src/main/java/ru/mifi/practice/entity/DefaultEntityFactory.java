package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Color;
import ru.mifi.practice.ui.Handler;

import java.util.List;

/**
 * Фабрика по умолчанию. Классы сущностей пакетно-приватные, поэтому подменить их
 * можно только вместе с фабрикой — снаружи они не видны.
 */
final class DefaultEntityFactory implements EntityFactory {
    @Override
    public Human createPlayer(Handler input, Room root) {
        return new Player(input, root, createInventory());
    }

    @Override
    public Inventory createInventory() {
        return new Inventory(List.of(new Candle(), new Slipper(), new Swatter()));
    }

    @Override
    public Entity createTable(int x, int y) {
        return new Furniture("стол", x, y, 24, 16, Color.get(-1, 210, 320, 431));
    }

    @Override
    public Entity createChair(int x, int y) {
        return new Furniture("стул", x, y, 12, 12, Color.get(-1, 210, 321, 432));
    }

    @Override
    public Entity createWardrobe(int x, int y) {
        return new Furniture("шкаф", x, y, 16, 24, Color.get(-1, 100, 210, 321));
    }

    @Override
    public Entity createFly(int x, int y, int z, Room room) {
        return new Fly(x, y, z, room);
    }
}
