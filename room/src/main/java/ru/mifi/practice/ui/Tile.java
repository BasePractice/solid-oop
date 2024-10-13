package ru.mifi.practice.ui;

import ru.mifi.practice.entity.Entity;
import ru.mifi.practice.room.Room;

import java.util.Random;

public abstract class Tile {
    public static Tile[] TILES = new Tile[256];
    public static Tile GRASS = new GrassTile((byte) 0);
    public static Tile ROCK = new RockTile((byte) 1);
    public static Tile WATER = new WaterTile((byte) 2);
    public static Tile FLOWER = new FlowerTile((byte) 3);
    public static Tile DIRT = new DirtTile((byte) 5);
    private final byte id;
    protected boolean connectsToGrass = false;
    protected boolean connectsToSand = false;
    protected boolean connectsToLava = false;
    protected boolean connectsToWater = false;

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

    public boolean interact(Room room, int xt, int yt, Entity.Human player, Entity.Item item, int attackDir) {
        return false;
    }

    public boolean use(Room room, int xt, int yt, Entity.Human player, int attackDir) {
        return false;
    }

    public abstract void render(Screen screen, Room room, int x, int y);

    public int getLightRadius(Room room, int x, int y) {
        return 0;
    }

    public void hurt(Room room, int xt, int yt, Entity.Dynamic entity, int i, int attackDir) {

    }

    public void steppedOn(Room room, int xt, int yt, Entity entity) {

    }

    public void bumpedInto(Room room, int xt, int yt, Entity entity) {

    }

    public boolean mayPass(Room room, int xt, int yt, Entity entity) {
        return false;
    }

    static final class RockTile extends Tile {

        private RockTile(int id) {
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

    static final class DirtTile extends Tile {
        public DirtTile(int id) {
            super(id);
        }

        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(Room.DIRT_COLOR, Room.DIRT_COLOR, Room.DIRT_COLOR - 111, Room.DIRT_COLOR - 111);
            screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
            screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
            screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
            screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
        }

        public boolean interact(Room level, int xt, int yt, Entity.Human player, Entity.Item item, int attackDir) {
//            if (item instanceof ToolItem) {
//                ToolItem tool = (ToolItem) item;
//                if (tool.type == ToolType.shovel) {
//                    if (player.payStamina(4 - tool.level)) {
//                        level.setTile(xt, yt, Tile.hole, 0);
//                        level.add(new ItemEntity(level.sound, level.playerHandler(), level.propertyReader(),
//                            new ResourceItem(Resource.dirt),
//                            xt * 16 + random.nextInt(10) + 3,
//                            yt * 16 + random.nextInt(10) + 3));
//                        level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
//                        return true;
//                    }
//                }
//                if (tool.type == ToolType.hoe) {
//                    if (player.payStamina(4 - tool.level)) {
//                        level.setTile(xt, yt, Tile.farmland, 0);
//                        level.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
//                        return true;
//                    }
//                }
//            }
            return false;
        }
    }

    private static class GrassTile extends Tile {
        public GrassTile(int id) {
            super(id);
            connectsToGrass = true;
        }

        public void render(Screen screen, Room room, int x, int y) {
            int col = Color.get(Room.GRASS_COLOR, Room.GRASS_COLOR, Room.GRASS_COLOR + 111, Room.GRASS_COLOR + 111);
            int transitionColor = Color.get(Room.GRASS_COLOR - 111, Room.GRASS_COLOR, Room.GRASS_COLOR + 111, Room.DIRT_COLOR);

            boolean u = !room.getTile(x, y - 1).connectsToGrass;
            boolean d = !room.getTile(x, y + 1).connectsToGrass;
            boolean l = !room.getTile(x - 1, y).connectsToGrass;
            boolean r = !room.getTile(x + 1, y).connectsToGrass;

            if (!u && !l) {
                screen.render(x * 16 + 0, y * 16 + 0, 0, col, 0);
            } else {
                screen.render(x * 16 + 0, y * 16 + 0, (l ? 11 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);
            }

            if (!u && !r) {
                screen.render(x * 16 + 8, y * 16 + 0, 1, col, 0);
            } else
                screen.render(x * 16 + 8, y * 16 + 0, (r ? 13 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);

            if (!d && !l) {
                screen.render(x * 16 + 0, y * 16 + 8, 2, col, 0);
            } else {
                screen.render(x * 16 + 0, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
            }
            if (!d && !r) {
                screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
            } else {
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
            }
        }

        public boolean interact(Room room, int xt, int yt, Entity.Human player, Entity.Item item, int attackDir) {
//            if (item instanceof ToolItem) {
//                ToolItem tool = (ToolItem) item;
//                if (tool.type == ToolType.shovel) {
//                    if (player.payStamina(4 - tool.level)) {
//                        room.setTile(xt, yt, Tile.dirt, 0);
//                        room.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
//                        if (random.nextInt(5) == 0) {
//                            room.add(new ItemEntity(level.sound, player.handler, player.property,
//                                new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3,
//                                yt * 16 + random.nextInt(10) + 3));
//                            return true;
//                        }
//                    }
//                }
//                if (tool.type == ToolType.hoe) {
//                    if (player.payStamina(4 - tool.level)) {
//                        room.sound.play(xt, yt, Sound.Type.MONSTER_HURT);
//                        if (random.nextInt(5) == 0) {
//                            room.add(new ItemEntity(level.sound, player.handler, player.property,
//                                new ResourceItem(Resource.seeds), xt * 16 + random.nextInt(10) + 3,
//                                yt * 16 + random.nextInt(10) + 3));
//                            return true;
//                        }
//                        room.setTile(xt, yt, Tile.farmland, 0);
//                        return true;
//                    }
//                }
//            }
            return false;

        }
    }

    private static final class FlowerTile extends GrassTile {
        public FlowerTile(int id) {
            super(id);
            TILES[id] = this;
            connectsToGrass = true;
        }

        public void render(Screen screen, Room room, int x, int y) {
            super.render(screen, room, x, y);

            int data = room.getData(x, y);
            int shape = (data / 16) % 2;
            int flowerCol = Color.get(10, Room.GRASS_COLOR, 555, 440);

            if (shape == 0) screen.render(x * 16 + 0, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
            if (shape == 1) screen.render(x * 16 + 8, y * 16 + 0, 1 + 1 * 32, flowerCol, 0);
            if (shape == 1) screen.render(x * 16 + 0, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
            if (shape == 0) screen.render(x * 16 + 8, y * 16 + 8, 1 + 1 * 32, flowerCol, 0);
        }

        public boolean interact(Room room, int x, int y, Entity.Human player, Entity.Item item, int attackDir) {
//            if (item instanceof ToolItem) {
//                ToolItem tool = (ToolItem) item;
//                if (tool.type == ToolType.shovel) {
//                    if (player.payStamina(4 - tool.level)) {
//                        room.add(new ItemEntity(level.sound, player.handler, player.property, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
//                        room.add(new ItemEntity(level.sound, player.handler, player.property, new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
//                        room.setTile(x, y, Tile.grass, 0);
//                        return true;
//                    }
//                }
//            }
            return false;
        }

        public void hurt(Room room, int x, int y, Entity.Dynamic source, int dmg, int attackDir) {
//            int count = random.nextInt(2) + 1;
//            for (int i = 0; i < count; i++) {
//                level.add(new ItemEntity(level.sound,
//                    level.playerHandler(), level.propertyReader(),
//                    new ResourceItem(Resource.flower), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
//            }
            room.setTile(x, y, Tile.GRASS, 0);
        }
    }

    private static final class WaterTile extends Tile {
        private Random wRandom = new Random();

        public WaterTile(int id) {
            super(id);
            connectsToSand = true;
            connectsToWater = true;
        }

        public void render(Screen screen, Room room, int x, int y) {
//            wRandom.setSeed((tickCount + (x / 2 - y) * 4311) / 10 * 54687121l + x * 3271612l + y * 3412987161l);
            int col = Color.get(005, 005, 115, 115);
            int transitionColor1 = Color.get(3, 005, Room.DIRT_COLOR - 111, Room.DIRT_COLOR);
            int transitionColor2 = Color.get(3, 005, Room.GRASS_COLOR - 110, Room.GRASS_COLOR);

            boolean u = !room.getTile(x, y - 1).connectsToWater;
            boolean d = !room.getTile(x, y + 1).connectsToWater;
            boolean l = !room.getTile(x - 1, y).connectsToWater;
            boolean r = !room.getTile(x + 1, y).connectsToWater;

            boolean su = u && room.getTile(x, y - 1).connectsToSand;
            boolean sd = d && room.getTile(x, y + 1).connectsToSand;
            boolean sl = l && room.getTile(x - 1, y).connectsToSand;
            boolean sr = r && room.getTile(x + 1, y).connectsToSand;

            if (!u && !l) {
                screen.render(x * 16 + 0, y * 16 + 0, wRandom.nextInt(4), col, wRandom.nextInt(4));
            } else
                screen.render(x * 16 + 0, y * 16 + 0, (l ? 14 : 15) + (u ? 0 : 1) * 32, (su || sl) ? transitionColor2 : transitionColor1, 0);

            if (!u && !r) {
                screen.render(x * 16 + 8, y * 16 + 0, wRandom.nextInt(4), col, wRandom.nextInt(4));
            } else
                screen.render(x * 16 + 8, y * 16 + 0, (r ? 16 : 15) + (u ? 0 : 1) * 32, (su || sr) ? transitionColor2 : transitionColor1, 0);

            if (!d && !l) {
                screen.render(x * 16 + 0, y * 16 + 8, wRandom.nextInt(4), col, wRandom.nextInt(4));
            } else
                screen.render(x * 16 + 0, y * 16 + 8, (l ? 14 : 15) + (d ? 2 : 1) * 32, (sd || sl) ? transitionColor2 : transitionColor1, 0);
            if (!d && !r) {
                screen.render(x * 16 + 8, y * 16 + 8, wRandom.nextInt(4), col, wRandom.nextInt(4));
            } else
                screen.render(x * 16 + 8, y * 16 + 8, (r ? 16 : 15) + (d ? 2 : 1) * 32, (sd || sr) ? transitionColor2 : transitionColor1, 0);
        }

        public boolean mayPass(Room room, int x, int y, Entity e) {
            return false;
        }
    }


}
