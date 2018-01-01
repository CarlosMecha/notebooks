package com.carlosmecha.notebooks.budgets;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Common operations for budgets.
 *
 * Created by carlos on 15/01/17.
 */
public class BudgetService {

    private final static String selectOneStmt = "SELECT id, notebook_code, name, value, start_on, end_on, created_on, created_by, description FROM budgets WHERE id = ?";
    private final static String selectAllStmt = "SELECT id, notebook_code, name, value, start_on, end_on, created_on, created_by, description FROM budgets WHERE notebook_code = ? ORDER BY start_on ASC";
    private final static String insertStmt = "INSERT INTO budgets (notebook_code, name, value, start_on, end_on, created_on, created_by, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    private final static Logger logger = LoggerFactory.getLogger(BudgetService.class);

    public BudgetService() {
    }

    /**
     * Retrieves a budget.
     * @param id budget.
     * @return The budget if found.
     */
    public Optional<Budget> get(Connection conn, int id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectOneStmt)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Budget.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Creates the budget.
     * @param notebook Notebook.
     * @param name Name.
     * @param value Budget value.
     * @param startOn When the budget starts.
     * @param endOn When the budget ends.
     * @param description Description.
     * @param requester The user.
     * @return New budget.
     */
    public Budget create(Connection conn, Notebook notebook, String name, float value, Date startOn, Date endOn, String description, User requester) throws SQLException {
        // notebook_code, name, value, start_on, end_on, created_on, created_by, description
        logger.debug("Creating budget for notebook {}", notebook.getCode());
        try (PreparedStatement stmt = conn.prepareStatement(insertStmt)) {
            Date now = new Date();

            stmt.setString(1, notebook.getCode());
            stmt.setString(2, name);
            stmt.setFloat(3, value);
            stmt.setTimestamp(4, new Timestamp(startOn.getTime()));
            stmt.setTimestamp(5, new Timestamp(endOn.getTime()));
            stmt.setTimestamp(6, new Timestamp(now.getTime()));
            stmt.setString(7, requester.getLoginName());
            stmt.setString(8, description);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    Budget budget = new Budget();
                    budget.setId(result.getInt(1));
                    budget.setNotebookCode(notebook.getCode());
                    budget.setName(name);
                    budget.setValue(value);
                    budget.setStartOn(startOn);
                    budget.setEndOn(endOn);
                    budget.setCreatedOn(now);
                    budget.setCreatedBy(requester.getLoginName());
                    budget.setDescription(description);
                    return budget;
                }
                // Internal error
                logger.error("The id was not returned when creating budget");
                throw new RuntimeException("Unable to create budget");
            }
        }

    }

    /**
     * Gets all budgets for a notebook.
     * @param notebook Notebook.
     * @return List of budgets.
     */
    public List<Budget> getAll(Connection conn, Notebook notebook) throws SQLException {
        return getAll(conn, notebook.getCode());
    }

    /**
     * Gets all budgets for a notebook.
     * @param notebookCode Notebook code.
     * @return List of budgets.
     */
    public List<Budget> getAll(Connection conn, String notebookCode) throws SQLException {
        logger.debug("Looking for all budgets.");
        try (PreparedStatement stmt = conn.prepareStatement(selectAllStmt)) {
            stmt.setString(1, notebookCode);
            try (ResultSet result = stmt.executeQuery()) {
                List<Budget> budgets = new LinkedList<>();
                while (result.next()) {
                    budgets.add(Budget.fromRow(result));
                }
                return budgets;
            }
        }
    }

}
