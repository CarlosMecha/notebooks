package com.carlosmecha.notebooks.notebooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves the notebook where the operation is performed.
 *
 * Created by carlos on 15/01/17.
 */
@Component
public class NotebookMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private NotebookService service;

    @Autowired
    public NotebookMethodArgumentResolver(NotebookService service) {
        this.service = service;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return Notebook.class.equals(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String> map = (Map<String, String>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(map.containsKey("notebookCode")) {
            Optional<Notebook> notebook = service.get(map.get("notebookCode"));
            return (notebook.isPresent()) ? notebook.get() : null;
        }

        return null;
    }
}
