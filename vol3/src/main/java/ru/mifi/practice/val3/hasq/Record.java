package ru.mifi.practice.val3.hasq;

import java.util.List;
import java.util.Objects;

import static ru.mifi.practice.val3.hasq.Chain.Detailed;
import static ru.mifi.practice.val3.hasq.Chain.Generator;
import static ru.mifi.practice.val3.hasq.Chain.Key;
import static ru.mifi.practice.val3.hasq.Chain.Owner;
import static ru.mifi.practice.val3.hasq.Chain.Token;
import static ru.mifi.practice.val3.hasq.Chain.ValidateType;

/**
 * Звено цепочки: хранит ключ, отпечаток следующего звена (generator) и отпечаток
 * звена через одно (owner). Порвать цепочку незаметно нельзя — при проверке отпечатки
 * пересчитываются и сравниваются.
 */
public final class Record {
    private static final int FIELDS = 6;
    final int id;
    private final Record parent;
    private final Token token;
    private final Key key;
    private final String note;
    private Record next;
    private Generator generator;
    private Owner owner;

    private Record(Record parent, int id, Token token, Key key, String note) {
        this(parent, id, token, key, note, null, null);
    }

    private Record(Record parent, int id, Token token, Key key, String note, Generator generator, Owner owner) {
        this.parent = parent;
        this.id = id;
        this.token = token;
        this.key = key;
        this.note = note;
        this.generator = generator;
        this.owner = owner;
    }

    static Record root(Token token, String passphrase) {
        return new Record(null, 0, token, token.key(0, passphrase), "NE");
    }

    static Record from(Record parent, String[] parts, Hash hash) {
        if (parts.length != FIELDS) {
            throw new IllegalArgumentException(
                "Record line needs " + FIELDS + " fields, got " + parts.length);
        }
        int id = Integer.parseInt(parts[0].trim());
        Token token = new Token(parts[1].trim(), hash);
        Key key = new Key(id, token, parts[2].trim());
        String generator = parts[3].trim();
        String owner = parts[4].trim();
        return new Record(
            parent, id, token, key, parts[5].trim(),
            generator.isEmpty() ? null : new Generator(id, key, generator),
            owner.isEmpty() ? null : new Owner(id, owner)
        );
    }

    static void createList(Record root, List<Record> children) {
        children.add(root);
        if (root.next != null) {
            createList(root.next, children);
        }
    }

    Token token() {
        return token;
    }

    void setGenerator(Generator generator) {
        this.generator = generator;
        if (parent != null) {
            parent.owner = generator.owner(parent.id);
        }
    }

    Record newRecord(Key key, String note) {
        next = new Record(this, key.id(), token, key, note, null, null);
        return next;
    }

    @Override
    public String toString() {
        return String.format("%05d;%s;%s;%s;%s;%s",
            id, token.value(), key.value(),
            Objects.requireNonNullElse(generator, ""), Objects.requireNonNullElse(owner, ""), note);
    }

    void setNext(Record next) {
        this.next = next;
    }

    public Result<ValidateType, Detailed> validate() {
        if (generator == null) {
            if (next == null) {
                return Result.ok(ValidateType.SUCCESS);
            }
            return Result.ok(ValidateType.NOT_COMPLETE);
        }
        if (next == null) {
            return Result.failure(new Detailed(this), "Record " + id + " has a generator but no next record");
        }
        String hash = token.hash(String.valueOf(next.id), token.value(), next.key.value());
        if (!hash.equals(generator.value())) {
            return Result.failure(new Detailed(next), "Hash for generator of record " + id + " does not match");
        }
        if (owner == null || next.next == null || next.generator == null) {
            return next.validate();
        }
        hash = token.hash(String.valueOf(next.next.id), token.value(), next.next.key.value(), next.generator.value());
        if (!hash.equals(owner.value())) {
            return Result.failure(new Detailed(next.next), "Hash for owner of record " + id + " does not match");
        }
        return next.validate();
    }
}
