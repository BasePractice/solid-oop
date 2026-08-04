package ru.mifi.practice.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

final class CandleTest {

    @Test
    void doesNotLeaveRoomDark() {
        assertThat(
            "candle lights nothing, so the room stays dark with it in hand",
            new Candle().getLightRadius(),
            greaterThan(new Slipper().getLightRadius())
        );
    }

    @Test
    void doesNotServeAsWeapon() {
        assertThat(
            "candle can attack, but nobody kills a cockroach with a candle",
            new Candle().canAttack(),
            is(false)
        );
    }
}
