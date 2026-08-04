package ru.mifi.practice.ui;

import ru.mifi.practice.entity.Dynamic;
import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.entity.Human;
import ru.mifi.practice.entity.Item;
import ru.mifi.practice.room.Room;

import java.util.Map;
import java.util.Random;

/**
 * Клетка пола. Каждый вид сам знает, как себя рисовать и как стыковаться с соседями,
 * поэтому комната про траву и камень ничего не знает — она хранит только байтовые
 * идентификаторы.
 *
 * <p>Реестр собирается один раз явным списком, а не самозаписью из конструктора:
 * так порядок инициализации не влияет на результат и в нумерации не остаётся дыр,
 * на которых поиск клетки вернул бы {@code null}.
 */
public abstract class Tile {
    public static final Tile GRASS = new GrassTile(0);
    public static final Tile ROCK = new RockTile(1);
    public static final Tile WATER = new WaterTile(2);
    public static final Tile FLOWER = new FlowerTile(3);
    public static final Tile DIRT = new DirtTile(4);
    private static final Map<Byte, Tile> BY_ID = Map.of(
        GRASS.id(), GRASS,
        ROCK.id(), ROCK,
        WATER.id(), WATER,
        FLOWER.id(), FLOWER,
        DIRT.id(), DIRT);
    private final byte id;
    private final boolean connectsToGrass;
    private final boolean connectsToSand;
    private final boolean connectsToWater;

    protected Tile(int id, boolean connectsToGrass, boolean connectsToSand, boolean connectsToWater) {
        this.id = (byte) id;
        this.connectsToGrass = connectsToGrass;
        this.connectsToSand = connectsToSand;
        this.connectsToWater = connectsToWater;
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

    public final boolean connectsToGrass() {
        return connectsToGrass;
    }

    public final boolean connectsToSand() {
        return connectsToSand;
    }

    public final boolean connectsToWater() {
        return connectsToWater;
    }

    public abstract void render(Screen screen, Room room, int x, int y);

    //TODO: Взаимодействие инструментов с клетками — копать землю, рубить камень, косить траву.
    //      Не сделано сейчас: нужен инвентарь и типы инструментов, которых в модели ещё нет,
    //      поэтому пока любая клетка отвечает отказом.
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

    private static final class RockTile extends Tile {
        private RockTile(int id) {
            super(id, false, false, false);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            final int col = Color.get(444, 444, 333, 333);
            final int transition = Color.get(111, 444, 555, Room.DIRT_COLOR);
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

        @Override
        public void hurt(Room room, int xt, int yt, Dynamic entity, int damage, int attackDir) {
            room.setData(xt, yt, Math.max(room.getData(xt, yt) - damage, 0));
        }
    }

    private static final class DirtTile extends Tile {
        private DirtTile(int id) {
            super(id, false, false, false);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(Room.DIRT_COLOR, Room.DIRT_COLOR, Room.DIRT_COLOR - 111, Room.DIRT_COLOR - 111);
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

    private static sealed class GrassTile extends Tile permits FlowerTile {
        private GrassTile(int id) {
            super(id, true, false, false);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(Room.GRASS_COLOR, Room.GRASS_COLOR, Room.GRASS_COLOR + 111, Room.GRASS_COLOR + 111);
            int transition = Color.get(
                Room.GRASS_COLOR - 111, Room.GRASS_COLOR, Room.GRASS_COLOR + 111, Room.DIRT_COLOR);
            boolean u = !room.getTile(x, y - 1).connectsToGrass();
            boolean d = !room.getTile(x, y + 1).connectsToGrass();
            boolean l = !room.getTile(x - 1, y).connectsToGrass();
            boolean r = !room.getTile(x + 1, y).connectsToGrass();
            if (u || l) {
                screen.render(x * 16, y * 16, (l ? 11 : 12) + (u ? 0 : 1) * 32, transition, 0);
            } else {
                screen.render(x * 16, y * 16, 0, col, 0);
            }
            if (u || r) {
                screen.render(x * 16 + 8, y * 16, (r ? 13 : 12) + (u ? 0 : 1) * 32, transition, 0);
            } else {
                screen.render(x * 16 + 8, y * 16, 1, col, 0);
            }
            if (d || l) {
                screen.render(x * 16, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transition, 0);
            } else {
                screen.render(x * 16, y * 16 + 8, 2, col, 0);
            }
            if (d || r) {
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transition, 0);
            } else {
                screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
            }
        }

        @Override
        public boolean mayPass(Room room, int xt, int yt, Entity entity) {
            return true;
        }
    }

    private static final class FlowerTile extends GrassTile {
        private FlowerTile(int id) {
            super(id);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            super.render(screen, room, x, y);
            int flower = Color.get(10, Room.GRASS_COLOR, 555, 440);
            if (room.getData(x, y) / 16 % 2 == 0) {
                screen.render(x * 16, y * 16, 1 + 32, flower, 0);
                screen.render(x * 16 + 8, y * 16 + 8, 1 + 32, flower, 0);
            } else {
                screen.render(x * 16 + 8, y * 16, 1 + 32, flower, 0);
                screen.render(x * 16, y * 16 + 8, 1 + 32, flower, 0);
            }
        }

        @Override
        public void hurt(Room room, int xt, int yt, Dynamic entity, int damage, int attackDir) {
            room.setTile(xt, yt, Tile.GRASS, 0);
        }
    }

    private static final class WaterTile extends Tile {
        private final Random random = new Random();

        private WaterTile(int id) {
            super(id, false, true, true);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(5, 5, 115, 115);
            int toDirt = Color.get(3, 5, Room.DIRT_COLOR - 111, Room.DIRT_COLOR);
            int toGrass = Color.get(3, 5, Room.GRASS_COLOR - 110, Room.GRASS_COLOR);
            boolean u = !room.getTile(x, y - 1).connectsToWater();
            boolean d = !room.getTile(x, y + 1).connectsToWater();
            boolean l = !room.getTile(x - 1, y).connectsToWater();
            boolean r = !room.getTile(x + 1, y).connectsToWater();
            boolean su = u && room.getTile(x, y - 1).connectsToSand();
            boolean sd = d && room.getTile(x, y + 1).connectsToSand();
            boolean sl = l && room.getTile(x - 1, y).connectsToSand();
            boolean sr = r && room.getTile(x + 1, y).connectsToSand();
            if (u || l) {
                screen.render(x * 16, y * 16, (l ? 14 : 15) + (u ? 0 : 1) * 32, su || sl ? toGrass : toDirt, 0);
            } else {
                screen.render(x * 16, y * 16, random.nextInt(4), col, random.nextInt(4));
            }
            if (u || r) {
                screen.render(x * 16 + 8, y * 16, (r ? 16 : 15) + (u ? 0 : 1) * 32, su || sr ? toGrass : toDirt, 0);
            } else {
                screen.render(x * 16 + 8, y * 16, random.nextInt(4), col, random.nextInt(4));
            }
            if (d || l) {
                screen.render(x * 16, y * 16 + 8, (l ? 14 : 15) + (d ? 2 : 1) * 32, sd || sl ? toGrass : toDirt, 0);
            } else {
                screen.render(x * 16, y * 16 + 8, random.nextInt(4), col, random.nextInt(4));
            }
            if (d || r) {
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 16 : 15) + (d ? 2 : 1) * 32, sd || sr ? toGrass : toDirt, 0);
            } else {
                screen.render(x * 16 + 8, y * 16 + 8, random.nextInt(4), col, random.nextInt(4));
            }
        }
    }
}
