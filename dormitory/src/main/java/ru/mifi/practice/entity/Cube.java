package ru.mifi.practice.entity;

/**
 * Кубический предмет обстановки — заготовка для стола, стула и шкафа: от них он
 * отличается только размерами и внешним видом.
 */
final class Cube extends AbstractStaticEntity {
    Cube(int x, int y, int z, int side) {
        super(x, y, z, side, side, side);
    }
}
