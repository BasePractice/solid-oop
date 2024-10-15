package ru.mifi.practice.room;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.entity.EntityFactory;
import ru.mifi.practice.ui.Handler;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

final class Default implements Room {
    private final Entity.Human player;
    private final Buffer[] buffers;
    private final String name;
    private final int width;
    private final int height;
    private final Map<UUID, Entity.Data> entities = new HashMap<>();
    private final Set<Entity>[] entitiesInTiles;
    private byte[] tiles;
    private byte[] data;
    private int swapBuffer;

    private int xo;
    private int yo;
    private int ho;
    private int wo;

    public Default(String name, Handler input, int width, int height, Generator generator, EntityFactory factory) {
        this.name = name;
        this.width = width;
        this.height = height;
        Data generate = generator.generate(width, height, factory);
        this.buffers = new Buffer[2];
        this.buffers[0] = new Buffer(width, height, generate.tiles(), generate.data());
        this.buffers[1] = new Buffer(width, height, generate.tiles(), generate.data());
        this.swapBuffer = 0;
        this.tiles = buffers[swapBuffer].tiles;
        this.data = buffers[swapBuffer].datas;
        this.entitiesInTiles = buffers[swapBuffer].entitiesInTiles;
        this.swapBuffer = 1;
        this.player = factory.createPlayer(input, this);
        add(this.player);
        for (int i = 0; i < 100; i++) {
            add(factory.createFly(i * 2, 40, 5, this));
        }
    }

    public static Set<Entity> getEntities(Set<Entity>[] entitiesInTiles, int w, int h, int x0, int y0, int x1, int y1) {
        Set<Entity> result = new HashSet<>();
        int xt0 = (x0 >> 4) - 1;
        int yt0 = (y0 >> 4) - 1;
        int xt1 = (x1 >> 4) + 1;
        int yt1 = (y1 >> 4) + 1;
        for (int y = yt0; y <= yt1; y++) {
            for (int x = xt0; x <= xt1; x++) {
                if (x < 0 || y < 0 || x >= w || y >= h) continue;
                Set<Entity> entities = entitiesInTiles[x + y * w];
                for (Entity e : entities) {
                    if (e.intersects(x0, y0, x1, y1)) {
                        result.add(e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Entity.Human player() {
        return player;
    }

    void updateSwap() {
        int i = prevBuffer();
        buffers[swapBuffer].copy(buffers[i]);
    }

    @Override
    public boolean canRender() {
        return true;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    void swap() {
        this.tiles = buffers[swapBuffer].tiles;
        this.data = buffers[swapBuffer].datas;
        nextBuffer();
    }

    private void nextBuffer() {
        swapBuffer++;
        if (swapBuffer >= buffers.length) {
            swapBuffer = 0;
        }
    }

    private int prevBuffer() {
        return swapBuffer - 1 < 0 ? buffers.length - 1 : swapBuffer - 1;
    }

    void updateTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        buffers[swapBuffer].tiles[x + y * width] = t.id();
        updateData(x, y, dataVal);
    }

    void updateData(int x, int y, int value) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        buffers[swapBuffer].datas[x + y * width] = (byte) value;
    }

    void updateRemoveEntity(Entity entity) {
        int x = entity.x() >> 4;
        int y = entity.y() >> 4;
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        Entity.Data data = removeEntity(entity);
        if (data != null) {
            buffers[swapBuffer].entitiesInTiles[data.index()].remove(entity);
        }
    }

    void updateInsertEntity(Entity entity) {
//                entity.removed = false;
//                entity.init(this);

        int x = entity.x() >> 4;
        int y = entity.y() >> 4;

        if (x < 0 || y < 0 || x >= width || y >= height)
            return;
        int index = x + y * width;
        putEntity(entity, index);
        buffers[swapBuffer].entitiesInTiles[index].add(entity);
    }

    @Override
    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        xo = xScroll >> 4;
        yo = yScroll >> 4;
        wo = (screen.width() + 15) >> 4;
        ho = (screen.height() + 15) >> 4;
        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= ho + yo; y++) {
            for (int x = xo; x <= wo + xo; x++) {
                getTile(x, y).render(screen, this, x, y);
            }
        }
        screen.setOffset(0, 0);
    }

    @Override
    public void renderSprites(Screen screen, int xScroll, int yScroll) {
        final ArrayList<Entity> rowSprites = new ArrayList<>();
        xo = xScroll >> 4;
        yo = yScroll >> 4;
        wo = (screen.width() + 15) >> 4;
        ho = (screen.height() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        sortAndRender(screen, rowSprites);
        for (int y = yo; y <= ho + yo; y++) {
            for (int x = xo; x <= wo + xo; x++) {
                if (x < 0 || y < 0 || x >= this.width() || y >= this.height()) {
                    continue;
                }
                rowSprites.addAll(entitiesInTiles[x + y * this.width()]);
            }
            if (!rowSprites.isEmpty()) {
                sortAndRender(screen, rowSprites);
            }
            rowSprites.clear();
        }
        screen.setOffset(0, 0);
    }

    private void sortAndRender(Screen screen, ArrayList<Entity> rowSprites) {
        rowSprites.forEach(p -> p.render(screen));
    }

    @Override
    public void renderLight(Screen screen, int xScroll, int yScroll) {
        xo = xScroll >> 4;
        yo = yScroll >> 4;
        wo = (screen.width() + 15) >> 4;
        ho = (screen.height() + 15) >> 4;

        screen.setOffset(xScroll, yScroll);
        int r = 4;
        for (int y = yo - r; y <= ho + yo + r; y++) {
            for (int x = xo - r; x <= wo + xo + r; x++) {
                if (x < 0 || y < 0 || x >= this.width() || y >= this.height()) {
                    continue;
                }
                Set<Entity> entities = entitiesInTiles[x + y * this.width()];
                for (Entity e : entities) {
                    // e.render(screen);
                    int lr = e.getLightRadius();
                    if (lr > 0) {
                        screen.renderLight(e.x() - 1, e.y() - 4, lr * 8);
                    }
                }
                int lr = getTile(x, y).getLightRadius(this, x, y);
                if (lr > 0) {
                    screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
                }
            }
        }
        screen.setOffset(0, 0);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Set<Entity> getEntities(int x0, int y0, int x1, int y1) {
        return getEntities(entitiesInTiles, width, height, x0, y0, x1, y1);
    }

    @Override
    public void tick() {
        for (Entity.Data e : new HashMap<>(entities).values()) {
            if (e.entity() instanceof Entity.Dynamic entity) {
                int xto = entity.x() >> 4;
                int yto = entity.y() >> 4;
                int rxo = entity.x();
                int ryo = entity.y();

                entity.tick();
                if (entity.isRemoved()) {
                    entities.remove(entity.id());
                    removeEntity(xto, yto, entity);
//                    if (entity instanceof DynamicObjects.Player && ((Player) e.entity).die) {
//                        //
//                    }
                } else {
                    int xt = entity.x() >> 4;
                    int yt = entity.y() >> 4;
                    int rx = entity.x();
                    int ry = entity.y();

                    if (xto != xt || yto != yt) {
                        removeEntity(xto, yto, entity);
                        insertEntity(xt, yt, entity);
                    } else if ((rx != rxo || ry != ryo)) {
                        removeEntity(xto, yto, entity);
                        insertEntity(xt, yt, entity);
                    }
                }
            }
        }
    }

    @Override
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return Tile.ROCK;
        }
        byte id = tiles[x + y * width];
        Tile tile = Tile.TILES[id];
        return tile;
    }

    @Override
    public void setTile(int x, int y, Tile t, int dataVal) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        tiles[x + y * width] = t.id();
        setData(x, y, dataVal);
    }

    @Override
    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return 0;
        }
        return data[x + y * width()] & 0xff;
    }

    @Override
    public void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return;
        }
        data[x + y * width()] = (byte) val;
    }

    @Override
    public Meta meta() {
        return new Meta(xo, yo, wo, ho);
    }

    void putEntity(Entity e, int index) {
        entities.put(e.id(), new Entity.Data(index, e));
    }

    public void add(Entity entity) {
//        entity.removed = false;
//        entity.init(this);
        insertEntity(entity.x() >> 4, entity.y() >> 4, entity);
    }

    public void remove(Entity e) {
        int xto = e.x() >> 4;
        int yto = e.y() >> 4;
        removeEntity(xto, yto, e);
    }

    public void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= width() || y >= height)
            return;
        int index = x + y * width;
        putEntity(e, index);
        entitiesInTiles[index].add(e);
    }

    public void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        int i = x + y * width;
        Entity.Data data = removeEntity(e);
        if (data != null) {
            entitiesInTiles[data.index()].remove(e);
        }
    }

    Entity.Data removeEntity(Entity e) {
        return entities.get(e.id());
    }
}
