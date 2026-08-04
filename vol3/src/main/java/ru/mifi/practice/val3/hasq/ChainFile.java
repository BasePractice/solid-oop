package ru.mifi.practice.val3.hasq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Цепочка, лежащая в CSV-файле: одна строка — одно звено, поля разделены точкой с запятой.
 * Объект знает свой путь, поэтому вызывающему не нужно таскать его за собой.
 */
public final class ChainFile {
    private final Path path;

    public ChainFile(Path path) {
        this.path = Objects.requireNonNull(path, "Chain file cannot live without path");
    }

    void write(Record root) throws IOException {
        List<Record> records = new ArrayList<>();
        Record.createList(root, records);
        Files.write(path, records.stream().map(Record::toString).toList(), StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    Chain read(Hash hash) throws IOException {
        Record current = null;
        Record root = null;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            Record previous = current;
            current = Record.from(current, line.split(";"), hash);
            if (root == null) {
                root = current;
            }
            if (previous != null) {
                previous.setNext(current);
            }
        }
        if (root == null) {
            throw new IllegalStateException("Chain file " + path + " is empty");
        }
        return Chain.loadChain(root);
    }
}
