package ru.mifi.practice.vol1.agent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ru.mifi.practice.vol1.agent.Event.EventMessage;
import static ru.mifi.practice.vol1.agent.Event.Listener;
import static ru.mifi.practice.vol1.agent.Transport.Message;
import static ru.mifi.practice.vol1.agent.Transport.Replay;

/**
 * Среда, в которой живут агенты: раздаёт им транспорт, разносит события и двигает время.
 * Рассылка идёт по копии списка слушателей — обработчик вправе зарегистрировать
 * нового агента прямо во время рассылки, и это не должно рушить обход.
 */
public interface Environment extends Using, Registrar {

    Snapshot snapshot();

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
        private final Transport.Factory factory;

        private Default(Transport.Factory factory) {
            this.factory = factory;
        }

        @Override
        public Snapshot snapshot() {
            return this;
        }

        @Override
        public void register(Agent.Factory factory) {
            Transport transport = this.factory.create(this);
            register(factory.create(transport), transport);
        }

        @Override
        public void register(Agent.Iterator iterator) {
            Transport transport = factory.create(this);
            Optional<Agent> agent = iterator.next(transport);
            while (agent.isPresent()) {
                register(agent.get(), transport);
                transport = factory.create(this);
                agent = iterator.next(transport);
            }
        }

        private void register(Agent agent, Transport transport) {
            Object id = agent.id();
            if (agents.containsKey(id)) {
                throw new IllegalArgumentException("Agent " + id + " is already registered");
            }
            transport.own(agent);
            agents.put(id, agent);
            subscribe(agent);
        }

        @Override
        public void subscribe(Listener listener) {
            listeners.add(listener);
        }

        @Override
        public Optional<Replay> receive(Object target, Object source, Message message) {
            if (target == this || target == null) {
                for (Listener listener : List.copyOf(listeners)) {
                    listener.onEvent(new EventMessage(message, this, source));
                }
            }
            Agent agent = agents.get(target);
            if (agent == null) {
                return Optional.empty();
            }
            return agent.call(message);
        }

        @Override
        public void tick(Snapshot snapshot) {
            for (Agent agent : List.copyOf(agents.values())) {
                agent.tick(snapshot);
            }
        }
    }
}
