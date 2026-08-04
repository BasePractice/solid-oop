package ru.mifi.practice.vol7.adapter;

import java.util.Optional;

/**
 * Интерфейс, которого ждёт наш код: отсутствие значения выражается через
 * {@code Optional}, а не через {@code null}. Именно это несовпадение и примиряет адаптер.
 */
@FunctionalInterface
public interface Int2 {

    Optional<String> title();
}
