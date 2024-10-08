package ru.mifi.practice.vol0.klass;

import java.util.Arrays;

public class Fly {
    Wing leftWing;
    Wing rightWing;
    Eye leftEye;
    Eye rightEye;
    Leg[] legs;
    Trunk trunk;

    public Fly() {
    }

    public Fly(Wing leftWing, Wing rightWing,
               Eye leftEye, Eye rightEye,
               Leg[] legs, Trunk trunk) {
        this.leftWing = leftWing;
        this.rightWing = rightWing;
        this.leftEye = leftEye;
        this.rightEye = rightEye;
        this.legs = legs;
        this.trunk = trunk;
    }

    public static void main(String[] args) {
        Fly fly = new Fly();
        fly.leftWing = new Wing();
        fly.rightWing = new Wing();
        fly.leftEye = new Eye();
        fly.rightEye = new Eye();
        fly.legs = new Leg[]{
            new Leg(),
            new Leg(),
            new Leg(),
            new Leg(),
            new Leg(),
            new Leg()
        };
        fly.trunk = new Trunk();
        System.out.println(fly);
        System.out.println(fly.validate());
    }

    boolean validate() {
        return leftWing != null && rightWing != null
            && leftEye != null && rightEye != null
            && legs != null && legs.length == 6
            && trunk != null;
    }

    @Override
    public String toString() {
        return "Fly{" +
            "leftWing=" + leftWing +
            ", rightWing=" + rightWing +
            ", leftEye=" + leftEye +
            ", rightEye=" + rightEye +
            ", legs=" + Arrays.toString(legs) +
            ", trunk=" + trunk +
            '}';
    }

    public static class Wing {
    }

    public static class Eye {
    }

    public static class Leg {
    }

    public static class Trunk {

    }
}
