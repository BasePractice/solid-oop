package ru.mifi.practice.vol7.factory;

/**
 * Сотрудник. Клиент фабрики знает только этот интерфейс и не догадывается,
 * какой класс за ним стоит.
 */
public interface Employee {

    String name();

    String position();
}
