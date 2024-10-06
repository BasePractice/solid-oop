package ru.mifi.practice.vol1.agent;

import java.util.Optional;

import static ru.mifi.practice.vol1.agent.Transport.Message;
import static ru.mifi.practice.vol1.agent.Transport.Replay;

public interface Agent extends Using, Event.Listener {

    Object id();

    Optional<Replay> call(Message message);

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @FunctionalInterface
    interface Factory {
        Agent create(Transport transport);
    }

    @FunctionalInterface
    interface Iterator {
        Optional<Agent> next(Transport transport);
    }
}
