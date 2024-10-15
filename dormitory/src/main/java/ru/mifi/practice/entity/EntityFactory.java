package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Handler;

import static ru.mifi.practice.entity.Entity.Human;

public interface EntityFactory {

    EntityFactory DEFAULT = new DefaultEntityFactory();

    Human createPlayer(Handler input, Room root);

    Entity createCube(int x, int y, int z, int side);

    Entity createFly(int x, int y, int z, Room room);
}
