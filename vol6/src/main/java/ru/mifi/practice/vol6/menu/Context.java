package ru.mifi.practice.vol6.menu;

import ru.mifi.practice.vol6.security.Authentication;
import ru.mifi.practice.vol6.transport.Input;
import ru.mifi.practice.vol6.transport.Output;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Сеанс работы с пользователем: ввод, вывод и текущая сессия. Меню обращается только
 * сюда и не знает, что за консолью стоит {@code System.in}.
 */
public final class Context implements Output, Input {
    private final Output output;
    private final Input input;
    private final AtomicReference<Authentication.Session> session = new AtomicReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public Context(Output output, Input input) {
        this.output = Objects.requireNonNull(output, "Context cannot work without output");
        this.input = Objects.requireNonNull(input, "Context cannot work without input");
    }

    @Override
    public void close() throws Exception {
        input.close();
    }

    @Override
    public String inputString() {
        return input.inputString();
    }

    @Override
    public Optional<Number> inputNumber() {
        return input.inputNumber();
    }

    @Override
    public void print(String format, Object... args) {
        output.print(format, args);
    }

    public void print() {
        output.println("Auth: %s", authorized()
            .map(current -> current.user().username())
            .orElse("не авторизован"));
    }

    @Override
    public void error(String format, Object... args) {
        output.error(format, args);
    }

    public String select(String text) {
        output.print(text);
        output.print("> ");
        return input.inputString();
    }

    public void putSession(Authentication.Session session) {
        this.session.set(Objects.requireNonNull(session, "Session cannot be null"));
    }

    public void clearSession() {
        session.set(null);
    }

    public Optional<Authentication.Session> authorized() {
        return Optional.ofNullable(session.get());
    }

    public boolean running() {
        return running.get();
    }

    public void stop() {
        running.set(false);
    }
}
