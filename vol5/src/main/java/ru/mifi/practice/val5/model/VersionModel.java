package ru.mifi.practice.val5.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class VersionModel {
    @NotBlank
    @JsonProperty("version")
    private final String version;
    @NotNull
    @JsonProperty("build_date")
    private final LocalDateTime buildDateTime;

    @JsonCreator
    private VersionModel(@JsonProperty("version") String version,
                         @JsonProperty("build_date") LocalDateTime buildDateTime) {
        this.version = version;
        this.buildDateTime = buildDateTime;
    }

    public static VersionModel current() {
        return new VersionModel("v1", LocalDateTime.now());
    }

    public @NotBlank String getVersion() {
        return version;
    }

    public @NotNull LocalDateTime getBuildDateTime() {
        return buildDateTime;
    }
}
