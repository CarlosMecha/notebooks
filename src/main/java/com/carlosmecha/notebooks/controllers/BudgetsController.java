package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.budgets.Budget;
import com.carlosmecha.notebooks.budgets.BudgetService;
import com.carlosmecha.notebooks.expenses.Expense;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

/**
 * Budgets controller.
 *
 * Created by Carlos on 12/29/16.
 */
@Controller
@RequestMapping("/notebooks/{notebookCode}/budgets")
public class BudgetsController {

    private final static Logger logger = LoggerFactory.getLogger(BudgetsController.class);

    private BudgetService service;

    @Autowired
    public BudgetsController(BudgetService service) {
        this.service = service;
    }

    /**
     * Shows all budgets and a form to create a new one.
     * @return Template name.
     */
    @GetMapping
    public ModelAndView getAll(Notebook notebook, User user) {
        ModelAndView model = new ModelAndView("budgets");
        model.addObject("notebook", notebook);
        model.addObject("name", user.getName());
        model.addObject("budgets", service.getAll(notebook));
        model.addObject("budget", new BudgetForm());
        return model;
    }

    /**
     * Shows all expenses.
     * @return Template name.
     */
    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") int id, Notebook notebook, User user) {

        Optional<Budget> budget = service.get(id);
        if(!budget.isPresent() || !notebook.getCode().equals(budget.get().getNotebook().getCode())) {
            logger.debug("Requested an unknown budget {} or from a different notebook", id);
            return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
        }

        ModelAndView model = new ModelAndView("budget");
        model.addObject("notebook", notebook);
        model.addObject("name", user.getName());
        model.addObject("budget", budget.get());

        float total = budget.get().getValue();
        for (Expense e : budget.get().getExpenses()) {
            total += e.getValue();
        }

        model.addObject("total", total);
        return model;
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
                         Notebook notebook,
                         User user) {
        logger.debug("User {} is trying to create budget {}", user.getLoginName());

        if(result.hasErrors() || budget.getValue() == 0) {
            attributes.addFlashAttribute("error", "Error creating the budget, check the information provided");
            return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
        }

        service.create(notebook, budget.getName(), budget.getValue(),
                StringUtils.unsafeToDate(budget.getStartOn(), "yyyy-MM-dd", logger),
                StringUtils.unsafeToDate(budget.getEndOn(), "yyyy-MM-dd", logger),
                budget.getDescription(), user);
        attributes.addFlashAttribute("message", "Budget created.");

        return new ModelAndView(new RedirectView("/notebooks/" + notebook.getCode() + "/budgets"));
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
