package com.carlosmecha.notebooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entrypoint for the application.
 */
@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@EntityScan(basePackageClasses = NotebooksApplication.class)
public class NotebooksApplication extends WebMvcConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(NotebooksApplication.class);  

    @Bean
    public DataSource dataSource(DatabaseConfiguration config) {
        
        PoolProperties p = new PoolProperties();
        p.setUrl(config.getUrl());
        p.setDriverClassName("org.postgresql.Driver");
        p.setUsername(config.getUsername());
        p.setPassword(config.getPassword());
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setMaxActive(5);
        p.setMaxIdle(5);
        p.setInitialSize(1);
        p.setMinIdle(1);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);
        return datasource;
    }

    public static void main(String[] args) {
        SpringApplication.run(NotebooksApplication.class, args);
    }

}
