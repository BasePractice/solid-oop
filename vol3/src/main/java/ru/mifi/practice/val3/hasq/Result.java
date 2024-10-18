package ru.mifi.practice.val3.hasq;

public sealed interface Result<R, E> {

    static <R, E> Result<R, E> ok(R result) {
        return new Body<>(result, null, null);
    }

    static <R, E> Result<R, E> failure(E error, String message) {
        return new Body<>(null, error, message);
    }

    R getOr(R result);

    Error<E> error();

    final class Error<B> {
        final B value;
        final String error;

        private Error(B value, String error) {
            this.value = value;
            this.error = error;
        }

        @Override
        public String toString() {
            return value + "(" + error + ")";
        }
    }

    final class Body<R, E> implements Result<R, E> {
        private final R result;
        private final Error<E> error;

        private Body(R result, E error, String message) {
            this.result = result;
            this.error = new Error<>(error, message);
        }

        @Override
        public R getOr(R result) {
            return this.result == null ? result : this.result;
        }

        @Override
        public Error<E> error() {
            return error;
        }

        @Override
        public String toString() {
            return result == null ? error.toString() : result.toString();
        }
    }
}
