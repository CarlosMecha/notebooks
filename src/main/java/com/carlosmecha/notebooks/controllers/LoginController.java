package com.carlosmecha.notebooks.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * Login controller.
 *
 * Created by carlos on 15/01/17.
 */
@Controller
public class LoginController {

    public LoginController() {
    }

    /**
     * Login view.
     * @return The model.
     */
    public ModelAndView get() {
        return new ModelAndView("login");
    }
}
