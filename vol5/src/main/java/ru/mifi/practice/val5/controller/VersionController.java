package ru.mifi.practice.val5.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mifi.practice.val5.mapper.VersionMapper;
import ru.mifi.practice.val5.mapper.dto.VersionDto;
import ru.mifi.practice.val5.model.VersionModel;

/**
 * Отдаёт версию сервиса. Наружу уходит DTO, а не модель — иначе переименование поля
 * внутри сразу ломает контракт для клиентов.
 */
@Slf4j
@RestController
@RequestMapping("/version")
public class VersionController {

    @Operation(summary = "Версия сервиса")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Версия", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = VersionDto.class))
        })
    })
    @GetMapping
    public ResponseEntity<VersionDto> version() {
        VersionModel current = VersionModel.current();
        if (log.isDebugEnabled()) {
            log.debug("Отдаём версию {}", current.getVersion());
        }
        return ResponseEntity.ok(VersionMapper.DEFAULT.toVersionDto(current));
    }
}
