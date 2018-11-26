package com.carlosmecha.notebooks.expenses;

import com.carlosmecha.notebooks.budgets.Budget;
import com.carlosmecha.notebooks.budgets.BudgetService;
import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.DataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Expense service.
 *
 * Created by Carlos on 12/28/16.
 */
public class ExpenseService {

    private final static Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    private final static String selectLatest = "SELECT id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes FROM expenses WHERE notebook_code = ? ORDER BY date DESC, created_on DESC LIMIT ?";
    private final static String selectByDateRange = "SELECT id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes FROM expenses WHERE notebook_code = ? AND date >= ? AND date <= ? ORDER BY date";
    private final static String selectByMonth = "SELECT id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes FROM expenses WHERE notebook_code = ? AND EXTRACT('month' FROM date) = ? AND EXTRACT('year' FROM date) = ? ORDER BY date";
    private final static String selectExpenseTags = "SELECT tag_id FROM expense_tags WHERE expense_id = ?";
    private final static String selectByBudget = "SELECT id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes FROM budget_expenses AS b INNER JOIN expenses AS e ON (e.id = b.expense_id) WHERE budget_id = ?";
    private final static String insert = "INSERT INTO expenses (notebook_code, category_id, value, date, created_on, updated_on, created_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private final static String insertExpenseTag = "INSERT INTO expense_tags (expense_id, tag_id) VALUES (?, ?)";
    private final static String insertExpenseBudget = "INSERT INTO budget_expenses (budget_id, expense_id) VALUES (?, ?)";

    private CategoryService categories;
    private TagService tags;
    private BudgetService budgets;

    public ExpenseService() {
        this.categories = new CategoryService();
        this.tags = new TagService();
        this.budgets = new BudgetService();
    }

    /**
     * Gets a limited list of expenses ordered by date and creation date.
     * @param notebook Notebook.
     * @param size Size of the list.
     * @return Expenses.
     */
    public List<Expense> getLatest(Connection conn, Notebook notebook, int size) throws SQLException {
        return getLatest(conn, notebook.getCode(), size);
    }

    /**
     * Gets a limited list of expenses ordered by date and creation date.
     * @param notebookCode Notebook code.
     * @param size Size of the list.
     * @return Expenses.
     */
    public List<Expense> getLatest(Connection conn, String notebookCode, int size) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectLatest)) {
            stmt.setString(1, notebookCode);
            stmt.setInt(2, size);

            try (ResultSet result = stmt.executeQuery()) {
                List<Expense> expenses = new LinkedList<>();
                while (result.next()) {
                    expenses.add(Expense.fromRow(result));
                }
                return expenses;
            }
        }
    }

    public List<Integer> getExpenseTagIds(Connection conn, long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectExpenseTags)) {
            stmt.setLong(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                List<Integer> tagIds = new LinkedList<>();
                while (result.next()) {
                    tagIds.add(result.getInt(1));
                }
                return tagIds;
            }
        }
    }

    /**
     * Gets the expenses in a budget.
     * @param budgetId Budget it.
     * @return Expenses.
     */
    public List<Expense> getForBudget(Connection conn, int budgetId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectByBudget)) {
            stmt.setInt(1, budgetId);

            try (ResultSet result = stmt.executeQuery()) {
                List<Expense> expenses = new LinkedList<>();
                while (result.next()) {
                    expenses.add(Expense.fromRow(result));
                }
                return expenses;
            }
        }
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
    public Expense create(Connection conn, Notebook notebook, float value, int categoryId, Date date, Set<String> tagCodes, Set<Integer> budgetIds, String note, User requester) throws SQLException, DataNotFoundException {
        logger.debug("Creating expense of {}", value);

        Date now = new Date();

        Expense expense = new Expense();

        // id, notebook_code, category_id, value, date, created_on, updated_on, created_by, notes
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, notebook.getCode());
            stmt.setInt(2, categoryId);
            stmt.setFloat(3, value);
            stmt.setTimestamp(4, new Timestamp(date.getTime()));
            stmt.setTimestamp(5, new Timestamp(now.getTime()));
            stmt.setTimestamp(6, new Timestamp(now.getTime()));
            stmt.setString(7, requester.getLoginName());
            stmt.setString(8, note);

            try (ResultSet result = stmt.executeQuery()) {
                if (!result.next()) {
                    // Internal error
                    logger.error("The id was not returned when creating expense");
                    throw new RuntimeException("Unable to create expense");
                }
                expense.setId(result.getLong(1));
                expense.setNotebookCode(notebook.getCode());
                expense.setCategoryId(categoryId);
                expense.setValue(value);
                expense.setDate(date);
                expense.setCreatedOn(now);
                expense.setUpdatedOn(now);
                expense.setCreatedBy(requester.getLoginName());
                expense.setNotes(note);
            }
        }

        // Category
        Optional<Category> category = categories.get(conn, categoryId);
        if(!category.isPresent()) {
            throw new DataNotFoundException("Category " + categoryId + " not found");
        }

        expense.setCategory(category.get());

        // Tags
        Set<Tag> expenseTags = new HashSet<>(); 

        for (String tagCode : tagCodes) {
            Optional<Tag> tag = tags.get(conn, notebook.getCode(), tagCode);
            Tag t = (tag.isPresent()) ? tag.get() : tags.create(conn, notebook, tagCode);
            
            try (PreparedStatement stmt = conn.prepareStatement(insertExpenseTag)) {
                stmt.setLong(1, expense.getId());
                stmt.setInt(2, t.getId());
                stmt.execute();
            }

            expenseTags.add(t);
        }

        expense.setTags(expenseTags);

        // Budgets
        Set<Budget> expenseBudgets = new HashSet<>();

        for (int budgetId : budgetIds) {
            Optional<Budget> budget = budgets.get(conn, budgetId);
            if (!budget.isPresent()) {
                throw new DataNotFoundException("Budget " + budgetId + " not found");
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertExpenseBudget)) {
                stmt.setInt(1, budgetId);
                stmt.setLong(2, expense.getId());
                stmt.execute();
            }

            expenseBudgets.add(budget.get());
        }

        expense.setBudgets(expenseBudgets);
        return expense;
    }

    /**
     * Creates a basic report with all expenses between two dates.
     * @param notebook Notebook.
     * @param title Report title.
     * @param startDate Start date.
     * @param endDate End date.
     * @return Report.
     */
    public Report createReportByDateRange(Connection conn, Notebook notebook, String title, Date startDate, Date endDate) throws SQLException {
        
        List<Expense> expenses = new LinkedList<>();
        try (PreparedStatement stmt = conn.prepareStatement(selectByDateRange)) {
            stmt.setString(1, notebook.getCode());
            stmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(3, new Timestamp(endDate.getTime()));

            try (ResultSet result = stmt.executeQuery()) {
                return createReportFromResults(conn, title, startDate, endDate, result);
            }
        }

    }

    /**
     * Creates a basic report with all expenses for a month and year.
     * @param conn Connection to the database.
     * @param notebook Notebook of the report.
     * @param month Month (1 - 12).
     * @param year Year.
     * @return Report.
     */
    public Report createMontlyReport(Connection conn, Notebook notebook, int month, int year) throws SQLException {
        
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month should be between 1 and 12");
        }

        if (year < 2010 || year > 2050) {
            throw new IllegalArgumentException("Year out of valid range");
        }

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.YEAR, year);
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        Date from = date.getTime();
        date = Calendar.getInstance();
        date.set(Calendar.MONTH, month);
        date.set(Calendar.YEAR, year);
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.HOUR, 23);
        date.set(Calendar.MINUTE, 59);
        Date to = date.getTime();
        
        try (PreparedStatement stmt = conn.prepareStatement(selectByMonth)) {
            stmt.setString(1, notebook.getCode());
            stmt.setInt(2, month);
            stmt.setInt(3, year);

            try (ResultSet result = stmt.executeQuery()) {
                return createReportFromResults(conn, String.format("Monthly Report: %d/%d", month, year), from, to, result);
            }
        }

        
    }

    private Report createReportFromResults(Connection conn, String title, Date from, Date to, ResultSet result) throws SQLException {

        List<Expense> expenses = new LinkedList<>();
        while(result.next()) {
            expenses.add(Expense.fromRow(result));
        }

        // Categories
        float total = 0;
        Map<Category, Float> reportCategories = new HashMap<>();
        Map<Integer, Tag> reportTags = new HashMap<>();
        for(Expense expense : expenses) {
            
            float value = expense.getValue();
            Optional<Category> c = categories.get(conn, expense.getCategoryId());
            if (!c.isPresent()) {
                // Internal error
                logger.error("The category was not returned when creating report");
                throw new RuntimeException("Unable to create report");
            }
            Category category = c.get();

            if (reportCategories.containsKey(category)) {
                reportCategories.put(category, reportCategories.get(category) + expense.getValue());
            } else {
                reportCategories.put(category, expense.getValue());
            }

            total += expense.getValue();

            Set<Integer> tagIds = new HashSet<>();
            try (PreparedStatement stmt = conn.prepareStatement(selectExpenseTags)) {
                stmt.setLong(1, expense.getId());
                try (ResultSet resultTags = stmt.executeQuery()) {
                    while(resultTags.next()) {
                        int tagId = resultTags.getInt(1);
                        if (!reportTags.containsKey(tagId)) {
                            tagIds.add(tagId);
                        } 
                    }
                }
            }

            for (int tagId : tagIds) {
                Optional<Tag> tag = tags.get(conn, tagId);
                if (!tag.isPresent()) {
                    // Internal error
                    logger.error("The tag was not returned when creating report");
                    throw new RuntimeException("Unable to create report");
                }
                reportTags.put(tagId, tag.get());
            }

        }

        List<Tag> tags = new ArrayList<>(reportTags.size()); 
        for (Tag tag : reportTags.values()) {
            tags.add(tag);
        }
        tags.sort(Comparator.comparing(Tag::getCode));



        Report report = new Report();
        report.setTitle(title);
        report.setStartDate(from);
        report.setEndDate(to);
        report.setCategories(reportCategories);
        report.setTags(tags);
        report.setTotal(total);
        return report;

    }

}
