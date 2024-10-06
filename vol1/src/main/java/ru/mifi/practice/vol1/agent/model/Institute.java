package ru.mifi.practice.vol1.agent.model;

import ru.mifi.practice.vol1.agent.Agent;
import ru.mifi.practice.vol1.agent.Environment;
import ru.mifi.practice.vol1.agent.Event;
import ru.mifi.practice.vol1.agent.Transport;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public final class Institute {
    private final Environment environment;

    public Institute() {
        this.environment = new Environment.Factory.Default().create(new Transport.Factory.Default());
        this.environment.register(new Agent.Iterator() {
            private final Human[] humans = new Human[]{
                new Human("Петрович", Lector.class),
                new Human("Саша", Student.class),
                new Human("Маша", Student.class),
                new Human("Наташа", Student.class),
            };
            private int index = 0;

            @Override
            public Optional<Agent> next(Transport transport) {
                if (index >= humans.length) {
                    return Optional.empty();
                }
                return Optional.of(humans[index++].create(transport));
            }
        });
        this.environment.subscribe(new Event.Listener() {
            @Override
            public void onEvent(Event event) {
                if (event instanceof Event.EventMessage eventMessage
                    && eventMessage.message() instanceof Messages message
                    && eventMessage.source() != this && message == Messages.SEND_FLY) {
                    environment.register(new Agent.Factory() {
                        private int flyCounter = 0;

                        @Override
                        public Agent create(Transport transport) {
                            return new Fly(String.format("fly_%d", ++flyCounter),
                                eventMessage.source(), transport);
                        }
                    });
                }
            }
        });
        this.environment.subscribe(System.out::println);
    }

    public void tick() {
        environment.tick();
    }

    enum Messages implements Transport.Replay {
        NOISE, WAT, SEND_FLY
    }

    private record Human(String name, Class<? extends Agent> klass) implements Agent.Factory {
        public Agent create(Transport transport) {
            try {
                var constructor = klass().getConstructor(String.class, Transport.class);
                constructor.setAccessible(true);
                return constructor.newInstance(name(), transport);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                     | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private record Lector(String id, Transport transport) implements Agent {

        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof Event.EventMessage eventMessage
                && eventMessage.message() instanceof Messages message
                && eventMessage.source() != this) {
                switch (message) {
                    case NOISE: {
                        transport.send(Messages.WAT);
                        break;
                    }
                    case WAT: {
                        //Skip
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            throw new UnsupportedOperationException();
        }
    }

    private record Student(String id, Transport transport) implements Agent {

        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            if (message instanceof Messages msg) {
                return Optional.empty();
            }
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof Event.EventMessage eventMessage
                && eventMessage.message() instanceof Messages message
                && eventMessage.source() != this) {
                switch (message) {
                    case NOISE: {
                        //Skip
                        break;
                    }
                    case WAT: {
                        //TODO:
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            throw new UnsupportedOperationException();
        }
    }

    private record Fly(String id, Object owner, Transport transport) implements Agent {

        @Override
        public Optional<Transport.Replay> call(Transport.Message message) {
            return Optional.empty();
        }

        @Override
        public void onEvent(Event event) {
            //TODO:
        }

        @Override
        public void tick(Environment.Snapshot snapshot) {
            //TODO:
        }
    }
}
