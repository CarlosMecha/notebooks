package com.carlosmecha.notebooks.categories;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Common operations for categories.
 *
 * Created by carlos on 15/01/17.
 */
public class CategoryService {

    private final static Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final static String selectOne = "SELECT id, code, notebook_code, name, created_on FROM categories WHERE id = ?";
    private final static String selectOneByCode = "SELECT id, code, notebook_code, name, created_on FROM categories WHERE notebook_code = ? AND code = ?";
    private final static String selectAll = "SELECT id, code, notebook_code, name, created_on FROM categories WHERE notebook_code = ? ORDER BY code";
    private final static String insert = "INSERT INTO categories (code, notebook_code, name, created_on) VALUES (?, ?, ?, ?) RETURNING id";

    public CategoryService() {}

    /**
     * Retrieves a category.
     * @param id category.
     * @return The category if found.
     */
    public Optional<Category> get(Connection conn, int id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectOne)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Category.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Retrieves a category.
     * @param notebookCode Notebook code.
     * @param code Category code.
     * @return The category if found.
     */
    public Optional<Category> get(Connection conn, String notebookCode, String code) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectOneByCode)) {
            stmt.setString(1, notebookCode);
            stmt.setString(2, code);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Category.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Creates a category using just the name.
     * @param notebook Notebook.
     * @param name Category name.
     * @return New category.
     */
    public Category create(Connection conn, Notebook notebook, String name) throws SQLException {
        return create(conn, notebook, null, name);
    }

    /**
     * Creates the category.
     * @param notebook Notebook.
     * @param code Category code.
     * @param name Category name.
     * @return New category.
     */
    public Category create(Connection conn, Notebook notebook, String code, String name) throws SQLException {
        logger.debug("Creating category {}", name);
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            String c = (code == null || code.isEmpty()) ? StringUtils.nameToCode(name) : code;
            Date now = new Date();
            stmt.setString(1, c);
            stmt.setString(2, notebook.getCode());
            stmt.setString(3, name);
            stmt.setTimestamp(4, new Timestamp(now.getTime()));

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    Category category = new Category();
                    category.setId(result.getInt(1));
                    category.setCode(c);
                    category.setNotebookCode(notebook.getCode());
                    category.setName(name);
                    category.setCreatedOn(now);
                    return category;
                }
                // Internal error
                logger.error("The id was not returned when creating category");
                throw new RuntimeException("Unable to create category");
            }
        }
    }

    /**
     * Gets all categories for a notebook.
     * @param notebook Notebook.
     * @return List of categories.
     */
    public List<Category> getAll(Connection conn, Notebook notebook) throws SQLException {
        return getAll(conn, notebook.getCode());
    }

    /**
     * Gets all categories for a notebook.
     * @param notebookCode Notebook code.
     * @return List of categories.
     */
    public List<Category> getAll(Connection conn, String notebookCode) throws SQLException {
        logger.debug("Looking for all categories.");
        try (PreparedStatement stmt = conn.prepareStatement(selectAll)) {
            stmt.setString(1, notebookCode);
            List<Category> categories = new LinkedList<>();
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    categories.add(Category.fromRow(result));
                }
                return categories;
            }
        }
    }

}
