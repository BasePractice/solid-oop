package ru.mifi.practice.vol1.agent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ru.mifi.practice.vol1.agent.Event.EventMessage;
import static ru.mifi.practice.vol1.agent.Event.Listener;
import static ru.mifi.practice.vol1.agent.Transport.Message;
import static ru.mifi.practice.vol1.agent.Transport.Replay;

public interface Environment extends Using {

    Snapshot snapshot();

    void register(Agent.Factory factory);

    void register(Agent.Iterator iterator);

    void subscribe(Listener listener);

    default void tick() {
        tick(snapshot());
    }

    Optional<Replay> receive(Object target, Object source, Message message);

    interface Snapshot {

    }

    interface Factory {
        Environment create(Transport.Factory factory);

        final class Default implements Factory {

            @Override
            public Environment create(Transport.Factory factory) {
                return new Environment.Default(factory);
            }
        }
    }

    final class Default implements Environment, Snapshot {
        private final Map<Object, Agent> agents = new HashMap<>();
        private final Set<Listener> listeners = new HashSet<>();
        private final Transport transport;

        private Default(Transport.Factory factory) {
            this.transport = factory.create(this);
        }

        @Override
        public Snapshot snapshot() {
            return this;
        }

        @Override
        public void register(Agent.Factory factory) {
            register(factory.create(transport));
        }

        private void register(Agent agent) {
            agents.put(agent.id(), agent);
            subscribe(agent);
        }

        @Override
        public void register(Agent.Iterator iterator) {
            Optional<Agent> agent = iterator.next(transport);
            while (agent.isPresent()) {
                register(agent.get());
                agent = iterator.next(transport);
            }
        }

        @Override
        public void subscribe(Listener listener) {
            listeners.add(listener);
        }

        @Override
        public Optional<Replay> receive(Object target, Object source, Message message) {
            if (target == this || target == null) {
                listeners.forEach(listener -> listener.onEvent(
                        new EventMessage(message, Default.this, source)));
            }
            Agent agent = agents.get(target);
            if (agent != null) {
                return agent.call(message);
            }
            return Optional.empty();
        }

        @Override
        public void tick(Snapshot snapshot) {
            for (Agent agent : agents.values()) {
                agent.tick(snapshot);
            }
        }
    }
}
