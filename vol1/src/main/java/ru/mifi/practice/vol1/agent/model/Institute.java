package ru.mifi.practice.vol1.agent.model;

import ru.mifi.practice.vol1.agent.Agent;
import ru.mifi.practice.vol1.agent.Environment;
import ru.mifi.practice.vol1.agent.Event;
import ru.mifi.practice.vol1.agent.Transport;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class Institute implements Agent.Iterator {
    private final Agent[] agents = new Agent[]{
            new Lector(), new Student("Саша"), new Student("Маша"), new Student("Наташа")
    };
    private final AtomicInteger agentIndex = new AtomicInteger(0);
    private final Environment environment;

    public Institute(Transport.Factory transportFactory, Environment.Factory environmentFactory) {
        this.environment = environmentFactory.create(transportFactory);
        this.environment.register(this);
    }

    @Override
    public Optional<Agent> next(Transport transport) {
        if (agentIndex.incrementAndGet() >= agents.length) {
            return Optional.empty();
        }
        return Optional.of(agents[agentIndex.getAndIncrement()]);
    }

    public void tick() {
        environment.tick();
    }

    private static final class Lector implements Agent {

        @Override
        public Object id() {
            return "Петрович";
        }

        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {

        }

        @Override
        public void tick(Environment.Snapshot snapshot) {

        }
    }

    private record Student(String id) implements Agent {

        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {

        }

        @Override
        public void tick(Environment.Snapshot snapshot) {

        }
    }
}
