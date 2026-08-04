package ru.mifi.practice.vol7.factory.impl;

import ru.mifi.practice.vol7.factory.Director;
import ru.mifi.practice.vol7.factory.Employee;
import ru.mifi.practice.vol7.factory.EmployeeFactory;

import java.util.List;
import java.util.Objects;

/**
 * Конкретная фабрика — единственное место, где известны классы-реализации.
 * Наружу отдаются только интерфейсы, поэтому сами классы объявлены приватными.
 */
public final class ConcreteEmployeeFactory implements EmployeeFactory {
    @Override
    public Employee createEmployee(String name) {
        return new Staff(Objects.requireNonNull(name, "Employee cannot be created without name"));
    }

    @Override
    public Director createDirector(String name) {
        return new Chief(Objects.requireNonNull(name, "Director cannot be created without name"), List.of());
    }

    private record Staff(String name) implements Employee {
        @Override
        public String position() {
            return "сотрудник";
        }
    }

    private record Chief(String name, List<Employee> subordinates) implements Director {
        private Chief {
            subordinates = List.copyOf(subordinates);
        }

        @Override
        public String position() {
            return "директор";
        }
    }
}
