package ru.mifi.practice.vol1.agent;

import java.util.Optional;

public interface Transport {

    Optional<Replay> send(Object target, Message message);

    default Optional<Replay> send(Message message) {
        return send(this, message);
    }

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
                return (target, message) ->
                        environment.receive(target, Default.this, message);
            }
        }
    }
}
