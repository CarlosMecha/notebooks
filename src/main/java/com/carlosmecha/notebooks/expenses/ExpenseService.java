package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.DataNotFoundException;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Expense service.
 *
 * Created by Carlos on 12/28/16.
 */
@Service
public class ExpenseService {

    private final static Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private ExpenseRepository repository;
    private CategoryService categories;
    private TagService tags;

    @Autowired
    public ExpenseService(CategoryService categories, ExpenseRepository repository, TagService tags) {
        this.categories = categories;
        this.repository = repository;
        this.tags = tags;
    }

    /**
     * Gets a limited list of expenses ordered by date and creation date.
     * @param notebookCode Notebook code.
     * @param size Size of the list.
     * @return Expenses.
     */
    public List<Expense> getLatest(String notebookCode, int size) {
        return ListUtils.toList(repository.findAllByNotebookCode(notebookCode,
                new PageRequest(0, size, Sort.Direction.DESC, "date", "createdOn")));
    }

    /**
     * Creates a new expense.
     * @param notebook Notebook.
     * @param value Value.
     * @param categoryId Category id.
     * @param date Date.
     * @param tagCodes A list of tags.
     * @param note Notes (optional).
     * @param requester The user who request the creation.
     * @return The new expense.
     * @throws DataNotFoundException When some data is missing.
     */
    @Transactional
    public Expense create(Notebook notebook, float value, int categoryId, Date date, Set<String> tagCodes, String note, User requester) throws DataNotFoundException {
        logger.debug("Creating expense of {}", value);

        Optional<Category> category = categories.get(categoryId);
        if(!category.isPresent()) {
            throw new DataNotFoundException("Category " + categoryId + " not found");
        }

        Set<Tag> tagSet = tagCodes.stream().map(t -> {
            Optional<Tag> tag = tags.get(notebook.getCode(), t);
            return (tag.isPresent()) ? tag.get() : new Tag(notebook, t);
        }).collect(Collectors.toSet());

        Expense expense = new Expense(notebook, category.get(), value, date, tagSet, note, requester);
        repository.save(expense);
        return expense;
    }

    /**
     * Creates a basic report with all expenses between two dates.
     * @param notebookCode Notebook code.
     * @param title Report title.
     * @param startDate Start date.
     * @param endDate End date.
     * @return Report.
     */
    public Report createReportByDateRange(String notebookCode, String title, Date startDate, Date endDate) {
        return new Report(title, startDate, endDate, repository.findAllByNotebookCodeAndDateRange(notebookCode, startDate, endDate));
    }


}
