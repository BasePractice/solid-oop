package ru.mifi.practice.vol7.adapter;

import java.util.Objects;
import java.util.Optional;

/**
 * Адаптер: снаружи выглядит как {@link Int2}, внутри держит {@link Int1} и переводит
 * его «строка либо null» в «Optional». Клиент про {@code Int1} не знает вовсе.
 */
public final class Adapter implements Int2 {
    private final Int1 origin;

    public Adapter(Int1 origin) {
        this.origin = Objects.requireNonNull(origin, "Adapter cannot wrap nothing");
    }

    @Override
    public Optional<String> title() {
        return Optional.ofNullable(origin.getName());
    }
}
