package ru.mifi.practice.room;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.ui.Handler;
import ru.mifi.practice.ui.Player;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.util.HashSet;
import java.util.Set;

public interface Room {
    int DIRT_COLOR = 322;
    Factory DEFAULT_FACTORY = (name, input) -> new R00m("r00m", 128, 128, input, new byte[0], new byte[0]);

    Player player();

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

    interface Factory {

        Room create(String name, Handler input);
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

}
