package ru.mifi.practice.vol7.adapter;

/**
 * Чужой интерфейс, который менять нельзя: отдаёт имя строкой и допускает {@code null},
 * если имени нет.
 */
@FunctionalInterface
public interface Int1 {

    String getName();
}
