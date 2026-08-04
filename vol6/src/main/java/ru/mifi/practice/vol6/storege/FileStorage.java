package ru.mifi.practice.vol6.storege;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ru.mifi.practice.vol6.repository.Repository;
import ru.mifi.practice.vol6.repository.RepositoryMutant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * Хранит репозиторий в JSON-файле. Отсутствие файла — обычное дело при первом запуске,
 * а вот испорченный файл или отказ диска прячутся молча только в плохом коде, поэтому
 * они превращаются в исключение.
 */
final class FileStorage implements Storage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path users;

    FileStorage(Path users) {
        this.users = Objects.requireNonNull(users, "Storage cannot work without path");
    }

    @Override
    public <T> void write(Repository<T, String> repository) {
        try {
            Files.writeString(users, gson.toJson(repository.findAll()),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write users to " + users, e);
        }
    }

    @Override
    public <T> RepositoryMutant<T, String> read(RepositoryMutant<T, String> repository) {
        if (!Files.exists(users)) {
            return repository;
        }
        try {
            T[] items = gson.fromJson(Files.readString(users, StandardCharsets.UTF_8), repository.arrayType());
            repository.addAll(items == null ? List.of() : List.of(items));
        } catch (IOException | JsonSyntaxException e) {
            throw new IllegalStateException("Cannot read users from " + users, e);
        }
        return repository;
    }
}
