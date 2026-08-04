package ru.mifi.practice.entity;

import ru.mifi.practice.room.Room;
import ru.mifi.practice.ui.Screen;
import ru.mifi.practice.ui.Tile;

/**
 * Предмет инвентаря. Свеча светит, тапок бьёт, мебель ставится на пол — поэтому
 * подтипы объявлены отдельно, а игрок спрашивает не «что это», а «умеешь ли ты».
 *
 * <p>{@code reaches} отвечает на вопрос, достаёт ли предмет до конкретной цели:
 * тапком муху в воздухе не поймать, а мухобойкой — можно. Промах живёт здесь,
 * а не в самом насекомом: это свойство орудия, а не жертвы.
 */
public interface Item extends Entity {

    String name();

    boolean interact(Human player, Entity entity, int attackDir);

    void renderIcon(Screen screen, int x, int y);

    boolean interactOn(Tile tile, Room room, int xt, int yt, Human player, int attackDir);

    boolean isDepleted();

    boolean canAttack();

    boolean reaches(Entity entity);

    int getAttackDamageBonus(Entity entity);

    interface FurnitureItem extends Item {
        void update(int x, int y);
    }

    interface LightItem extends Item {
        @Override
        int getLightRadius();
    }

    interface ToolItem extends Item {

    }
}
