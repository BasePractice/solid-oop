package ru.mifi.practice.room;

import org.junit.jupiter.api.Test;
import ru.mifi.practice.entity.EntityFactory;
import ru.mifi.practice.ui.Tile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

final class RoomGeneratorTest {

    @Test
    void doesNotLeaveRoomWithoutWalls() {
        Room.Data data = Room.DEFAULT_GENERATOR.generate(10, 10, EntityFactory.DEFAULT);
        assertThat(
            "corner of the room is not a wall, so the player walks straight out",
            data.tiles()[0],
            is(Tile.WALL.id())
        );
    }

    @Test
    void doesNotWallUpTheMiddle() {
        Room.Data data = Room.DEFAULT_GENERATOR.generate(10, 10, EntityFactory.DEFAULT);
        assertThat(
            "middle of the room is a wall, so there is nowhere to stand",
            data.tiles()[5 + 5 * 10],
            is(Tile.FLOOR.id())
        );
    }

    @Test
    void doesNotSealTheFarWall() {
        Room.Data data = Room.DEFAULT_GENERATOR.generate(10, 10, EntityFactory.DEFAULT);
        assertThat(
            "far edge is not a wall, so the room leaks on the opposite side",
            data.tiles()[9 + 9 * 10],
            is(Tile.WALL.id())
        );
    }

    @Test
    void doesNotLeaveRoomEmpty() {
        Room.Data data = Room.DEFAULT_GENERATOR.generate(24, 24, EntityFactory.DEFAULT);
        assertThat(
            "generated room holds no furniture, so it is a bare box",
            data.entities(),
            is(not(emptyIterable()))
        );
    }
}
