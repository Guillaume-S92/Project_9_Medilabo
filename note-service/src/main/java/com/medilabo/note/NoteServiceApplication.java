package com.medilabo.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
}
