package ru.mifi.practice.ui;

import java.util.Arrays;

public interface Screen {
    int width();

    int height();

    void clear(int color);

    void setOffset(int xOffset, int yOffset);

    void overlay(Screen screen, int xa, int ya);

    void render(int xp, int yp, int tile, int colors, int bits);

    void renderLight(int x, int y, int r);

    void draw(String msg, int x, int y, int col);

    int pixel(int i);

    void setPixel(int x, int y, int colors);

    final class Default implements Screen {
        private static final int BIT_MIRROR_X = 0x01;
        private static final int BIT_MIRROR_Y = 0x02;
        private final int width;
        private final int height;
        private final SpriteSheet sheet;
        private final int[] dither = new int[]{0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5,};
        private final int[] pixels;
        private final Font font;
        private int xOffset;
        private int yOffset;

        public Default(int width, int height, SpriteSheet sheet) {
            this.sheet = sheet;
            this.width = width;
            this.height = height;
            this.pixels = new int[width * height];
            this.font = new Font.Default(this);
        }

        @Override
        public int width() {
            return width;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public void clear(int color) {
            Arrays.fill(pixels, color);
        }

        @Override
        public void render(int xp, int yp, int tile, int colors, int bits) {
            xp -= xOffset;
            yp -= yOffset;
            boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
            boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

            int xTile = tile % 32;
            int yTile = tile / 32;
            int toffs = xTile * 8 + yTile * 8 * sheet.width;

            for (int y = 0; y < 8; y++) {
                int ys = y;
                if (mirrorY) {
                    ys = 7 - y;
                }
                if (y + yp < 0 || y + yp >= height) {
                    continue;
                }
                for (int x = 0; x < 8; x++) {
                    if (x + xp < 0 || x + xp >= width) {
                        continue;
                    }

                    int xs = x;
                    if (mirrorX) {
                        xs = 7 - x;
                    }
                    int col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255;
                    if (col < 255) {
                        pixels[(x + xp) + (y + yp) * width] = col;
                    }
                }
            }
        }

        @Override
        public void setOffset(int xOffset, int yOffset) {
            this.yOffset = yOffset;
            this.xOffset = xOffset;
        }

        @Override
        public void overlay(Screen screen, int xa, int ya) {
            int[] oPixels = ((Default) screen).pixels;
            int i = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (oPixels[i] / 10 <= dither[((x + xa) & 3) + ((y + ya) & 3) * 4]) {
                        pixels[i] = 0;
                    }
                    i++;
                }
            }
        }

        @Override
        public void renderLight(int x, int y, int r) {
            x -= xOffset;
            y -= yOffset;
            int x0 = x - r;
            int x1 = x + r;
            int y0 = y - r;
            int y1 = y + r;

            if (x0 < 0) {
                x0 = 0;
            }
            if (y0 < 0) {
                y0 = 0;
            }
            if (x1 > width) {
                x1 = width;
            }
            if (y1 > height) {
                y1 = height;
            }
            for (int yy = y0; yy < y1; yy++) {
                int yd = yy - y;
                yd = yd * yd;
                for (int xx = x0; xx < x1; xx++) {
                    int xd = xx - x;
                    int dist = xd * xd + yd;
                    if (dist <= r * r) {
                        int br = 255 - dist * 255 / (r * r);
                        if (pixels[xx + yy * width] < br) {
                            pixels[xx + yy * width] = br;
                        }
                    }
                }
            }
        }

        @Override
        public void draw(String msg, int x, int y, int col) {
            font.draw(msg, x, y, col);
        }

        @Override
        public int pixel(int i) {
            return pixels[i];
        }

        @Override
        public void setPixel(int xp, int yp, int colors) {
            xp -= xOffset;
            yp -= yOffset;

            if (yp < 0 || yp >= height) {
                return;
            }
            if (xp < 0 || xp >= width) {
                return;
            }
            pixels[xp + yp * width] = colors;
        }
    }
}
