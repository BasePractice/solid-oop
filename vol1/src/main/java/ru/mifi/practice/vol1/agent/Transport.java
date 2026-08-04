package ru.mifi.practice.vol1.agent;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Канал агента в среду. У каждого агента свой транспорт, и он помнит владельца —
 * иначе среда не смогла бы сказать получателям, кто именно прислал сообщение,
 * и агент отвечал бы сам себе.
 *
 * <p>Владелец проставляется сразу после создания агента: раньше его просто не существует,
 * ведь транспорт нужен агенту уже в конструкторе.
 */
public interface Transport {

    Optional<Replay> send(Object target, Message message);

    default Optional<Replay> send(Message message) {
        return send(null, message);
    }

    void own(Object owner);

    interface Message {

    }

    interface Replay extends Message {

    }

    @FunctionalInterface
    interface Factory {
        Transport create(Environment environment);

        final class Default implements Factory {
            @Override
            public Transport create(Environment environment) {
                return new Owned(environment);
            }
        }
    }

    final class Owned implements Transport {
        private final Environment environment;
        private final AtomicReference<Object> owner = new AtomicReference<>();

        Owned(Environment environment) {
            this.environment = Objects.requireNonNull(environment, "Transport cannot work without environment");
        }

        @Override
        public Optional<Replay> send(Object target, Message message) {
            return environment.receive(target, owner.get(), message);
        }

        @Override
        public void own(Object owner) {
            if (!this.owner.compareAndSet(null, owner)) {
                throw new IllegalStateException("Transport already belongs to " + this.owner.get());
            }
        }
    }
}
