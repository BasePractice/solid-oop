package ru.mifi.practice.room;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.entity.EntityFactory;
import ru.mifi.practice.entity.Human;
import ru.mifi.practice.ui.Handler;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Комната: карта клеток, их данные и населяющие её сущности. Хранит клетки байтами,
 * а не объектами, поэтому копия карты стоит одного {@code System.arraycopy}.
 */
public interface Room {
    int FLOOR_COLOR = 322;
    int SIDE = 24;
    int CELL = 16;
    Generator DEFAULT_GENERATOR = (width, height, factory) -> {
        byte[] tiles = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean edge = x == 0 || y == 0 || x == width - 1 || y == height - 1;
                tiles[x + y * width] = edge ? Tile.WALL.id() : Tile.FLOOR.id();
            }
        }
        return new Data(width, height, tiles, new byte[width * height], List.of(
            factory.createTable(7 * CELL, 4 * CELL),
            factory.createChair(6 * CELL, 6 * CELL),
            factory.createChair(9 * CELL, 6 * CELL),
            factory.createWardrobe(2 * CELL, 2 * CELL),
            factory.createWardrobe(18 * CELL, 17 * CELL)
        ));
    };
    Factory DEFAULT_FACTORY = (name, input) ->
        new Default(name, input, SIDE, SIDE, DEFAULT_GENERATOR, EntityFactory.DEFAULT);

    Human player();

    int width();

    int height();

    void renderBackground(Screen screen, int xScroll, int yScroll);

    void renderSprites(Screen screen, int xScroll, int yScroll);

    void renderLight(Screen lightScreen, int xScroll, int yScroll);

    Tile getTile(int x, int y);

    void setTile(int x, int y, Tile t, int dataVal);

    int getData(int x, int y);

    void setData(int x, int y, int val);

    Meta meta();

    String name();

    Set<Entity> getEntities(int x0, int y0, int x1, int y1);

    void tick();

    interface Factory {

        Room create(String name, Handler input);
    }

    @FunctionalInterface
    interface Generator {
        Data generate(int width, int height, EntityFactory factory);
    }

    final class Buffer {
        final byte[] tiles;
        final byte[] datas;
        final List<Set<Entity>> entitiesInTiles;

        Buffer(int width, int height, byte[] tiles, byte[] data) {
            this.tiles = tiles.clone();
            this.datas = data.clone();
            this.entitiesInTiles = createEntities(width, height);
        }

        private static List<Set<Entity>> createEntities(int width, int height) {
            List<Set<Entity>> result = new ArrayList<>(width * height);
            for (int i = 0; i < width * height; i++) {
                result.add(new HashSet<>());
            }
            return result;
        }
    }

    record Meta(int xo, int yo, int ho, int wo) {
    }

    /**
     * Сгенерированная карта: клетки, их данные и обстановка. Массивы копируются
     * на входе и на выходе — record обещает неизменяемость, и без копий это
     * обещание было бы ложным.
     *
     * <p>Насекомых здесь нет: их конструктор требует уже созданную комнату,
     * а комнаты в момент генерации ещё не существует.
     */
    record Data(int width, int height, byte[] tiles, byte[] data, List<Entity> entities) {
        public Data {
            tiles = tiles.clone();
            data = data.clone();
            entities = List.copyOf(entities);
        }

        @Override
        public byte[] tiles() {
            return tiles.clone();
        }

        @Override
        public byte[] data() {
            return data.clone();
        }
    }

}
