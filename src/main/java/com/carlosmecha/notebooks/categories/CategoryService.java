package com.carlosmecha.notebooks.categories;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Common operations for categories.
 *
 * Created by carlos on 15/01/17.
 */
@Service
public class CategoryService {

    private final static Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private CategoryRepository repository;

    @Autowired
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a category.
     * @param id category.
     * @return The category if found.
     */
    public Optional<Category> get(int id) {
        Category category = repository.findOne(id);
        return Optional.ofNullable(category);
    }

    /**
     * Retrieves a category.
     * @param notebookCode Notebook code.
     * @param code Category code.
     * @return The category if found.
     */
    public Optional<Category> get(String notebookCode, String code) {
        List<Category> category = ListUtils.toList(repository.findByNotebookCodeAndCode(notebookCode, code));
        return (category.isEmpty()) ? Optional.empty() : Optional.of(category.get(0));
    }

    /**
     * Creates a category using just the name.
     * @param notebook Notebook.
     * @param name Category name.
     * @return New category.
     */
    public Category create(Notebook notebook, String name) {
        return create(notebook,null, name);
    }

    /**
     * Creates the category.
     * @param notebook Notebook.
     * @param code Category code.
     * @param name Category name.
     * @return New category.
     */
    @Transactional
    public Category create(Notebook notebook, String code, String name) {
        logger.debug("Creating category {}", name);
        Category category = (code == null || code.isEmpty()) ? new Category(notebook, name) : new Category(notebook, code, name);
        repository.save(category);
        return category;
    }

    /**
     * Gets all categories for a notebook.
     * @param notebook Notebook.
     * @return List of categories.
     */
    public List<Category> getAll(Notebook notebook) {
        return getAll(notebook.getCode());
    }

    /**
     * Gets all categories for a notebook.
     * @param notebookCode Notebook code.
     * @return List of categories.
     */
    public List<Category> getAll(String notebookCode) {
        logger.debug("Looking for all categories.");
        return ListUtils.toList(repository.findAllByNotebookCode(notebookCode,
                new PageRequest(0, 1000, Sort.Direction.ASC, "code")));
    }

}
