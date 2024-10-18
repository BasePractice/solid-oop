package ru.mifi.practice.val3.hasq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public interface Chain {

    public static void main(String[] args) throws IOException {
        newChain(new Token("1000000000"), "init")
            .add("init", "Next")
            .add("init", "Next")
            .add("init", "Next")
            .write(Path.of("1000000000.csv"));
    }

    static Chain newChain(Token token, String passphrase) {
        return new Default(token).add(passphrase, "Created");
    }

    Chain write(Path path) throws IOException;

    Chain add(String passphrase, String note);

    interface Value {
        String value();
    }

    final class Default implements Chain {
        private final List<Record> lines = new ArrayList<>();
        private final Token token;

        Default(Token token) {
            this.token = token;
        }

        @Override
        public Chain add(String passphrase, String note) {
            if (lines.isEmpty()) {
                Key key = createKey(0, passphrase);
                lines.add(new Record(0, token, key, note));
            } else {
                Record line = lines.get(lines.size() - 1);
                int id = line.id + 1;
                Key key = createKey(id, passphrase);
                Generator generator = createGenerator(line.id, key);
                line.generator = generator;
                lines.add(new Record(id, token, key, note));
                if (lines.size() > 2) {
                    line = lines.get(lines.size() - 3);
                    line.owner = createOwner(line.id, generator);
                }
            }
            return this;
        }

        public Chain write(Path path) throws IOException {
            Files.write(path, lines.stream().map(Record::toString).toList(), StandardOpenOption.CREATE_NEW);
            return this;
        }

        public void read(Path path) throws IOException {
            Files.readAllLines(path).forEach(line -> {
                String[] parts = line.split(";");
                lines.add(Record.from(parts));
            });
        }

        private Key createKey(int id, String passphrase) {
            return new Key(id, Utils.hash(String.valueOf(id), token.value(), passphrase));
        }

        private Generator createGenerator(int id, Key key) {
            return new Generator(id, Utils.hash(String.valueOf(id), token.value(), key.value()));
        }

        private Owner createOwner(int id, Generator generator) {
            return new Owner(id, Utils.hash(String.valueOf(id), token.value(), generator.value()));
        }
    }

    final class Record {
        private final int id;
        private final Token token;
        private final Key key;
        private final String note;
        private Generator generator;
        private Owner owner;

        private Record(int id, Token token, Key key, String note) {
            this(id, token, key, note, null, null);
        }

        public Record(int id, Token token, Key key, String note, Generator generator, Owner owner) {
            this.id = id;
            this.token = token;
            this.key = key;
            this.note = note;
            this.generator = generator;
            this.owner = owner;
        }

        private static Record from(String[] parts) {
            int id = Integer.parseInt(parts[0]);
            if (parts.length == 6) {
                return new Record(
                    id,
                    new Token(parts[1]),
                    new Key(id, parts[2]),
                    parts[5],
                    new Generator(id, parts[3]),
                    new Owner(id, parts[4])
                );
            } else {
                return new Record(
                    id,
                    new Token(parts[1]),
                    new Key(id, parts[2]),
                    parts[5]
                );
            }
        }

        @Override
        public String toString() {
            return String.format("%05d;%s;%s;%s;%s;%s",
                id, token.value(), key.value(),
                Objects.requireNonNullElse(generator, ""),
                Objects.requireNonNullElse(owner, ""),
                note);
        }
    }

    record Key(int id, String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }

    record Generator(int id, String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }

    record Token(String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }

    record Owner(int id, String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }

    final class Utils {
        private Utils() {
        }

        private static String hash(Object... objects) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                for (Object object : objects) {
                    digest.update(object.toString().getBytes());
                }
                byte[] bytes = digest.digest();
                return HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
