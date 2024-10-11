package ru.mifi.practice.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public interface Room {
    String NAME = "Room";

    static Room start(boolean development) {
        return new Default(development).start();
    }

    int width();

    int height();

    int scale();

    int time();

    void respawn();

    void addHandler(Handler handler);

    interface Instance {
        Player player();

        boolean canRender();

        int width();

        int height();

        void renderBackground(Screen screen, int xScroll, int yScroll);

        void renderSprites(Screen screen, int xScroll, int yScroll);

        void renderLight(Screen lightScreen, int xScroll, int yScroll);

        String name();

        final class Buffer {
            final byte[] tiles;
            final byte[] datas;

            private Buffer(int width, int height, byte[] tiles, byte[] data) {
                this.tiles = new byte[tiles.length];
                System.arraycopy(tiles, 0, this.tiles, 0, tiles.length);
                this.datas = new byte[data.length];
                System.arraycopy(data, 0, this.datas, 0, data.length);
            }

            void copy(Buffer buffer) {
                System.arraycopy(buffer.tiles, 0, this.tiles, 0, tiles.length);
                System.arraycopy(buffer.datas, 0, this.datas, 0, datas.length);
//                    for (int i = 0; i < buffer.entitiesInTiles.length; i++) {
//                        this.entitiesInTiles[i] = new HashSet<>(buffer.entitiesInTiles[i]);
//                    }
            }
        }

        final class Default implements Instance {
            private final Player player;
            private final Buffer[] buffers;
            private final String name;
            private final int width;
            private final int height;
            private byte[] tiles;
            private byte[] data;
            private int swapBuffer;

            private int xo;
            private int yo;
            private int ho;
            private int wo;


            public Default(String name, int width, int height, Handler input, byte[] tiles, byte[] data) {
                this.name = name;
                this.width = width;
                this.height = height;
                this.buffers = new Buffer[2];
                this.buffers[0] = new Buffer(width, height, tiles, data);
                this.buffers[1] = new Buffer(width, height, tiles, data);
                this.swapBuffer = 0;
                this.tiles = buffers[swapBuffer].tiles;
                this.data = buffers[swapBuffer].datas;
                this.swapBuffer = 1;
                this.player = new Player.Default(input, this);
            }

            @Override
            public Player player() {
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

            @Override
            public void renderBackground(Screen screen, int xScroll, int yScroll) {
                xo = xScroll >> 4;
                yo = yScroll >> 4;
                wo = (screen.width() + 15) >> 4;
                ho = (screen.height() + 15) >> 4;
                screen.setOffset(xScroll, yScroll);
//                for (int y = yo; y <= ho + yo; y++) {
//                    for (int x = xo; x <= wo + xo; x++) {
//                        getTile(x, y).render(screen, this, x, y);
//                    }
//                }
                screen.setOffset(0, 0);
            }

            @Override
            public void renderSprites(Screen screen, int xScroll, int yScroll) {
                final ArrayList<Player> rowSprites = new ArrayList<>();
                xo = xScroll >> 4;
                yo = yScroll >> 4;
                wo = (screen.width() + 15) >> 4;
                ho = (screen.height() + 15) >> 4;

                screen.setOffset(xScroll, yScroll);
                rowSprites.add(player);
                sortAndRender(screen, rowSprites);
//                for (int y = yo; y <= ho + yo; y++) {
//                    for (int x = xo; x <= wo + xo; x++) {
//                        if (x < 0 || y < 0 || x >= this.width() || y >= this.height()) continue;
//                        rowSprites.addAll(entitiesInTiles[x + y * this.width()]);
//                    }
//                    if (!rowSprites.isEmpty()) {
//                        sortAndRender(screen, rowSprites);
//                    }
//                    rowSprites.clear();
//                }
                screen.setOffset(0, 0);
            }

            private void sortAndRender(Screen screen, ArrayList<Player> rowSprites) {
                rowSprites.forEach(p -> p.render(screen));
            }

            @Override
            public void renderLight(Screen screen, int xScroll, int yScroll) {
                int xo = xScroll >> 4;
                int yo = yScroll >> 4;
                int w = (screen.width() + 15) >> 4;
                int h = (screen.height() + 15) >> 4;

                screen.setOffset(xScroll, yScroll);
                int r = 4;
                for (int y = yo - r; y <= h + yo + r; y++) {
//                    for (int x = xo - r; x <= w + xo + r; x++) {
//                        if (x < 0 || y < 0 || x >= this.width() || y >= this.height()) continue;
//                        Set<Entity> entities = entitiesInTiles[x + y * this.w];
//                        for (Entity e : entities) {
//                            // e.render(screen);
//                            int lr = e.getLightRadius();
//                            if (lr > 0) screen.renderLight(e.x - 1, e.y - 4, lr * 8);
//                        }
//                        int lr = getTile(x, y).getLightRadius(this, x, y);
//                        if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
//                    }
                }
                screen.setOffset(0, 0);
            }

            @Override
            public String name() {
                return name;
            }

//            public Tile getTile(int x, int y) {
//                if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
//                return Tile.tiles[tiles[x + y * w]];
//            }
//
//            public void setTile(int x, int y, Tile t, int dataVal) {
//                if (x < 0 || y < 0 || x >= w || y >= h) return;
//                tiles[x + y * w] = t.id;
//                setData(x, y, dataVal);
//            }

            public int getData(int x, int y) {
                if (x < 0 || y < 0 || x >= width() || y >= height()) return 0;
                return data[x + y * width()] & 0xff;
            }

            public void setData(int x, int y, int val) {
                if (x < 0 || y < 0 || x >= width() || y >= height()) return;
                data[x + y * width()] = (byte) val;
            }


        }
    }

    final class Default extends Canvas implements Runnable, Room {
        private final int[] colors = new int[256];
        private final JFrame frame;
        private final Handler input;
        private final boolean development;
        private BufferedImage image;
        private int[] pixels;
        private boolean running = false;
        private Screen screen;
        private Screen lightScreen;
        private Font font;
        private int tickCount = 0;
        private int gameTime = 0;
        private int currentLevel = 3;
        private int playerDeadTime;
        private int wonTimer = 0;
        private boolean hasWon = false;
        private boolean respawn = false;
        private int drawableFrames = 0;
        private int drawableTicks = 0;
        private int distance;
        private boolean fastQuit = false;
        private Instance.Default instance;

        private Default(boolean development) {
            selfUpdate();
            this.development = development;
            this.frame = new JFrame(NAME);
            this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.frame.setLayout(new BorderLayout());
            this.frame.add(this, BorderLayout.CENTER);
            this.frame.pack();
            this.frame.setResizable(false);
            this.frame.setLocationRelativeTo(null);

            this.image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
            this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            this.input = new Handler(this);
            this.instance = new Instance.Default(
                "r00m", 128, 128, this.input, new byte[0], new byte[0]);
        }

        public Default start() {
            frame.setVisible(true);
            running = true;
            new Thread(this).start();
            return this;
        }

        @Override
        public int width() {
            return 800 / scale();
        }

        @Override
        public int height() {
            return 600 / scale();
        }

        @Override
        public int scale() {
            return 3;
        }

        public void stop() {
            running = false;
        }

        public void reset() {
            if (fastQuit) {
                fastQuit = false;
                return;
            }
            playerDeadTime = 0;
            wonTimer = 0;
            gameTime = 0;
            hasWon = false;
        }

        @Override
        public void respawn() {
            playerDeadTime = 0;
            wonTimer = 0;
            gameTime = 0;
            fastQuit = false;
            respawn = true;
        }

        @Override
        public int time() {
            return gameTime;
        }

        private void init() {
            int pp = 0;
            for (int r = 0; r < 6; r++) {
                for (int g = 0; g < 6; g++) {
                    for (int b = 0; b < 6; b++) {
                        int rr = (r * 255 / 5);
                        int gg = (g * 255 / 5);
                        int bb = (b * 255 / 5);
                        int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

                        int r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10;
                        int g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10;
                        int b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10;
                        colors[pp++] = r1 << 16 | g1 << 8 | b1;
                    }
                }
            }
            reset();
        }

        public void run() {
            long lastTime = System.nanoTime();
            double unprocessed = 0;
            double nsPerTick = 1000000000.0 / 60;
            int frames = 0;
            int ticks = 0;
            long lastTimer1 = System.currentTimeMillis();

            init();
            try {
                while (running) {
                    long now = System.nanoTime();
                    unprocessed += (now - lastTime) / nsPerTick;
                    lastTime = now;
                    boolean shouldRender = false;
                    while (unprocessed >= 1) {
                        ticks++;
                        tick();
                        unprocessed -= 1;
                        shouldRender = true;
                    }

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        //Ignore
                    }

                    if (shouldRender) {
                        frames++;
                        render();
                    }

                    if (System.currentTimeMillis() - lastTimer1 > 1000) {
                        lastTimer1 += 1000;
                        drawableFrames = frames;
                        drawableTicks = ticks;
                        frames = 0;
                        ticks = 0;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            frame.dispose();
        }

        public void tick() {
            tickCount++;
            if (!hasFocus()) {
                input.releaseAll();
            } else {
                input.tick();
                Player player = instance != null ? instance.player() : null;
                if (player != null) {
                    gameTime++;
                    player.tick();
                }
            }
        }

        @Override
        public void addHandler(Handler handler) {
            super.addKeyListener(handler);
        }

        public void render() {
            int color = Color.get(0);
            screen.clear(color);
            BufferStrategy bs = getBufferStrategy();
            if (bs == null) {
                createBufferStrategy(3);
                requestFocus();
                return;
            }

            Player player = instance.player();
            if (player != null && instance.canRender()) {
                int xScroll = player.x() - screen.width() / 2;
                int yScroll = player.y() - (screen.height() - 8) / 2;
                if (xScroll < 16) {
                    xScroll = 16;
                }
                if (yScroll < 16) {
                    yScroll = 16;
                }
                if (xScroll > instance.width() * 16 - screen.width() - 16) {
                    xScroll = instance.width() * 16 - screen.width() - 16;
                }
                if (yScroll > instance.height() * 16 - screen.height() - 16) {
                    yScroll = instance.height() * 16 - screen.height() - 16;
                }
                if (currentLevel > 3) {
                    int col = Color.get(20, 20, 121, 121);
                    for (int y = 0; y < 14; y++) {
                        for (int x = 0; x < 24; x++) {
                            screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
                        }
                    }
                }
                instance.renderBackground(screen, xScroll, yScroll);
                instance.renderSprites(screen, xScroll, yScroll);

                if (currentLevel < 3) {
                    lightScreen.clear(0);
                    instance.renderLight(lightScreen, xScroll, yScroll);
                    screen.overlay(lightScreen, xScroll, yScroll);
                }
            }

            renderGui(color);
            if (!hasFocus()) {
                renderFocusNagger();
            }

            int yNext = 0;
            if (development) {
                String msg = String.format("FPS: %5d", drawableTicks);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("FRAMES: %5d", drawableFrames);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                if (player != null) {
                    msg = String.format("MAP: %5s", instance.name());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("X  : %5d", player.x());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("Y  : %5d", player.y());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("STATE: %5s", player.state());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                }
                msg = String.format("OFFSET-X: %5d", instance.xo);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-Y: %5d", instance.yo);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-W: %5d", instance.wo + instance.xo);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-H: %5d", instance.ho + instance.yo);
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
            }

            for (int y = 0; y < screen.height(); y++) {
                for (int x = 0; x < screen.width(); x++) {
                    int cc = screen.pixel(x + y * screen.width());
                    if (cc < 255) {
                        pixels[x + y * width()] = colors[cc];
                    }
                }
            }

            Graphics g = bs.getDrawGraphics();
            g.fillRect(0, 0, getWidth(), getHeight());

            int ww = width() * scale();
            int hh = height() * scale();
            int xo = (getWidth() - ww) / 2;
            int yo = (getHeight() - hh) / 2;
            g.drawImage(image, xo, yo, ww, hh, null);
            g.dispose();
            bs.show();
        }

        private void renderGui(int color) {
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 20; x++) {
                    screen.render(x * 8, screen.height() - 16 + y * 8, 0 + 12 * 32, color, 0);
                }
            }

            Player player = instance != null ? instance.player() : null;
            if (player != null) {
                for (int i = 0; i < 10; i++) {
                    if (i < player.health()) {
                        screen.render(i * 8, screen.height() - 16, 0 + 12 * 32, Color.get(000, 200, 500, 533), 0);
                    } else {
                        screen.render(i * 8, screen.height() - 16, 0 + 12 * 32, Color.get(000, 100, 000, 000), 0);
                    }

                    if (player.staminaRechargeDelay() > 0) {
                        if (player.staminaRechargeDelay() / 4 % 2 == 0) {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(000, 555, 000, 000), 0);
                        } else {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
                        }
                    } else {
                        if (i < player.stamina()) {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(000, 220, 550, 553), 0);
                        } else {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
                        }
                    }
                }
            }
        }

        private void renderFocusNagger() {
            renderSplashMessage("Click to focus!");
        }

        private void renderSplashMessage(String msg) {
            int xx = (width() - msg.length() * 8) / 2;
            int yy = (height() - 8) / 2;
            int w = msg.length();
            int h = 1;

            screen.render(xx - 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + w * 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
            screen.render(xx - 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
            screen.render(xx + w * 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3);
            for (int x = 0; x < w; x++) {
                screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
                screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
            }
            for (int y = 0; y < h; y++) {
                screen.render(xx - 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
                screen.render(xx + w * 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
            }

            if ((tickCount / 20) % 2 == 0) {
                font.draw(msg, xx, yy, Color.get(5, 333, 333, 333));
            } else {
                font.draw(msg, xx, yy, Color.get(5, 555, 555, 555));
            }
        }

        private void selfUpdate() {
            int xWidth = width() * scale();
            int xHeight = height() * scale();

            Dimension size = new Dimension(xWidth, xHeight);
            setMinimumSize(size);
            setMaximumSize(size);
            setPreferredSize(size);

            image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            try {
                screen = new Screen.Default(width(), height(), new SpriteSheet(ImageIO.read(Room.class.getResourceAsStream("/icons.png"))));
                lightScreen = new Screen.Default(width(), height(), new SpriteSheet(ImageIO.read(Room.class.getResourceAsStream("/icons.png"))));
                font = new Font.Default(screen);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            distance = Math.max(screen.width() / 2, (screen.height() - 8) / 2);
        }
    }
}
