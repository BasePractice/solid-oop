package ru.mifi.practice.vol6.repository;

import java.util.List;
import java.util.Optional;

/**
 * Читающая половина репозитория. {@code arrayType} нужен хранилищу, чтобы восстановить
 * элементы из JSON: обобщённый тип в рантайме стирается, а класс массива — нет,
 * поэтому рефлексия не требуется.
 */
public interface Repository<T, I> {

    List<T> findAll();

    Optional<T> search(I id);

    Class<T[]> arrayType();
}
