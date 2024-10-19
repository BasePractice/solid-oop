package ru.mifi.practice.val3.cont.sevices;

import com.google.gson.annotations.SerializedName;
import okhttp3.HttpUrl;
import ru.mifi.practice.val3.cont.Http;
import ru.mifi.practice.val3.cont.Weather;

import java.util.Objects;
import java.util.Optional;

public final class OpenMeteo implements Weather {
    private static final String URL = "https://api.open-meteo.com/v1/forecast";
    private final Http http;

    public OpenMeteo(Http http) {
        this.http = http;
    }

    @Override
    public Optional<Details> get(float latitude, float longitude) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(URL))
            .newBuilder()
            .addQueryParameter("latitude", String.valueOf(latitude))
            .addQueryParameter("longitude", String.valueOf(longitude))
            .addQueryParameter("current", "temperature_2m,wind_speed_10m")
            .build();
        return http.get(url.toString(), Result.class).map(result -> new Details(
            Float.parseFloat(result.current.temperature),
            Float.parseFloat(result.current.windSpeed)
        ));
    }

    private record Result(@SerializedName("current") Current current) {

    }

    private record Current(@SerializedName("temperature_2m") String temperature,
                           @SerializedName("wind_speed_10m") String windSpeed) {

    }
}
