package com.carlosmecha.notebooks.users;

/**
 * User model.
 *
 * @since 0.4
 */
public class User {

    private String loginName;
    private String name;
    private String email;

    public User(String loginName, String name, String email) {
        this.loginName = loginName;
        this.name = name;
        this.email = email;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("User %s (%s)", loginName, name);
    }
}
