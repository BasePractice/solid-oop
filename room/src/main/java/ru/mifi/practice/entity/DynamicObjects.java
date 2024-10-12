package ru.mifi.practice.entity;

import java.util.UUID;

public final class DynamicObjects {


    private abstract static class AbstractDynamicEntity implements Entity.Dynamic {
        private final UUID id;
        private int x;
        private int y;
        private int z;
        private int side;

        protected AbstractDynamicEntity(int x, int y, int z, int side) {
            this.id = UUID.randomUUID();
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int z() {
            return z;
        }

        @Override
        public UUID id() {
            return id;
        }

        @Override
        public void tick() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static final class Player extends AbstractDynamicEntity implements Entity.Human {
        private Player(int x, int y, int z) {
            super(x, y, z, 4);
        }
    }
}
