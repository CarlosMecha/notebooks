package com.carlosmecha.notebooks;

import com.carlosmecha.notebooks.authentication.UserMethodArgumentResolver;
import com.carlosmecha.notebooks.notebooks.NotebookMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Main entrypoint for the application.
 */
@SpringBootApplication
@EntityScan(basePackageClasses = NotebooksApplication.class)
public class NotebooksApplication extends WebMvcConfigurerAdapter {

    @Autowired
    private UserMethodArgumentResolver users;

    @Autowired
    private NotebookMethodArgumentResolver notebooks;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(users);
        argumentResolvers.add(notebooks);
    }

    public static void main(String[] args) {
        SpringApplication.run(NotebooksApplication.class, args);
    }

}
