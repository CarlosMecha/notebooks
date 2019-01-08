package com.carlosmecha.notebooks.controllers;

import com.carlosmecha.notebooks.authentication.AuthenticationService;
import com.carlosmecha.notebooks.users.User;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Notebooks controller.
 * Thymeleaf and @RequestMapping don't work at class level. Fixing it.
 *
 * Created by Carlos on 12/29/16.
 */
@Controller
public class NotebooksController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(NotebooksController.class);

    @Autowired
    public NotebooksController(DataSource source) {
        super(source);
    }

    /**
     * Shows all notebooks and a form to create a new one.
     * @return Template name.
     */
    @GetMapping({"/", "/notebooks"})
    public ModelAndView getAll() throws SQLException {
        User user = AuthenticationService.getRequestUser();

        try (Connection conn = getConnection()) {
            ModelAndView model = new ModelAndView("notebooks");
            model.addObject("name", user.getName());
            model.addObject("notebook", new NotebookForm());
            model.addObject("notebooks", notebooks.getAll(conn));
            return model;
        }
    }

    /**
     * Create a new notebook.
     * @param notebook Notebook form.
     * @param result Binding result.
     * @param attributes Redirect attributes.
     * @return Redirection.
     */
    @PostMapping({"/", "/notebooks"})
    public ModelAndView create(@ModelAttribute NotebookForm notebook,
                         BindingResult result,
                         RedirectAttributes attributes) throws SQLException {
        User user = AuthenticationService.getRequestUser();
        logger.debug("User {} is trying to create notebook {}", user.getName(), notebook.getName());

        if(result.hasErrors() || notebook.getName() == null || notebook.getName().isEmpty()) {
            attributes.addFlashAttribute("error", "Error creating the notebook, check the information provided");
            return new ModelAndView(new RedirectView("/notebooks"));
        }

        try (Connection conn = getConnection()) {
            
            try {
                conn.setAutoCommit(false);
                notebooks.create(conn, notebook.getCode(), notebook.getName(), user);
                attributes.addFlashAttribute("message", "Notebook " + notebook.getName() + " created.");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
            return new ModelAndView(new RedirectView("/notebooks"));
        }
    }

    /**
     * Category form model.
     */
    public static class NotebookForm {

        private String code;
        private String name;

        public NotebookForm() {
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
