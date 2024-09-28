package ru.mifi.practice.vol2.ant;

import com.google.common.io.LineProcessor;

import java.io.IOException;

public sealed interface Grid {

    static Grid toroid(int width, int height) {
        return new ToroidGrid(width, height);
    }

    static LineProcessor<Grid> toroidProcessor() {
        return new ToroidGrid.ToroidLineProcessor();
    }

    int foods();

    int getWidth();

    int getHeight();

    Place at(int x, int y);

    Mutable copy();

    Place nextPlace(int x, int y, Direction direction);

    enum Element {
        NONE,
        PATH,
        FOOD,
        ANT
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    sealed interface Mutable extends Grid {
        void set(int x, int y, Element element);
    }

    record Place(int x, int y, Element element) {

    }

    final class ToroidGrid implements Mutable {
        private final int width;
        private final int height;
        private final Element[][] map;
        private int foods;

        private ToroidGrid(int width, int height) {
            this.width = width;
            this.height = height;
            this.map = new Element[width][height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    map[x][y] = Element.NONE;
                }
            }
        }

        @Override
        public int foods() {
            return foods;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public Place at(int x, int y) {
            return new Place(x, y, map[x][y]);
        }

        @Override
        public Mutable copy() {
            ToroidGrid n = new ToroidGrid(width, height);
            for (int x = 0; x < height; x++) {
                System.arraycopy(map[x], 0, n.map[x], 0, width);
            }
            return n;
        }

        @Override
        public Place nextPlace(int x, int y, Direction direction) {
            switch (direction) {
                case UP: {
                    y--;
                    if (y <= 0) {
                        y = height - 1;
                    }
                    break;
                }
                case DOWN: {
                    ++y;
                    if (y >= height) {
                        y = 0;
                    }
                    break;
                }
                case LEFT: {
                    --x;
                    if (x < 0) {
                        x = width - 1;
                    }
                    break;
                }
                case RIGHT: {
                    ++x;
                    if (x >= width) {
                        x = 0;
                    }
                    break;
                }
                default:
                    //Ignore
            }
            return new Place(x, y, map[x][y]);
        }

        @Override
        public void set(int x, int y, Element element) {
            map[x][y] = element;
        }

        private static final class ToroidLineProcessor implements LineProcessor<Grid> {
            private ToroidGrid toroidGrid = null;
            private int width;
            private int it;

            @Override
            public boolean processLine(String line) throws IOException {
                if (width == 0) {
                    width = Integer.parseInt(line);
                } else if (toroidGrid == null) {
                    toroidGrid = new ToroidGrid(width, Integer.parseInt(line));
                } else {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length; i++) {
                        switch (Integer.parseInt(parts[i])) {
                            case 1: {
                                toroidGrid.map[i][it] = Element.FOOD;
                                toroidGrid.foods++;
                                break;
                            }
                            case 2: {
                                toroidGrid.map[i][it] = Element.PATH;
                                break;
                            }
                            case 3: {
                                toroidGrid.map[i][it] = Element.ANT;
                                break;
                            }
                            case 0:
                            default: {
                                toroidGrid.map[i][it] = Element.NONE;
                                break;
                            }
                        }
                    }
                    ++it;
                }
                return true;
            }

            @Override
            public Grid getResult() {
                return toroidGrid;
            }
        }
    }
}
