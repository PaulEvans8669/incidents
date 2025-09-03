package fr.paulevans.incidents;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class IncidentsApplication {

    @PostConstruct
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    public static void main(String[] args) {
        SpringApplication.run(IncidentsApplication.class, args);
    }

}
