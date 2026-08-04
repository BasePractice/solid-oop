package ru.mifi.practice.vol7;

import ru.mifi.practice.vol7.adapter.Adapter;
import ru.mifi.practice.vol7.decorator.FilteredList;
import ru.mifi.practice.vol7.factory.Director;
import ru.mifi.practice.vol7.factory.EmployeeFactory;
import ru.mifi.practice.vol7.factory.impl.ConcreteEmployeeFactory;
import ru.mifi.practice.vol7.proxy.DynamicProxy;
import ru.mifi.practice.vol7.proxy.Simple;
import ru.mifi.practice.vol7.proxy.SimpleProxy;
import ru.mifi.practice.vol7.singletone.Singletone1;
import ru.mifi.practice.vol7.singletone.Singletone2;
import ru.mifi.practice.vol7.singletone.Singletone3;

import java.util.ArrayList;
import java.util.List;

/**
 * Прогон паттернов из модуля: у каждого видно поведение, а не только объявление.
 * Порядок совпадает с порядком разбора на занятии.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        factory();
        decorator();
        adapter();
        proxy();
        singleton();
    }

    private static void factory() {
        EmployeeFactory factory = new ConcreteEmployeeFactory();
        Director director = factory.createDirector("Петрович");
        System.out.printf("Фабрика: %s — %s, подчинённых %d%n",
            director.name(), director.position(), director.subordinates().size());
        System.out.printf("Фабрика: %s — %s%n",
            factory.createEmployee("Саша").name(), factory.createEmployee("Саша").position());
    }

    private static void decorator() {
        List<String> filtered = new FilteredList<>(new ArrayList<>(), text -> text.length() > 3);
        filtered.add("Маша");
        System.out.printf("Декоратор: принято %s%n", filtered);
        try {
            filtered.add("Ян");
        } catch (IllegalArgumentException ex) {
            System.out.printf("Декоратор: отказ — %s%n", ex.getMessage());
        }
    }

    private static void adapter() {
        System.out.printf("Адаптер: %s%n", new Adapter(() -> "Наташа").title().orElseThrow());
    }

    private static void proxy() {
        Simple origin = new Simple() {
            @Override
            public int doSomething() {
                return 0;
            }

            @Override
            public int doSomethingElse() {
                return 2;
            }
        };
        Simple statical = new SimpleProxy(origin);
        Simple dynamical = new DynamicProxy(origin).proxy();
        System.out.printf("Прокси статический: подменено %d, прозрачно %d%n",
            statical.doSomething(), statical.doSomethingElse());
        System.out.printf("Прокси динамический: подменено %d, прозрачно %d%n",
            dynamical.doSomething(), dynamical.doSomethingElse());
    }

    private static void singleton() {
        System.out.printf("Синглтон: %b %b %b%n",
            Singletone1.getInstance() == Singletone1.getInstance(),
            Singletone2.getInstance() == Singletone2.getInstance(),
            Singletone3.getInstance() == Singletone3.getInstance());
    }
}
