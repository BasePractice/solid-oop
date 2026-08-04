package ru.mifi.practice.entity;

import java.util.List;
import java.util.Optional;

/**
 * Инвентарь: набор предметов и один выбранный. Список неизменяем — предметы в этой
 * версии не подбираются и не теряются, меняется только выбор.
 *
 * <p>Пустой инвентарь — законное состояние: игрок с голыми руками тоже умеет бить,
 * поэтому {@code active} возвращает {@link Optional}, а не выдумывает «предмет-пустышку».
 */
public final class Inventory {
    private final List<Item> items;
    private int selected;

    public Inventory(List<Item> items) {
        this.items = List.copyOf(items);
    }

    public List<Item> items() {
        return items;
    }

    public int selected() {
        return selected;
    }

    public Optional<Item> active() {
        if (selected < 0 || selected >= items.size()) {
            return Optional.empty();
        }
        return Optional.of(items.get(selected));
    }

    public void select(int index) {
        if (index >= 0 && index < items.size()) {
            selected = index;
        }
    }
}
