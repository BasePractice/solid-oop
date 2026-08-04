package ru.mifi.practice.val5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Точка входа сервиса. Класс не абстрактный намеренно: Spring поднимает его как бин,
 * и только поэтому обработчик {@link ApplicationReadyEvent} вообще вызывается.
 */
@SpringBootApplication
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready(ApplicationReadyEvent event) {
        if (LOG.isInfoEnabled()) {
            LOG.info("http://127.0.0.1:{}/swagger-ui/index.html",
                event.getApplicationContext().getEnvironment().getProperty("server.port"));
        }
    }
}
