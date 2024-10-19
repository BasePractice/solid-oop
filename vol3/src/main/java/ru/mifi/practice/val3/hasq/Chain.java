package ru.mifi.practice.val3.hasq;

public interface Chain {
    static Chain newChain(Token token, String passphrase) {
        return new Default(token, passphrase);
    }

    static Chain loadChain(Record root) {
        return new Default(root);
    }

    default Chain add(String passphrase, String note) {
        Key key = nextKey(passphrase);
        return add(key, note);
    }

    Chain add(Key key, String note);

    Record root();

    Key nextKey(String passphrase);

    Result<ValidateType, Detailed> validate();

    enum ValidateType {
        SUCCESS,
        FAILURE,
        NOT_STARTED,
        NOT_COMPLETE
    }

    interface Value {
        String value();
    }

    record Detailed(Record record) {

    }

    final class Default implements Chain {
        private final Record root;
        private final Token token;
        private Record current;

        Default(Token token, String passphrase) {
            this.token = token;
            this.root = Record.root(token, passphrase);
            this.current = root;
        }

        Default(Record root) {
            this.root = root;
            this.token = root.token();
            this.current = root;
        }

        @Override
        public Record root() {
            return root;
        }

        @Override
        public Chain add(Key key, String note) {
            Generator generator = key.generator(current.id);
            current.setGenerator(generator);
            current = current.newRecord(key, note);
            return this;
        }

        @Override
        public Key nextKey(String passphrase) {
            return token.key(current.id + 1, passphrase);
        }

        @Override
        public Result<ValidateType, Detailed> validate() {
            if (root == null) {
                return Result.ok(ValidateType.NOT_STARTED);
            }
            try {
                return root.validate();
            } catch (Exception ex) {
                return Result.failure(null, ex.getMessage());
            }
        }
    }

    record Key(int id, Token token, String value) implements Value, Hash {
        @Override
        public String toString() {
            return value;
        }

        public Generator generator(int id) {
            return new Generator(id, this, hash(String.valueOf(this.id), token.value, value));
        }

        @Override
        public String hash(Object... args) {
            return token.hash(args);
        }
    }

    record Generator(int id, Key key, String value) implements Value, Hash {
        @Override
        public String toString() {
            return value;
        }

        public Owner owner(int id) {
            return new Owner(id, hash(String.valueOf(key.id), key.token.value, key.value, value));
        }

        @Override
        public String hash(Object... args) {
            return key.hash(args);
        }
    }

    record Token(String value, Hash hash) implements Value, Hash {
        @Override
        public String toString() {
            return value;
        }

        public Key key(int n, String passphrase) {
            return new Key(n, this, hash(String.valueOf(n), value(), passphrase));
        }

        @Override
        public String hash(Object... args) {
            return hash.hash(args);
        }
    }

    record Owner(int id, String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }
}
