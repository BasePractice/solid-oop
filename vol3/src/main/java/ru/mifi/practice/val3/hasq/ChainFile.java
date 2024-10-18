package ru.mifi.practice.val3.hasq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public abstract class ChainFile {
    static void write(Path path, Record root) throws IOException {
        List<Record> records = new ArrayList<>();
        Record.createList(root, records);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.write(path, records.stream().map(Record::toString).toList(), StandardOpenOption.CREATE_NEW);
    }

    static Chain read(Path path, Hash hash) throws IOException {
        Record current = null;
        Record root = null;
        for (String line : Files.readAllLines(path)) {
            String[] parts = line.split(";");
            Record prev = current;
            current = Record.from(current, parts, hash);
            if (root == null) {
                root = current;
            }
            if (prev != null) {
                prev.setNext(current);
            }
        }
        return Chain.newChain(root);
    }
}
