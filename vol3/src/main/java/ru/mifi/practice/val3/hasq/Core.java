package ru.mifi.practice.val3.hasq;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

public interface Core {

    Sequence newSequence(Token token, String passphrase);

    interface Sequence {

        void add(String passphrase, String note);

        final class Default implements Sequence {
            private final List<Line> lines = new ArrayList<>();

            Default(Line line) {
                lines.add(line);
            }

            private void add(Generator generator, String note) {
                Line last = lines.get(lines.size() - 1);
                last.generator = generator;
                last.note = note;
                if (lines.size() > 2) {
                    Line line = lines.get(lines.size() - 2);
                    line.owner = Utils.createOwner(line.id, line.token, generator);
                }
            }

            @Override
            public void add(String passphrase, String note) {
                Line line = lines.getLast();
                Key key = Utils.createKey(line.id + 1, line.token, passphrase);
                Generator generator = Utils.createGenerator(line.id, line.token, key);
                add(generator, note);
            }
        }
    }

    final class Line {
        private final int id;
        private final Token token;
        private final Key key;
        private Generator generator;
        private Owner owner;
        private String note;

        private Line(int id, Token token, Key key, Generator generator, Owner owner, String note) {
            this.id = id;
            this.token = token;
            this.key = key;
            this.generator = generator;
            this.owner = owner;
            this.note = note;
        }

        private Line(int id, Token token, Key key, String note) {
            this(id, token, key, null, null, note);
        }
    }

    final class Default implements Core {

        @Override
        public Sequence newSequence(Token token, String passphrase) {
            Key key = Utils.createKey(0, token, passphrase);
            return new Sequence.Default(new Line(0, token, key, null, null, "Create"));
        }
    }

    record Key(String value) {

    }

    record Generator(String value) {

    }

    record Token(String value) {

    }

    record Owner(String value) {

    }

    final class Utils {
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

        private static Key createKey(int i, Token token, String passphrase) {
            return new Key(hash(String.valueOf(i), token.value, passphrase));
        }

        private static Generator createGenerator(int i, Token token, Key key) {
            return new Generator(hash(String.valueOf(i), token.value, key.value));
        }

        private static Owner createOwner(int i, Token token, Generator generator) {
            return new Owner(hash(String.valueOf(i), token.value, generator.value));
        }
    }
}
