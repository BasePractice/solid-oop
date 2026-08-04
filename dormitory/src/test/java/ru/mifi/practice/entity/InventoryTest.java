package ru.mifi.practice.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class InventoryTest {

    @Test
    void doesNotStartWithoutActiveItem() {
        assertThat(
            "inventory hands out nothing at start, so the player begins empty-handed",
            new Inventory(List.of(new Candle(), new Slipper())).active().orElseThrow().name(),
            is("свеча")
        );
    }

    @Test
    void doesNotIgnoreSelection() {
        Inventory inventory = new Inventory(List.of(new Candle(), new Slipper(), new Swatter()));
        inventory.select(2);
        assertThat(
            "inventory forgets the chosen slot, so digits do nothing",
            inventory.active().orElseThrow().name(),
            is("мухобойка")
        );
    }

    @Test
    void doesNotAcceptSelectionOutOfRange() {
        Inventory inventory = new Inventory(List.of(new Candle()));
        inventory.select(7);
        assertThat(
            "inventory accepts a slot that does not exist and loses the current item",
            inventory.active().orElseThrow().name(),
            is("свеча")
        );
    }

    @Test
    void doesNotPretendEmptyInventoryHoldsSomething() {
        assertThat(
            "empty inventory reports an item, but bare hands are not a thing to hold",
            new Inventory(List.of()).active().isPresent(),
            is(false)
        );
    }
}
