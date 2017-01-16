package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.expenses.ExpenseService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.DataNotFoundException;
import com.carlosmecha.notebooks.utils.StringUtils;
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

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * Expenses controller.
 *
 * Created by Carlos on 12/28/16.
 */
@Controller
@RequestMapping("/{notebookCode}/service")
public class ExpensesController {

    private final static Logger logger = LoggerFactory.getLogger(ExpensesController.class);

    private ExpenseService service;

    @Autowired
    public ExpensesController(ExpenseService service) {
        this.service = service;
    }

    /**
     * Shows this month report and a form to create a new expense.
     * @return Template name.
     */
    @GetMapping("/")
    public ModelAndView index(Notebook notebook, User user) {

        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = date.getTime();
        date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = date.getTime();

        ModelAndView model = new ModelAndView("service");

        model.addObject("name", user.getName());
        model.addObject("report", service.createReportByDateRange(notebook, "Monthly", startDate, endDate));
        model.addObject("expense", new ExpenseForm());
        return model;
    }

    /**
     * Shows a list of service.
     * @return Template name.
     */
    @GetMapping("/latest")
    public ModelAndView getLatest(Notebook notebook, User user) {
        ModelAndView model = new ModelAndView("latest");
        model.addObject("name", user.getName());
        model.addObject("service", service.getLatest(notebook, 100));
        return model;
    }

    /**
     * Creates the new expense a redirects to the main page.
     * @param expense Expense.
     * @param result Result of binding.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping("/")
    public ModelAndView create(@ModelAttribute ExpenseForm expense,
                         BindingResult result,
                         RedirectAttributes attributes,
                         Notebook notebook, User user) {
        logger.debug("User {} is trying to create expense {}", user.getLoginName(), expense.getValue());

        if(result.hasErrors() || expense.categoryId < 0) {
            attributes.addAttribute("error", "Missing information!");
            return new ModelAndView(new RedirectView(notebook.getCode() + "/service"));
        }

        try {
            service.create(notebook, expense.getValue(), expense.categoryId,
                    StringUtils.unsafeToDate(expense.getDate(), "yyyy-MM-dd", logger),
                    StringUtils.split(expense.getTagCodes(), ","),
                    expense.getNotes(), user);
        } catch (DataNotFoundException e) {
            attributes.addAttribute("error", "Data not found!");
            return new ModelAndView(new RedirectView(notebook.getCode() + "/service"));
        }

        attributes.addAttribute("message", "Expense created");
        return new ModelAndView(new RedirectView(notebook.getCode() + "/service"));
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
