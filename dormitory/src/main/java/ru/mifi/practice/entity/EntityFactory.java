package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Handler;

/**
 * Единственное место, где известны классы сущностей. Комната просит «муху» и получает
 * {@link Entity}, не зная, каким классом та реализована.
 */
public interface EntityFactory {

    EntityFactory DEFAULT = new DefaultEntityFactory();

    Human createPlayer(Handler input, Room root);

    Inventory createInventory();

    Entity createCube(int x, int y, int z, int side);

    Entity createFly(int x, int y, int z, Room room);
}
