package com.carlosmecha.notebooks.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Common user operations.
 *
 * Created by carlos on 15/01/17.
 */
@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves an user by user details.
     * @param principal Security principal.
     * @return User if found, <code>null</code> otherwise.
     */
    public User getLoggedUser(Principal principal) {
        logger.debug("Looking for user with name {}", principal.getName());
        return repository.findOne(principal.getName());
    }

}
