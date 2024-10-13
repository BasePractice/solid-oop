package ru.mifi.practice.room;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.ui.Handler;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.util.HashSet;
import java.util.Set;

public interface Room {
    int DIRT_COLOR = 322;
    int GRASS_COLOR = 141;
    Generator DEFAULT_GENERATOR = (width, height) -> {
        Data data = new Data(width, height, new byte[width * height], new byte[width * height]);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data.tiles[x * y] = Tile.GRASS.id();
            }
        }
        return data;
    };
    Factory DEFAULT_FACTORY = (name, input) ->
        new Default("r00m", input, DEFAULT_GENERATOR.generate(24, 24));

    Entity.Human player();

    boolean canRender();

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

    interface Factory {

        Room create(String name, Handler input);
    }

    @FunctionalInterface
    interface Generator {
        Data generate(int width, int height);
    }

    final class Buffer {
        final byte[] tiles;
        final byte[] datas;
        final Set<Entity>[] entitiesInTiles;

        Buffer(int width, int height, byte[] tiles, byte[] data) {
            this.tiles = new byte[tiles.length];
            System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);
            this.datas = new byte[data.length];
            System.arraycopy(data, 0, this.datas, 0, data.length);
            this.entitiesInTiles = createEntities(width, height);
        }

        private static Set<Entity>[] createEntities(int width, int height) {
            Set<Entity>[] result = new Set[width * height];
            for (int i = 0; i < width * height; i++) {
                result[i] = new HashSet<>();
            }
            return result;
        }

        void copy(Buffer buffer) {
            System.arraycopy(buffer.tiles, 0, this.tiles, 0, tiles.length);
            System.arraycopy(buffer.datas, 0, this.datas, 0, datas.length);
            for (int i = 0; i < buffer.entitiesInTiles.length; i++) {
                this.entitiesInTiles[i] = new HashSet<>(buffer.entitiesInTiles[i]);
            }
        }
    }

    record Meta(int xo, int yo, int ho, int wo) {
    }

    record Data(int width, int height, byte[] tiles, byte[] data) {

    }

}
