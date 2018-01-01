package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.budgets.Budget;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

/**
 * Budgets controller.
 *
 * Created by Carlos on 12/29/16.
 */
@Controller
@RequestMapping("/notebooks/{notebookCode}/budgets")
public class BudgetsController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(BudgetsController.class);

    private BudgetService service;
    private ExpenseService expenses;
    private CategoryService categories;
    private TagService tags;

    @Autowired
    public BudgetsController(DataSource source) {
        super(source);
        this.service = new BudgetService();
        this.expenses = new ExpenseService();
        this.categories = new CategoryService();
        this.tags = new TagService();
    }

    /**
     * Shows all budgets and a form to create a new one.
     * @return Template name.
     */
    @GetMapping
    public ModelAndView getAll(HttpServletRequest request, Principal principal) throws SQLException {
        try (Connection conn = getConnection()){
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);

            ModelAndView model = new ModelAndView("budgets");
            model.addObject("notebook", notebook);
            model.addObject("name", user.getName());
            model.addObject("budgets", service.getAll(conn, notebook));
            model.addObject("budget", new BudgetForm());
            return model;
        }
    }

    /**
     * Shows all expenses.
     * @return Template name.
     */
    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") int id, HttpServletRequest request, Principal principal) throws SQLException {
        try (Connection conn = getConnection()) {
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);

            Optional<Budget> budget = service.get(conn, id);
            if(!budget.isPresent() || !notebook.getCode().equals(budget.get().getNotebookCode())) {
                logger.debug("Requested an unknown budget {} or from a different notebook", id);
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
            }

            ModelAndView model = new ModelAndView("budget");
            model.addObject("notebook", notebook);
            model.addObject("name", user.getName());
            model.addObject("budget", budget.get());
            
            List<Expense> budgetExpenses = expenses.getForBudget(conn, id);
            float total = budget.get().getValue();
            Map<Integer, Category> expCategories = new HashMap<>();
            Map<Integer, Tag> expTags = new HashMap<>();
            Map<Long, List<String>> tagCodes = new HashMap<>();
            for (Expense expense : budgetExpenses) {
                total += expense.getValue();
            
                if (!expCategories.containsKey(expense.getCategoryId())) {
                    expCategories.put(expense.getCategoryId(), categories.get(conn, expense.getCategoryId()).get());
                }
                expense.setCategory(expCategories.get(expense.getCategoryId()));

                tagCodes.put(expense.getId(), new LinkedList<String>());
                for (int tagId : expenses.getExpenseTagIds(conn, expense.getId())) {
                    if (!expTags.containsKey(tagId)) {
                        expTags.put(tagId, tags.get(conn, tagId).get());
                    }
                    Tag tag = expTags.get(tagId);
                    tagCodes.get(expense.getId()).add(tag.getCode());
                }
            }

            model.addObject("expenses", budgetExpenses);
            model.addObject("tagCodes", tagCodes);
            model.addObject("total", total);
            return model;

        }
    }

    /**
     * Create a new budget.
     * @param budget Budget form.
     * @param result Binding result.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping
    public ModelAndView create(@ModelAttribute BudgetForm budget,
                         BindingResult result,
                         RedirectAttributes attributes,
                         HttpServletRequest request,
                         Principal principal) throws SQLException {
        logger.debug("User {} is trying to create budget {}", principal.getName());

        try (Connection conn = getConnection()) {
            User user = fromPrincipal(conn, principal);
            Notebook notebook = getNotebook(conn, request);

            if(result.hasErrors() || budget.getValue() == 0) {
                attributes.addFlashAttribute("error", "Error creating the budget, check the information provided");
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
            }

            try {
                conn.setAutoCommit(false);
                service.create(conn, notebook, budget.getName(), budget.getValue(),
                    StringUtils.unsafeToDate(budget.getStartOn(), "yyyy-MM-dd", logger),
                    StringUtils.unsafeToDate(budget.getEndOn(), "yyyy-MM-dd", logger),
                    budget.getDescription(), user);
                attributes.addFlashAttribute("message", "Budget created.");

                conn.commit();
                return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
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
     * Budget form model.
     */
    public static class BudgetForm {

        private String name;
        private float value;
        private String startOn;
        private String endOn;
        private String description;

        public BudgetForm() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getStartOn() {
            return startOn;
        }

        public void setStartOn(String startOn) {
            this.startOn = startOn;
        }

        public String getEndOn() {
            return endOn;
        }

        public void setEndOn(String endOn) {
            this.endOn = endOn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


}
