package ru.mifi.practice.ui;

import ru.mifi.practice.entity.Dynamic;
import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.entity.Human;
import ru.mifi.practice.entity.Item;
import ru.mifi.practice.room.Room;

import java.util.Map;

/**
 * Клетка пола. Каждый вид сам знает, как себя рисовать и как стыковаться с соседями,
 * поэтому комната про стены и доски ничего не знает — она хранит только байтовые
 * идентификаторы.
 *
 * <p>Реестр собирается один раз явным списком, а не самозаписью из конструктора:
 * так порядок инициализации не влияет на результат и в нумерации не остаётся дыр,
 * на которых поиск клетки вернул бы {@code null}.
 */
public abstract class Tile {
    public static final Tile WALL = new WallTile(0);
    public static final Tile FLOOR = new FloorTile(1);
    private static final Map<Byte, Tile> BY_ID = Map.of(
        WALL.id(), WALL,
        FLOOR.id(), FLOOR);
    private final byte id;

    protected Tile(int id) {
        this.id = (byte) id;
    }

    public static Tile byId(byte id) {
        Tile tile = BY_ID.get(id);
        if (tile == null) {
            throw new IllegalArgumentException("Unknown tile id " + id);
        }
        return tile;
    }

    public final byte id() {
        return id;
    }

    public abstract void render(Screen screen, Room room, int x, int y);

    //TODO: Взаимодействие инструментов с клетками — отодрать обои, приподнять половицу.
    //      Не сделано сейчас: нужен инвентарь с инструментами, а тапок и мухобойка
    //      с клетками не работают, поэтому пока любая клетка отвечает отказом.
    public boolean interact(Room room, int xt, int yt, Human player, Item item, int attackDir) {
        return false;
    }

    public boolean use(Room room, int xt, int yt, Human player, int attackDir) {
        return false;
    }

    public int getLightRadius(Room room, int x, int y) {
        return 0;
    }

    public void hurt(Room room, int xt, int yt, Dynamic entity, int damage, int attackDir) {
        //Обычная клетка урона не замечает — переопределяют только разрушаемые
    }

    public void steppedOn(Room room, int xt, int yt, Entity entity) {
        //Обычная клетка на шаги не реагирует
    }

    public void bumpedInto(Room room, int xt, int yt, Entity entity) {
        //Обычная клетка на столкновения не реагирует
    }

    public boolean mayPass(Room room, int xt, int yt, Entity entity) {
        return false;
    }

    /**
     * Стена. Рисуется по соседям: если сверху и слева тоже стена, угол сплошной,
     * иначе кладётся стык. Из-за этого сплошная кладка выглядит кладкой, а не
     * набором отдельных кубиков.
     */
    private static final class WallTile extends Tile {
        private WallTile(int id) {
            super(id);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            final int col = Color.get(444, 444, 333, 333);
            final int transition = Color.get(111, 444, 555, Room.FLOOR_COLOR);
            final boolean u = room.getTile(x, y - 1) != this;
            final boolean d = room.getTile(x, y + 1) != this;
            final boolean l = room.getTile(x - 1, y) != this;
            final boolean r = room.getTile(x + 1, y) != this;
            final boolean ul = room.getTile(x - 1, y - 1) != this;
            final boolean dl = room.getTile(x - 1, y + 1) != this;
            final boolean ur = room.getTile(x + 1, y - 1) != this;
            final boolean dr = room.getTile(x + 1, y + 1) != this;
            if (u || l) {
                screen.render(x * 16, y * 16, (l ? 6 : 5) + (u ? 2 : 1) * 32, transition, 3);
            } else {
                screen.render(x * 16, y * 16, ul ? 7 : 0, ul ? transition : col, ul ? 3 : 0);
            }
            if (u || r) {
                screen.render(x * 16 + 8, y * 16, (r ? 4 : 5) + (u ? 2 : 1) * 32, transition, 3);
            } else {
                screen.render(x * 16 + 8, y * 16, ur ? 8 : 1, ur ? transition : col, ur ? 3 : 0);
            }
            if (d || l) {
                screen.render(x * 16, y * 16 + 8, (l ? 6 : 5) + (d ? 0 : 1) * 32, transition, 3);
            } else {
                screen.render(x * 16, y * 16 + 8, dl ? 7 + 32 : 2, dl ? transition : col, dl ? 3 : 0);
            }
            if (d || r) {
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 4 : 5) + (d ? 0 : 1) * 32, transition, 3);
            } else {
                screen.render(x * 16 + 8, y * 16 + 8, dr ? 8 + 32 : 3, dr ? transition : col, dr ? 3 : 0);
            }
        }
    }

    /**
     * Дощатый пол. Ходить можно, светить нечем, ломать нечего.
     */
    private static final class FloorTile extends Tile {
        private FloorTile(int id) {
            super(id);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(
                Room.FLOOR_COLOR, Room.FLOOR_COLOR, Room.FLOOR_COLOR - 111, Room.FLOOR_COLOR - 111);
            screen.render(x * 16, y * 16, 0, col, 0);
            screen.render(x * 16 + 8, y * 16, 1, col, 0);
            screen.render(x * 16, y * 16 + 8, 2, col, 0);
            screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
        }

        @Override
        public boolean mayPass(Room room, int xt, int yt, Entity entity) {
            return true;
        }
    }
}
