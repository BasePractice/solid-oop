package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Color;

/**
 * Свеча. Единственный источник света в тёмной комнате: пока она в руках, вокруг
 * игрока виден круг, без неё — только пятачок под ногами.
 *
 * <p>Драться свечой нельзя, поэтому {@code canAttack} отвечает отказом — удар
 * со свечой в руках проходит вхолостую.
 */
final class Candle extends AbstractItem implements Item.LightItem {
    private static final int RADIUS = 9;

    Candle() {
        super("свеча", Color.get(-1, 550, 553, 555));
    }

    @Override
    public int getLightRadius() {
        return RADIUS;
    }
}
