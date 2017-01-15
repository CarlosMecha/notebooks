package com.carlosmecha.notebooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Main entrypoint for the application.
 */
@SpringBootApplication
@EntityScan(basePackageClasses = NotebooksApplication.class)
public class NotebooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotebooksApplication.class, args);
    }

}
