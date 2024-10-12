package ru.mifi.practice.ui;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.room.Room;

public abstract class Tile {
    public static Tile[] TILES = new Tile[256];
    public static Tile ROCK = new Rock((byte) 1);
    private final byte id;

    protected Tile(int id) {
        this.id = (byte) id;
        if (TILES[id] != null) {
            throw new RuntimeException("Duplicate tile ids!");
        }
        TILES[id] = this;
    }

    public byte id() {
        return id;
    }

    public abstract void render(Screen screen, Room room, int x, int y);

    public abstract int getLightRadius(Room room, int x, int y);

    static final class Rock extends Tile {

        private Rock(int id) {
            super(id);
        }

        @Override
        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(444, 444, 333, 333);
            int transitionColor = Color.get(111, 444, 555, Room.DIRT_COLOR);

            boolean u = room.getTile(x, y - 1) != this;
            boolean d = room.getTile(x, y + 1) != this;
            boolean l = room.getTile(x - 1, y) != this;
            boolean r = room.getTile(x + 1, y) != this;

            boolean ul = room.getTile(x - 1, y - 1) != this;
            boolean dl = room.getTile(x - 1, y + 1) != this;
            boolean ur = room.getTile(x + 1, y - 1) != this;
            boolean dr = room.getTile(x + 1, y + 1) != this;

            if (!u && !l) {
                if (!ul)
                    screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
                else
                    screen.render(x * 16 + 0, y * 16 + 0, 7 + 0 * 32, transitionColor, 3);
            } else
                screen.render(x * 16 + 0, y * 16 + 0, (l ? 6 : 5) + (u ? 2 : 1) * 32, transitionColor, 3);

            if (!u && !r) {
                if (!ur)
                    screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
                else
                    screen.render(x * 16 + 8, y * 16 + 0, 8 + 0 * 32, transitionColor, 3);
            } else
                screen.render(x * 16 + 8, y * 16 + 0, (r ? 4 : 5) + (u ? 2 : 1) * 32, transitionColor, 3);

            if (!d && !l) {
                if (!dl)
                    screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
                else
                    screen.render(x * 16 + 0, y * 16 + 8, 7 + 1 * 32, transitionColor, 3);
            } else
                screen.render(x * 16 + 0, y * 16 + 8, (l ? 6 : 5) + (d ? 0 : 1) * 32, transitionColor, 3);
            if (!d && !r) {
                if (!dr)
                    screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
                else
                    screen.render(x * 16 + 8, y * 16 + 8, 8 + 1 * 32, transitionColor, 3);
            } else
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 4 : 5) + (d ? 0 : 1) * 32, transitionColor, 3);
        }

        @Override
        public int getLightRadius(Room room, int x, int y) {
            return 0;
        }

        public boolean mayPass(Room room, int x, int y, Entity e) {
            return false;
        }

//        public void hurt(Model.Room room, int x, int y, Mob source, int dmg, int attackDir) {
//            hurt(room, x, y, dmg);
//        }

//        public boolean interact(Model.Room room, int xt, int yt, Player player, Item item, int attackDir) {
//            if (item instanceof ToolItem) {
//                ToolItem tool = (ToolItem) item;
//                if (tool.type == ToolType.pickaxe) {
//                    if (player.payStamina(4 - tool.level)) {
//                        hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }

        public void hurt(Room room, int x, int y, int dmg) {
//            int damage = room.getData(x, y) + dmg;
//            level.add(new SmashParticle(level.sound, x * 16 + 8, y * 16 + 8));
//            level.add(new TextParticle(level.sound, "" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
//            if (damage >= 50) {
//                int count = random.nextInt(4) + 1;
//                for (int i = 0; i < count; i++) {
//                    room.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
//                        new ResourceItem(Resource.stone), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
//                }
//                count = random.nextInt(2);
//                for (int i = 0; i < count; i++) {
//                    level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
//                        new ResourceItem(Resource.coal), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
//                }
//                room.setTile(x, y, Tile.dirt, 0);
//            } else {
//                room.setData(x, y, damage);
//            }
        }

        public void tick(Room room, int xt, int yt) {
            int damage = room.getData(xt, yt);
            if (damage > 0) {
                room.setData(xt, yt, damage - 1);
            }
        }
    }
}
