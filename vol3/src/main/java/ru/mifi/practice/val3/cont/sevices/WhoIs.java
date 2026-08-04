package ru.mifi.practice.val3.cont.sevices;

import ru.mifi.practice.val3.cont.Http;
import ru.mifi.practice.val3.cont.Location;

import java.util.Optional;

/**
 * Определяет координаты по внешнему IP. Реализация {@link Location} — значит,
 * в тесте её можно заменить на {@code Location.fixed(...)} и в сеть не ходить.
 */
public final class WhoIs implements Location {
    private static final String URL = "https://ipwho.is";
    private final Http http;

    public WhoIs(Http http) {
        this.http = http;
    }

    @Override
    public Optional<Place> get() {
        return http.get(URL, Place.class);
    }
}
