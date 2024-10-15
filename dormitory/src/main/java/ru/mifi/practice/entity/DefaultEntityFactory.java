package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Handler;

final class DefaultEntityFactory implements EntityFactory {
    @Override
    public Entity.Human createPlayer(Handler input, Room root) {
        return new Player(input, root);
    }

    @Override
    public Entity createCube(int x, int y, int z, int side) {
        return new Cube(x, y, z, side);
    }

    @Override
    public Entity createFly(int x, int y, int z, Room room) {
        return new Fly(x, y, z, room);
    }
}
