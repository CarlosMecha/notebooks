package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.categories.CategoryService;
import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.users.User;
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

/**
 * Categories controller.
 * Thymeleaf and @RequestMapping don't work at class level. Fixing it.
 *
 * Created by Carlos on 12/29/16.
 */
@Controller
@RequestMapping("/{notebookCode}/categories")
public class CategoriesController {

    private final static Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    private CategoryService service;

    @Autowired
    public CategoriesController(CategoryService service) {
        this.service = service;
    }

    /**
     * Shows all categories and a form to create a new one.
     * @return Template name.
     */
    @GetMapping
    public ModelAndView getAll(Notebook notebook, User user) {
        ModelAndView model = new ModelAndView("categories");
        model.addObject("notebook", notebook);
        model.addObject("name", user.getName());
        model.addObject("categories", service.getAll(notebook));
        model.addObject("category", new CategoryForm());
        return model;
    }

    /**
     * Create a new category.
     * @param category Category form.
     * @param result Binding result.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping
    public ModelAndView create(@ModelAttribute CategoryForm category,
                         BindingResult result,
                         RedirectAttributes attributes,
                         Notebook notebook,
                         User user) {
        logger.debug("User {} is trying to create category {}", user.getLoginName(), category.getName());

        if(result.hasErrors() || category.getName() == null || category.getName().isEmpty()) {
            attributes.addFlashAttribute("error", "Error creating the category, check the information provided");
            return new ModelAndView(new RedirectView(notebook.getCode() + "/categories"));
        }

        service.create(notebook, category.getCode(), category.getName());
        attributes.addFlashAttribute("message", "Category " + category.getName() + " created.");

        return new ModelAndView(new RedirectView(notebook.getCode() + "/categories"));
    }

    /**
     * Category form model.
     */
    public static class CategoryForm {

        private String code;
        private String name;

        public CategoryForm() {
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
