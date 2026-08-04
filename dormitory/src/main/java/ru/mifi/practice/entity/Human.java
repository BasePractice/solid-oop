package ru.mifi.practice.entity;

/**
 * Человек в комнате. Тип запечатан: игрок в модели один, и это видно из объявления.
 */
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
