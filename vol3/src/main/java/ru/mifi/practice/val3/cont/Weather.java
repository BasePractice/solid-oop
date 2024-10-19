package ru.mifi.practice.val3.cont;

import java.util.Optional;

@FunctionalInterface
public interface Weather {

    Optional<Details> get(float latitude, float longitude);

    record Details(float temperature, float wind) {

    }
}
