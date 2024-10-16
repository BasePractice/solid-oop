package ru.mifi.practice.entity;

public sealed interface Human extends Dynamic permits Player {
    int stamina();

    int staminaRechargeDelay();

    State state();

    enum State {
        STAY,
        WALK,
        ATCK
    }
}
