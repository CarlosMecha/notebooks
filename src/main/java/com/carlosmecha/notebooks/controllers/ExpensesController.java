package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.authentication.AuthenticationService;
import com.carlosmecha.notebooks.budgets.BudgetService;
import com.carlosmecha.notebooks.categories.Category;
import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.expenses.Expense;
import com.carlosmecha.notebooks.expenses.ExpenseService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.DataNotFoundException;
import com.carlosmecha.notebooks.utils.StringUtils;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Expenses controller.
 *
 * Created by Carlos on 12/28/16.
 */
@Controller
@RequestMapping("/notebooks/{notebookCode}/expenses")
public class ExpensesController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ExpensesController.class);

    private ExpenseService service;
    private BudgetService budgets;
    private CategoryService categories;
    private TagService tags;

    @Autowired
    public ExpensesController(DataSource source) {
        super(source);
        this.service = new ExpenseService();
        this.budgets = new BudgetService();
        this.categories = new CategoryService();
        this.tags = new TagService();
    }

    /**
     * Shows this month report and a form to create a new expense.
     * @return Template name.
     */
    @GetMapping
    public ModelAndView index(HttpServletRequest request) throws SQLException {

        Calendar date = Calendar.getInstance();
        User user = AuthenticationService.getRequestUser();
        
        try (Connection conn = getConnection()) {
            Notebook notebook = getNotebook(conn, request);

            ModelAndView model = new ModelAndView("expenses");
            model.addObject("name", user.getName());
            model.addObject("notebook", notebook);
            model.addObject("report", service.createMontlyReport(conn, notebook, date.get(Calendar.MONTH) + 1, date.get(Calendar.YEAR)));
            model.addObject("expense", new ExpenseForm());
            model.addObject("budgets", budgets.getAll(conn, notebook.getCode()));
            return model;
        }
    }

    /**
     * Shows a list of service.
     * @return Template name.
     */
    @GetMapping("/latest")
    public ModelAndView getLatest(HttpServletRequest request) throws SQLException {
        User user = AuthenticationService.getRequestUser();
        try (Connection conn = getConnection()) {
            Notebook notebook = getNotebook(conn, request);

            ModelAndView model = new ModelAndView("latest");
            model.addObject("name", user.getName());
            model.addObject("notebook", notebook);
            
            List<Expense> expenses = service.getLatest(conn, notebook, 100);
            Map<Integer, Category> expCategories = new HashMap<>();
            Map<Integer, Tag> expTags = new HashMap<>();
            Map<Long, List<String>> tagCodes = new HashMap<>();
            for (Expense expense : expenses) {
                if (!expCategories.containsKey(expense.getCategoryId())) {
                    expCategories.put(expense.getCategoryId(), categories.get(conn, expense.getCategoryId()).get());
                }
                expense.setCategory(expCategories.get(expense.getCategoryId()));

                tagCodes.put(expense.getId(), new LinkedList<String>());
                for (int tagId : service.getExpenseTagIds(conn, expense.getId())) {
                    if (!expTags.containsKey(tagId)) {
                        expTags.put(tagId, tags.get(conn, tagId).get());
                    }
                    Tag tag = expTags.get(tagId);
                    tagCodes.get(expense.getId()).add(tag.getCode());
                }
            }

            model.addObject("expenses", expenses);
            model.addObject("tagCodes", tagCodes);

            return model;
        }
    }

    /**
     * Creates the new expense a redirects to the main page.
     * @param expense Expense.
     * @param result Result of binding.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping
    public ModelAndView create(@ModelAttribute ExpenseForm expense,
                         BindingResult result,
                         RedirectAttributes attributes,
                         HttpServletRequest request) throws SQLException {
        User user = AuthenticationService.getRequestUser();
        logger.debug("User {} is trying to create expense {}", user.getName(), expense.getValue());

        try (Connection conn = getConnection()) {
            Notebook notebook = getNotebook(conn, request);

            if(result.hasErrors() || expense.categoryId < 0) {
                attributes.addFlashAttribute("error", "Missing information!");
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/expenses"));
            }

            try {
                conn.setAutoCommit(false);
                service.create(conn, notebook, expense.getValue(), expense.categoryId,
                    StringUtils.unsafeToDate(expense.getDate(), "yyyy-MM-dd", logger),
                    StringUtils.split(expense.getTagCodes(), ","),
                    StringUtils.split(expense.getBudgetIds(), ",").stream().map(id -> Integer.parseInt(id)).collect(Collectors.toSet()),
                    expense.getNotes(), user);
                
                attributes.addFlashAttribute("message", "Expense created");
                conn.commit();

                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/expenses"));
            } catch (DataNotFoundException e) {
                conn.rollback();
                attributes.addFlashAttribute("error", "Data not found!");
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/expenses"));
            } catch (NumberFormatException e) {
                conn.rollback();
                attributes.addFlashAttribute("error", "Invalid budget id!");
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/expenses"));
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Expense form model.
     */
    public static class ExpenseForm {

        private float value;
        private int categoryId = -1;
        @NotNull
        private String date;
        private String tagCodes;
        private String budgetIds;
        private String notes;

        public ExpenseForm() {
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTagCodes() {
            return tagCodes;
        }

        public void setTagCodes(String tagCodes) {
            this.tagCodes = tagCodes;
        }

        public String getBudgetIds() {
            return budgetIds;
        }

        public void setBudgetIds(String budgetIds) {
            this.budgetIds = budgetIds;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "ExpenseForm{" +
                    "value=" + value +
                    ", categoryId='" + categoryId + '\'' +
                    ", date=" + date +
                    ", tagCodes=" + tagCodes +
                    ", notes='" + notes + '\'' +
                    '}';
        }
    }

}
