package ru.mifi.practice.vol2.ant;

import ru.mifi.practice.vol2.ant.Grid.Direction;

public interface State {

    boolean next(Ant ant);

    int steps();

    int foods();

    final class Default implements State, Ant.Sensor {
        private final Grid.Mutable map;
        private Direction direction;
        private int antPositionX;
        private int antPositionY;
        private int steps;
        private int foods;

        public Default(Grid map, int antPositionX, int antPositionY) {
            this.map = map.copy();
            this.antPositionX = antPositionX;
            this.antPositionY = antPositionY;
            this.foods = map.foods();
            this.direction = Direction.RIGHT;
        }

        @Override
        public boolean hasFood() {
            Grid.Place place = map.nextPlace(antPositionX, antPositionY, direction);
            return place.element() == Grid.Element.FOOD;
        }

        @Override
        public boolean next(Ant ant) {
            Ant.Step step = ant.step(this);
            switch (step) {
                case FORWARD -> {
                    Grid.Place place = map.nextPlace(antPositionX, antPositionY, direction);
                    if (place.element() == Grid.Element.FOOD) {
                        foods--;
                    }
                    map.set(antPositionX, antPositionY, Grid.Element.PATH);
                    map.set(place.x(), place.y(), Grid.Element.ANT);
                    antPositionX = place.x();
                    antPositionY = place.y();
                }
                case SPIN_LEFT -> {
                    switch (direction) {
                        case UP -> direction = Direction.LEFT;
                        case DOWN -> direction = Direction.RIGHT;
                        case LEFT -> direction = Direction.DOWN;
                        case RIGHT -> direction = Direction.UP;
                        default -> throw new IllegalStateException("Unexpected value: " + direction);
                    }
                }
                case SPIN_RIGHT -> {
                    switch (direction) {
                        case UP -> direction = Direction.RIGHT;
                        case DOWN -> direction = Direction.LEFT;
                        case LEFT -> direction = Direction.UP;
                        case RIGHT -> direction = Direction.DOWN;
                        default -> throw new IllegalStateException("Unexpected value: " + direction);
                    }
                }
                case NOOP -> {
                    //Ignore
                }
                case STOP -> {
                    return false;
                }
                default -> throw new IllegalStateException("Unexpected value: " + step);
            }
            ++steps;
            return true;
        }

        @Override
        public int steps() {
            return steps;
        }

        @Override
        public int foods() {
            return foods;
        }
    }
}
