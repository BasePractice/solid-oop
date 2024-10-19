package ru.mifi.practice.val3.cont;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

@FunctionalInterface
public interface Location {

    static Location fixed(Place place) {
        return () -> Optional.of(place);
    }

    static Location fixed(float latitude, float longitude) {
        return fixed(new Place(latitude, longitude));
    }

    Optional<Place> get();

    record Place(@SerializedName("latitude") float latitude,
                 @SerializedName("longitude") float longitude) {
    }
}
