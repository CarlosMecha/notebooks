package com.carlosmecha.notebooks.users;

import org.springframework.data.repository.Repository;

/**
 * User repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface UserRepository extends Repository<User, String> {

    /**
     * Finds for the user with the provided login name.
     * @param loginName Loging name.
     * @return The user if found, or <code>null</code> otherwise.
     */
    User findOne(String loginName);

}
