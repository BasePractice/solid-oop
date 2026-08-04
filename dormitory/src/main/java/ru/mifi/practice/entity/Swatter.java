package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Color;

/**
 * Мухобойка. Ровно наоборот к тапку: достаёт летающих, а по бегущим по полу
 * бьёт вхолостую — плоской сеткой таракана не раздавить.
 */
final class Swatter extends AbstractItem implements Item.ToolItem {
    private static final int DAMAGE = 20;

    Swatter() {
        super("мухобойка", Color.get(-1, 100, 111, 333));
    }

    @Override
    public boolean canAttack() {
        return true;
    }

    @Override
    public boolean reaches(Entity entity) {
        return entity instanceof Bug bug && bug.flying();
    }

    @Override
    public int getAttackDamageBonus(Entity entity) {
        return reaches(entity) ? DAMAGE : 0;
    }
}
