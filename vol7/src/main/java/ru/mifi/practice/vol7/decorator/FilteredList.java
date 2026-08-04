package ru.mifi.practice.vol7.decorator;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Декоратор списка: пропускает внутрь только те элементы, которые проходят условие,
 * а на остальные отвечает отказом. Сам список не хранит ничего — вся работа делегируется
 * обёрнутому.
 */
public final class FilteredList<T> extends AbstractList<T> {
    private final List<T> decorated;
    private final Predicate<T> predicate;

    public FilteredList(List<T> decorated, Predicate<T> predicate) {
        this.decorated = Objects.requireNonNull(decorated, "Decorated list cannot be null");
        this.predicate = Objects.requireNonNull(predicate, "Predicate cannot be null");
    }

    @Override
    public T set(int index, T element) {
        reject(element);
        return decorated.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        reject(element);
        decorated.add(index, element);
        modCount++;
    }

    @Override
    public T remove(int index) {
        T removed = decorated.remove(index);
        modCount++;
        return removed;
    }

    @Override
    public T get(int index) {
        return decorated.get(index);
    }

    @Override
    public int size() {
        return decorated.size();
    }

    private void reject(T element) {
        if (!predicate.test(element)) {
            throw new IllegalArgumentException("Element " + element + " does not pass the filter");
        }
    }
}
