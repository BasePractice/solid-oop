package ru.mifi.practice.entity;

import ru.mifi.practice.ui.Color;

/**
 * Тапок. Бьёт тех, кто бегает по полу, и бесполезен против летающих — до мухи
 * в воздухе им просто не дотянуться.
 */
final class Slipper extends AbstractItem implements Item.ToolItem {
    private static final int DAMAGE = 4;

    Slipper() {
        super("тапок", Color.get(-1, 200, 310, 421));
    }

    @Override
    public boolean canAttack() {
        return true;
    }

    @Override
    public boolean reaches(Entity entity) {
        return !(entity instanceof Bug bug) || !bug.flying();
    }

    @Override
    public int getAttackDamageBonus(Entity entity) {
        return reaches(entity) ? DAMAGE : 0;
    }
}
