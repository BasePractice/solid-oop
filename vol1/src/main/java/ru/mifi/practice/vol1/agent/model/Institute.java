package ru.mifi.practice.vol1.agent.model;

import ru.mifi.practice.vol1.agent.Agent;
import ru.mifi.practice.vol1.agent.Environment;
import ru.mifi.practice.vol1.agent.Event;
import ru.mifi.practice.vol1.agent.Transport;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * Аудитория как набор агентов. Студенты шумят, лектор отзывается, а на сигнал
 * SEND_FLY в аудитории заводится ещё одна муха — так видно, что агенты появляются
 * прямо во время работы среды, а не только при старте.
 */
public final class Institute {
    private static final int FLY_TICK = 2;
    private final Environment environment;
    private final AtomicInteger flies = new AtomicInteger();
    private final AtomicInteger ticks = new AtomicInteger();

    public Institute() {
        this.environment = new Environment.Factory.Default().create(new Transport.Factory.Default());
        this.environment.register(new Agent.Iterator() {
            private final Human[] humans = new Human[]{
                new Human("Петрович", Lector::new),
                new Human("Саша", Student::new),
                new Human("Маша", Student::new),
                new Human("Наташа", Student::new),
            };
            private int index;

            @Override
            public Optional<Agent> next(Transport transport) {
                if (index >= humans.length) {
                    return Optional.empty();
                }
                return Optional.of(humans[index++].create(transport));
            }
        });
        this.environment.subscribe(event -> {
            if (event instanceof Event.EventMessage message && message.message() == Messages.SEND_FLY) {
                hatch();
            }
        });
        this.environment.subscribe(System.out::println);
    }

    public void tick() {
        if (ticks.incrementAndGet() == FLY_TICK) {
            environment.receive(null, this, Messages.SEND_FLY);
        }
        environment.tick();
    }

    private void hatch() {
        String id = String.format("fly_%d", flies.incrementAndGet());
        Agent.Factory factory = transport -> new Fly(id, transport);
        environment.register(factory);
    }

    enum Messages implements Transport.Replay {
        NOISE, WAT, SEND_FLY
    }

    private record Human(String name, BiFunction<String, Transport, Agent> maker) implements Agent.Factory {
        @Override
        public Agent create(Transport transport) {
            return maker.apply(name, transport);
        }
    }

    private record Lector(String id, Transport transport) implements Agent {
        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof Event.EventMessage message
                && message.message() == Messages.NOISE
                && message.source() != this) {
                transport.send(Messages.WAT);
            }
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            //Лектор ничего не начинает сам — он только отвечает на шум
        }

        @Override
        public String toString() {
            return "Лектор " + id;
        }
    }

    private record Student(String id, Transport transport) implements Agent {
        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof Event.EventMessage message
                && message.message() == Messages.WAT
                && message.source() != this) {
                System.out.printf("%s затих%n", this);
            }
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            transport.send(Messages.NOISE);
        }

        @Override
        public String toString() {
            return "Студент " + id;
        }
    }

    private record Fly(String id, Transport transport) implements Agent {
        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            //Муху происходящее в аудитории не касается
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            System.out.printf("%s жужжит%n", this);
        }

        @Override
        public String toString() {
            return "Муха " + id;
        }
    }
}
