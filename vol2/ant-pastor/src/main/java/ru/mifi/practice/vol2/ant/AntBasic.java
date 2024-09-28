package ru.mifi.practice.vol2.ant;

public final class AntBasic implements Ant {

    private State state;

    public AntBasic() {
        state = State.S1;
    }

    @Override
    public String getName() {
        return "basic";
    }

    @Override
    public Step step(Sensor sensor) {
        switch (state) {
            case S1 -> {
                if (sensor.hasFood()) {
                    return Step.FORWARD;
                }
                state = State.S2;
                return Step.SPIN_RIGHT;
            }
            case S2 -> {
                if (sensor.hasFood()) {
                    state = State.S1;
                    return Step.FORWARD;
                }
                state = State.S3;
                return Step.SPIN_RIGHT;
            }
            case S3 -> {
                if (sensor.hasFood()) {
                    state = State.S1;
                    return Step.FORWARD;
                }
                state = State.S4;
                return Step.SPIN_RIGHT;
            }
            case S4 -> {
                if (sensor.hasFood()) {
                    state = State.S1;
                    return Step.FORWARD;
                }
                state = State.S5;
                return Step.SPIN_RIGHT;
            }
            case S5 -> {
                state = State.S1;
                return Step.FORWARD;
            }
            default -> {
                return Step.STOP;
            }
        }

    }

    private enum State {
        S1, S2, S3, S4, S5
    }
}
