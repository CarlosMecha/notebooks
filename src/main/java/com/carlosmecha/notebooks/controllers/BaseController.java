package com.carlosmecha.notebooks.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.notebooks.NotebookService;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    private DataSource source;
    protected NotebookService notebooks;

    protected BaseController(DataSource source) {
        this.source = source;
        this.notebooks = new NotebookService();
    }

    protected Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    protected Notebook getNotebook(Connection conn, HttpServletRequest request) throws SQLException {
        Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(map.containsKey("notebookCode")) {
            Optional<Notebook> notebook = notebooks.get(conn, map.get("notebookCode"));
            if (notebook.isPresent()) {
                return notebook.get();   
            }
        }

        throw new ResourceNotFound("Notebook not found");
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class ResourceNotFound extends RuntimeException {
        public ResourceNotFound(String msg) {
            super(msg);
        }
    }

}
