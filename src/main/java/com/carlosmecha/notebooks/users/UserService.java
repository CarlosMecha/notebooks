package com.carlosmecha.notebooks.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

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
     * Retrieves an user by login name.
     * @param loginName Login name.
     * @return User if found.
     */
    public Optional<User> get(String loginName) {
        logger.debug("Looking for user with name {}", loginName);
        return Optional.ofNullable(repository.findOne(loginName));
    }

}
