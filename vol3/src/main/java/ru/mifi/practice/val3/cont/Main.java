package ru.mifi.practice.val3.cont;

import ru.mifi.practice.val3.cont.sevices.OpenMeteo;
import ru.mifi.practice.val3.cont.sevices.WhoIs;

public abstract class Main {
    public static void main(String[] args) {
        Http http = new Http.Ok(new Deserializer.GsonDeserializer());
        Location location = new WhoIs(http);
        Weather weather = new OpenMeteo(http);
        location.get().flatMap(place -> weather.get(place.latitude(), place.longitude()))
            .ifPresent(System.out::println);
    }
}
