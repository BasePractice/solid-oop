package ru.mifi.practice.vol6.storege;

import ru.mifi.practice.vol6.repository.Repository;
import ru.mifi.practice.vol6.repository.RepositoryMutant;

import java.nio.file.Path;

/**
 * Место, куда репозиторий складывается между запусками. Меню про файл не знает —
 * ему достаточно этого интерфейса.
 */
public interface Storage {

    static Storage file(Path path) {
        return new FileStorage(path);
    }

    <T> void write(Repository<T, String> repository);

    <T> RepositoryMutant<T, String> read(RepositoryMutant<T, String> repository);
}
