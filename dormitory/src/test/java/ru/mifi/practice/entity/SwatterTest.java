package ru.mifi.practice.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class SwatterTest {

    @Test
    void doesNotMissFlyingBug() {
        assertThat(
            "swatter cannot reach a bug in the air, but that is its only job",
            new Swatter().reaches(new FakeBug(true)),
            is(true)
        );
    }

    @Test
    void doesNotReachCrawlingBug() {
        assertThat(
            "swatter reaches a bug on the floor, but a flat mesh cannot crush it",
            new Swatter().reaches(new FakeBug(false)),
            is(false)
        );
    }

    @Test
    void doesNotLeaveFlyingBugUnharmed() {
        assertThat(
            "swatter adds no damage to a flying bug, so flies would never die",
            new Swatter().getAttackDamageBonus(new FakeBug(true)),
            is(20)
        );
    }
}
