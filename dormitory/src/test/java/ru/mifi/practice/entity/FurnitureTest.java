package ru.mifi.practice.entity;

import org.junit.jupiter.api.Test;
import ru.mifi.practice.ui.Color;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class FurnitureTest {

    @Test
    void doesNotLetAnybodyThrough() {
        assertThat(
            "furniture lets an entity pass through it, so the table is a ghost",
            new Furniture("стол", 0, 0, 24, 16, Color.get(0)).blocks(null),
            is(true)
        );
    }

    @Test
    void doesNotHideFromOverlappingBox() {
        assertThat(
            "furniture is invisible to the room search, so it can never block anybody",
            new Furniture("стол", 32, 32, 24, 16, Color.get(0)).intersects(40, 40, 48, 44),
            is(true)
        );
    }

    @Test
    void doesNotClaimDistantBox() {
        assertThat(
            "furniture reports a hit far away from itself and blocks empty floor",
            new Furniture("стул", 32, 32, 12, 12, Color.get(0)).intersects(200, 200, 208, 208),
            is(false)
        );
    }
}
