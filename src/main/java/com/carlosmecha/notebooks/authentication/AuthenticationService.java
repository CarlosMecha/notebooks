package com.carlosmecha.notebooks.authentication;

import com.carlosmecha.notebooks.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that blocks any unauthorized request. This meant to be deployed
 * behind a authentication proxy like ssso that populates the header 
 * <code>X-Auth-User</code>.
 *
 * @since 0.4
 */
@Service
public class AuthenticationService extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final static String DEFAULT_USER = "default";
    private final static String DEFAULT_EMAIL = "default@email.com";
    private final static String DEFAULT_NAME = "Default User";

    private final static ThreadLocal<User> requestUser = new ThreadLocal<>();

    private boolean disableSecurity;

    public AuthenticationService(@Value("${security.disable}") boolean disableSecurity) {
        this.disableSecurity = disableSecurity;
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

        // Static resources
        String uri = request.getRequestURI();
        if (uri.startsWith("/js/") || uri.startsWith("/css/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (disableSecurity) {
            requestUser.set(new User(DEFAULT_USER, DEFAULT_NAME, DEFAULT_EMAIL));
            filterChain.doFilter(request, response);
            return;
        }

        String loginName = request.getHeader("X-Auth-User");

        if (loginName == null || loginName.trim().length() == 0) {
            logger.debug("Request not authenticated");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String name = request.getHeader("X-Auth-User-Name");
        String email = request.getHeader("X-Auth-User-Email");
        
        requestUser.set(new User(loginName, name, email));
        filterChain.doFilter(request, response);
    }
    
    /**
     * Retrieves the user that made the request.
     * 
     * @return User.
     */
    public static User getRequestUser() {
        return requestUser.get();
    }

}
