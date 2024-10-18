package ru.mifi.practice.ui;

import ru.mifi.practice.entity.Human;
import ru.mifi.practice.room.Room;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

public interface Model {
    String NAME = "Room";

    static Model start(boolean development) {
        return new Default(development).start();
    }

    int width();

    int height();

    int scale();

    int time();

    void respawn();

    void addHandler(Handler handler);

    final class Default extends Canvas implements Runnable, Model {
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
        private int lightning = 2;
        private int playerDeadTime;
        private int wonTimer = 0;
        private boolean hasWon = false;
        private boolean respawn = false;
        private int drawableFrames = 0;
        private int drawableTicks = 0;
        private int distance;
        private boolean fastQuit = false;
        private final Room room;

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
            this.room = Room.DEFAULT_FACTORY.create("r00m", input);
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
                room.tick();
                Human player = room != null ? room.player() : null;
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

            Human player = room.player();
            if (player != null && room.canRender()) {
                int xScroll = player.getX() - screen.width() / 2;
                int yScroll = player.getY() - (screen.height() - 8) / 2;
                if (xScroll < 16) {
                    xScroll = 16;
                }
                if (yScroll < 16) {
                    yScroll = 16;
                }
                if (xScroll > room.width() * 16 - screen.width() - 16) {
                    xScroll = room.width() * 16 - screen.width() - 16;
                }
                if (yScroll > room.height() * 16 - screen.height() - 16) {
                    yScroll = room.height() * 16 - screen.height() - 16;
                }
                if (lightning > 3) {
                    int col = Color.get(20, 20, 121, 121);
                    for (int y = 0; y < 14; y++) {
                        for (int x = 0; x < 24; x++) {
                            screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
                        }
                    }
                }
                room.renderBackground(screen, xScroll, yScroll);
                room.renderSprites(screen, xScroll, yScroll);

                if (lightning < 3) {
                    lightScreen.clear(0);
                    room.renderLight(lightScreen, xScroll, yScroll);
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
                    msg = String.format("MAP: %5s", room.name());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("X  : %5d", player.getX());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("Y  : %5d", player.getY());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                    msg = String.format("STATE: %5s", player.state());
                    font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                    yNext += 8;
                }
                Room.Meta meta = room.meta();
                msg = String.format("OFFSET-X: %5d", meta.xo());
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-Y: %5d", meta.yo());
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-W: %5d", meta.wo() + meta.xo());
                font.draw(msg, (width() - 8) - msg.length() * 8, yNext, Color.get(-1, 550, 550, 550));
                yNext += 8;
                msg = String.format("OFFSET-H: %5d", meta.ho() + meta.yo());
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

            Human player = room != null ? room.player() : null;
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


            screen.render(xx - 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
            screen.render(xx + w * 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
            screen.render(xx - 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
            screen.render(xx + w * 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3);
            for (int x = 0; x < w; x++) {
                screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
                screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
            }
            int h = 1;
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
                screen = new Screen.Default(width(), height(),
                    new SpriteSheet(ImageIO.read(Objects.requireNonNull(Model.class.getResourceAsStream("/icons.png")))));
                lightScreen = new Screen.Default(width(), height(),
                    new SpriteSheet(ImageIO.read(Objects.requireNonNull(Model.class.getResourceAsStream("/icons.png")))));
                font = new Font.Default(screen);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            distance = Math.max(screen.width() / 2, (screen.height() - 8) / 2);
        }
    }
}
