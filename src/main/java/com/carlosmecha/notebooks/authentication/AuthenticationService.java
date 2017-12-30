package com.carlosmecha.notebooks.authentication;

import com.carlosmecha.notebooks.users.User;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

/**
 * Service to retrieve user credentials from the user model.
 *
 * Created by Carlos on 12/20/16.
 */
@Service
public class AuthenticationService implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private DataSource dataSource;

    @Autowired
    public AuthenticationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        logger.debug("Looking for credentials for user with name {}", loginName);
        try (Connection conn = dataSource.getConnection()){
            Optional<User> user = User.get(conn, loginName);
            if(user.isPresent()) {
                return new BasicUserDetails(user.get());
            }
            throw new UsernameNotFoundException("User with name " + loginName + " not found.");
        } catch(SQLException e) {
            logger.error("Error retriving user for authentication", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Basic implementation of credentials
     */
    class BasicUserDetails implements UserDetails {

        private String name;
        private String encPassword;

        public BasicUserDetails(User user) {
            this.name = user.getLoginName();
            this.encPassword = user.getPassword();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return AuthorityUtils.commaSeparatedStringToAuthorityList("USER");
        }

        @Override
        public String getPassword() {
            return encPassword;
        }

        @Override
        public String getUsername() {
            return name;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }


}
