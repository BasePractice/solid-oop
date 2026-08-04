package ru.mifi.practice.val5.mapper.dto;

import java.time.LocalDateTime;

/**
 * То, что уходит наружу по HTTP. Отделено от модели намеренно: у поля здесь другое имя
 * ({@code releaseDate} вместо {@code buildDateTime}), и менять модель ради формата
 * ответа не приходится.
 *
 * <p>Сеттеры оставлены как есть — это классический JavaBean, с которым MapStruct
 * работает по умолчанию, и заодно повод посмотреть на сгенерированный маппер.
 */
public final class VersionDto {
    private String version;
    private LocalDateTime releaseDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
}
