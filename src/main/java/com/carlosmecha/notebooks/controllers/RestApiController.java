package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.budgets.Budget;
import com.carlosmecha.notebooks.budgets.BudgetService;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * REST Api controller.
 *
 * Created by Carlos on 12/28/16.
 */
@RestController
@RequestMapping("/api/v1/notebooks")
public class RestApiController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(RestApiController.class);

    private CategoryService categories;
    private TagService tags;
    private BudgetService budgets;

    @Autowired
    public RestApiController(DataSource source) {
        super(source);
        this.categories = new CategoryService();
        this.tags = new TagService();
        this.budgets = new BudgetService();
    }

    /**
     * Retrieves all stored notebooks.
     * @return List of notebooks.
     */
    @RequestMapping("/")
    public List<Notebook> getAll() throws SQLException {
        try (Connection conn = getConnection()) {
            return notebooks.getAll(conn);
        }
    }

    /**
     * Retrieves all stored categories.
     * @return List of categories.
     */
    @RequestMapping("/{notebookCode}/categories")
    public List<Category> getCategories(@PathVariable("notebookCode") String notebookCode) throws SQLException {
        try (Connection conn = getConnection()) {
            return categories.getAll(conn, notebookCode);
        }
    }

    /**
     * Retrieves all stored tags.
     * @return List of tags.
     */
    @RequestMapping("/{notebookCode}/tags")
    public List<Tag> getTags(@PathVariable("notebookCode") String notebookCode) throws SQLException {
        try (Connection conn = getConnection()) {
            return tags.getAll(conn, notebookCode);
        }
    }

    /**
     * Retrieves all stored budgets.
     * @return List of budgets.
     */
    @RequestMapping("/{notebookCode}/budgets")
    public List<Budget> getBudgets(@PathVariable("notebookCode") String notebookCode) throws SQLException {
        try (Connection conn = getConnection()) {
            return budgets.getAll(conn, notebookCode);
        }
    }
}
