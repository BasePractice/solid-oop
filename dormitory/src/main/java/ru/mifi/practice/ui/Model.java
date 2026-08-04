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
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Окно и игровой цикл: тики идут с фиксированной частотой, кадры рисуются по мере
 * готовности. Ресурсы читаются один раз и разделяются между экранами — картинка
 * одна и та же, незачем держать её в памяти дважды.
 */
public interface Model {
    String NAME = "Room";
    String ICONS = "/icons.png";

    static Model start(boolean development) {
        return new Default(development).start();
    }

    int width();

    int height();

    int scale();

    int time();

    void addHandler(Handler handler);

    final class Default extends Canvas implements Runnable, Model {
        private final Palette palette = new Palette();
        private final JFrame frame;
        private final Handler input;
        private final Room room;
        private final boolean development;
        private BufferedImage image;
        private int[] pixels;
        private Screen screen;
        private Screen lightScreen;
        private Font font;
        private boolean running;
        private int tickCount;
        private int gameTime;
        private int drawableFrames;
        private int drawableTicks;

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

        @Override
        public int time() {
            return gameTime;
        }

        public void run() {
            long lastTime = System.nanoTime();
            double unprocessed = 0;
            double nsPerTick = 1000000000.0 / 60;
            int frames = 0;
            int ticks = 0;
            long lastTimer1 = System.currentTimeMillis();

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
            } finally {
                frame.dispose();
            }
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
            if (player != null) {
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
                room.renderBackground(screen, xScroll, yScroll);
                room.renderSprites(screen, xScroll, yScroll);

                lightScreen.clear(0);
                room.renderLight(lightScreen, xScroll, yScroll);
                screen.overlay(lightScreen, xScroll, yScroll);
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
                        pixels[x + y * width()] = palette.rgb(cc);
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
                        screen.render(i * 8, screen.height() - 16, 0 + 12 * 32, Color.get(0, 200, 500, 533), 0);
                    } else {
                        screen.render(i * 8, screen.height() - 16, 0 + 12 * 32, Color.get(0, 100, 0, 0), 0);
                    }

                    if (player.staminaRechargeDelay() > 0) {
                        if (player.staminaRechargeDelay() / 4 % 2 == 0) {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(0, 555, 0, 0), 0);
                        } else {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0);
                        }
                    } else {
                        if (i < player.stamina()) {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(0, 220, 550, 553), 0);
                        } else {
                            screen.render(i * 8, screen.height() - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0);
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
            Dimension size = new Dimension(width() * scale(), height() * scale());
            setMinimumSize(size);
            setMaximumSize(size);
            setPreferredSize(size);
            image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            SpriteSheet sheet = sheet(ICONS);
            screen = new Screen.Default(width(), height(), sheet);
            lightScreen = new Screen.Default(width(), height(), sheet);
            font = new Font.Default(screen);
        }

        private static SpriteSheet sheet(String name) {
            try (InputStream stream = Model.class.getResourceAsStream(name)) {
                return new SpriteSheet(ImageIO.read(
                    Objects.requireNonNull(stream, "Sprite sheet " + name + " is missing from resources")));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read sprite sheet " + name, e);
            }
        }
    }
}
