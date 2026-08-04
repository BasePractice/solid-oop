package ru.mifi.practice.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class SlipperTest {

    @Test
    void doesNotReachFlyingBug() {
        assertThat(
            "slipper reaches a bug in the air, but it cannot",
            new Slipper().reaches(new FakeBug(true)),
            is(false)
        );
    }

    @Test
    void doesNotAddDamageToFlyingBug() {
        assertThat(
            "slipper adds damage to a flying bug, but it cannot touch it",
            new Slipper().getAttackDamageBonus(new FakeBug(true)),
            is(0)
        );
    }

    @Test
    void doesNotLeaveCrawlingBugUnharmed() {
        assertThat(
            "slipper adds no damage to a crawling bug, but that is what it is for",
            new Slipper().getAttackDamageBonus(new FakeBug(false)),
            is(4)
        );
    }
}
