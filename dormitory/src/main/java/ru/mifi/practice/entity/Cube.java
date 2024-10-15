package ru.mifi.practice.entity;

final class Cube extends AbstractStaticEntity {
    Cube(int x, int y, int z, int side) {
        super(x, y, z, side, side, side);
    }
}
