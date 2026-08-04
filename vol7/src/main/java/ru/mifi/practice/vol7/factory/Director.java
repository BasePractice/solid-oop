package ru.mifi.practice.vol7.factory;

import java.util.List;

/**
 * Директор — сотрудник, у которого есть подчинённые.
 */
public interface Director extends Employee {

    List<Employee> subordinates();
}
