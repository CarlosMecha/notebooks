package com.carlosmecha.notebooks.authentication;

import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * Resolves the user using Principal.
 *
 * Created by carlos on 15/01/17.
 */
@Component
public class UserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private UserService users;

    @Autowired
    public UserMethodArgumentResolver(UserService users) {
        this.users = users;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return User.class.equals(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Optional<User> user = users.get(nativeWebRequest.getUserPrincipal().getName());
        return (user.isPresent()) ? user.get() : null;
    }
}
