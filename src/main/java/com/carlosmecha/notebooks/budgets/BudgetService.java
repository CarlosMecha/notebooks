package com.carlosmecha.notebooks.budgets;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Common operations for budgets.
 *
 * Created by carlos on 15/01/17.
 */
@Service
public class BudgetService {

    private final static Logger logger = LoggerFactory.getLogger(BudgetService.class);

    private BudgetRepository repository;

    @Autowired
    public BudgetService(BudgetRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a budget.
     * @param id budget.
     * @return The budget if found.
     */
    public Optional<Budget> get(int id) {
        Budget budget = repository.findOne(id);
        return Optional.ofNullable(budget);
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
    @Transactional
    public Budget create(Notebook notebook, String name, float value, Date startOn, Date endOn, String description, User requester) {
        logger.debug("Creating budget for notebook {}", notebook.getCode());
        Budget budget = new Budget(notebook, name, value, startOn, endOn, description, requester);
        repository.save(budget);
        return budget;
    }

    /**
     * Gets all budgets for a notebook.
     * @param notebook Notebook.
     * @return List of budgets.
     */
    public List<Budget> getAll(Notebook notebook) {
        return getAll(notebook.getCode());
    }

    /**
     * Gets all budgets for a notebook.
     * @param notebookCode Notebook code.
     * @return List of budgets.
     */
    public List<Budget> getAll(String notebookCode) {
        logger.debug("Looking for all budgets.");
        return ListUtils.toList(repository.findAllByNotebookCode(notebookCode,
                new PageRequest(0, 1000, Sort.Direction.ASC, "startOn")));
    }

}
