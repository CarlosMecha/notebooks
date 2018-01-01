package com.carlosmecha.notebooks.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Date;

/**
 * User model.
 *
 * Created by Carlos on 12/25/16.
 */
public class User {

    private final static Logger logger = LoggerFactory.getLogger(User.class);
    private final static String selectOne = "SELECT login_name, name, password, email, created_on FROM users WHERE login_name = ?";

    private String loginName;
    private String name;
    private String email;
    private String password;
    private Date createdOn;

    private User() {}

    public static User fromRow(ResultSet row) throws SQLException {
        // login_name, name, password, email, created_on
        User user = new User();
        user.loginName = row.getString("login_name");
        user.name = row.getString("name");
        user.password = row.getString("password");
        user.email = row.getString("email");
        user.createdOn = new Date(row.getTimestamp("created_on").getTime());
        return user;
    }

    /**
     * Retrieves an user by login name.
     * @param loginName Login name.
     * @return User if found.
     */
    public static Optional<User> get(Connection conn, String loginName) throws SQLException {
        logger.debug("Looking for user with name {}", loginName);
        try (PreparedStatement stmt = conn.prepareStatement(selectOne)) {
            stmt.setString(1, loginName);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(User.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    public String getLoginName() {
        return loginName;
    }

    protected void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return String.format("User %s (%s)", loginName, name);
    }
}
