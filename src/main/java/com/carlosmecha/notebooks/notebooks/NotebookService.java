package com.carlosmecha.notebooks.notebooks;

import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Notebook common operations.
 *
 * Created by carlos on 15/01/17.
 */
public class NotebookService {

    private final static Logger logger = LoggerFactory.getLogger(NotebookService.class);
    private final static String selectOne = "SELECT code, name, created_on, created_by FROM notebooks WHERE code = ?";
    private final static String selectAll = "SELECT code, name, created_on, created_by FROM notebooks ORDER BY code";
    private final static String insert = "INSERT INTO notebooks (code, name, created_on, created_by) VALUES (?, ?, ?, ?)";

    public NotebookService() {
    }

    /**
     * Gets a notebook.
     * @param code Notebook code.
     * @return The notebook if found.
     */
    public Optional<Notebook> get(Connection conn, String code) throws SQLException {
        logger.debug("Looking for notebook {}", code);
        try (PreparedStatement stmt = conn.prepareStatement(selectOne)) {
            stmt.setString(1, code);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Notebook.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Gets all notebooks.
     * @return A list of notebooks.
     */
    public List<Notebook> getAll(Connection conn) throws SQLException {
        logger.debug("Looking for all notebooks");
        try (PreparedStatement stmt = conn.prepareStatement(selectAll);
            ResultSet result = stmt.executeQuery()) {
            List<Notebook> notebooks = new LinkedList<>();
            while (result.next()) {
                notebooks.add(Notebook.fromRow(result));
            }
            return notebooks;
        }
    }

    /**
     * Creates the notebook.
     * @param code Category code.
     * @param name Category name.
     * @param requester The user.
     * @return New notebook.
     */
    public Notebook create(Connection conn, String code, String name, User requester) throws SQLException {
        logger.debug("Creating notebook {} by {}", name, requester.getName());
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            String c = (code == null || code.isEmpty()) ? StringUtils.nameToCode(name) : code;
            Date now = new Date();
            stmt.setString(1, c);
            stmt.setString(2, name);
            stmt.setTimestamp(3, new Timestamp(now.getTime()));
            stmt.setString(4, requester.getLoginName());
            stmt.execute();

            Notebook notebook = new Notebook();
            notebook.setCode(c);
            notebook.setName(name);
            notebook.setCreatedOn(now);
            notebook.setCreatedBy(requester.getLoginName());
            return notebook;
        }
    }

}
