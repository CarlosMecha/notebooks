package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.notebooks.NotebookService;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.budgets.Budget;
import com.carlosmecha.notebooks.budgets.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Api controller.
 *
 * Created by Carlos on 12/28/16.
 */
@RestController
@RequestMapping("/api/v1/notebooks")
public class RestApiController {

    private final static Logger logger = LoggerFactory.getLogger(RestApiController.class);

    private NotebookService notebooks;
    private CategoryService categories;
    private TagService tags;
    private BudgetService budgets;

    @Autowired
    public RestApiController(NotebookService notebooks, CategoryService categories, TagService tags, BudgetService budgets) {
        this.notebooks = notebooks;
        this.categories = categories;
        this.tags = tags;
        this.budgets = budgets;
    }

    /**
     * Retrieves all stored notebooks.
     * @return List of notebooks.
     */
    @RequestMapping("/")
    public List<Notebook> getAll() {
        return notebooks.getAll();
    }

    /**
     * Retrieves all stored categories.
     * @return List of categories.
     */
    @RequestMapping("/{notebookCode}/categories")
    public List<Category> getCategories(@PathVariable("notebookCode") String notebookCode) {
        return categories.getAll(notebookCode);
    }

    /**
     * Retrieves all stored tags.
     * @return List of tags.
     */
    @RequestMapping("/{notebookCode}/tags")
    public List<Tag> getTags(@PathVariable("notebookCode") String notebookCode) {
        return tags.getAll(notebookCode);
    }

    /**
     * Retrieves all stored budgets.
     * @return List of budgets.
     */
    @RequestMapping("/{notebookCode}/budgets")
    public List<Budget> getBudgets(@PathVariable("notebookCode") String notebookCode) {
        return budgets.getAll(notebookCode);
    }
}
