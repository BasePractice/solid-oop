package ru.mifi.practice.val3.cont.business;

import ru.mifi.practice.val3.cont.BusinessLogic;
import ru.mifi.practice.val3.cont.Location;
import ru.mifi.practice.val3.cont.Weather;

/**
 * Вся задача: узнать, где мы, и спросить погоду для этого места. Ни про HTTP,
 * ни про JSON здесь не известно — только два интерфейса, полученных снаружи.
 * Поэтому логику можно проверить, подсунув фейковые {@link Location} и {@link Weather}.
 */
public record DefaultLogic(Location location, Weather weather) implements BusinessLogic {
    @Override
    public void execute() {
        location.get()
            .flatMap(place -> weather.get(place.latitude(), place.longitude()))
            .ifPresent(System.out::println);
    }
}
