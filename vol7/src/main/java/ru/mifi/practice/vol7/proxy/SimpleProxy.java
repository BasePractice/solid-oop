package ru.mifi.practice.vol7.proxy;

import java.util.Objects;

/**
 * Статический заместитель: реализует тот же интерфейс, что и оригинал, одну операцию
 * подменяет своей, остальные передаёт дальше. Клиент отличить подмену не может —
 * в этом и смысл паттерна.
 */
public final class SimpleProxy implements Simple {
    private final Simple origin;

    public SimpleProxy(Simple origin) {
        this.origin = Objects.requireNonNull(origin, "Proxy cannot stand for nothing");
    }

    @Override
    public int doSomething() {
        return 1;
    }

    @Override
    public int doSomethingElse() {
        return origin.doSomethingElse();
    }
}
