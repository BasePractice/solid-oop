package ru.mifi.practice.entity;

public class StaticObjects {



    final class Default implements Entity.Static {
        private final int x;
        private final int y;
        private final int z;
        private final int width;
        private final int height;
        private final int depth;

        Default(int x, int y, int z, int width, int height, int depth) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.depth = depth;
        }
    }
}
