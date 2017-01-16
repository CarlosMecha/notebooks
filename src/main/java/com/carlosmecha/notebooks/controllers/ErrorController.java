package com.carlosmecha.notebooks.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Error controller.
 *
 * Created by carlos on 15/01/17.
 */
@Controller
public class ErrorController {

    public ErrorController() {
    }

    /**
     * Default error view.
     * @return The model.
     */
    @GetMapping("/error")
    public ModelAndView get() {
        return new ModelAndView("error");
    }
}
