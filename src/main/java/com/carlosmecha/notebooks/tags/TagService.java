package com.carlosmecha.notebooks.tags;

import com.carlosmecha.notebooks.notebooks.Notebook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * Tag's common operations
 *
 * Created by carlos on 15/01/17.
 */
public class TagService {

    private final static Logger logger = LoggerFactory.getLogger(TagService.class);
    private final static String selectOne = "SELECT id, code, notebook_code, created_on FROM tags WHERE id = ?";
    private final static String selectOneByCode = "SELECT id, code, notebook_code, created_on FROM tags WHERE notebook_code = ? AND code = ?";
    private final static String selectAll = "SELECT id, code, notebook_code, created_on FROM tags WHERE notebook_code = ? ORDER BY code";
    private final static String insert = "INSERT INTO tags (code, notebook_code, created_on) VALUES (?, ?, ?) RETURNING id";

    public TagService() {
    }

    /**
     * Retrieves a tag.
     * @param id tag id.
     * @return The tag if found.
     */
    public Optional<Tag> get(Connection conn, int id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectOne)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Tag.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Retrieves a tag.
     * @param notebookCode Notebook code.
     * @param code Tag code.
     * @return The tag if found.
     */
    public Optional<Tag> get(Connection conn, String notebookCode, String code) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(selectOneByCode)) {
            stmt.setString(1, notebookCode);
            stmt.setString(2, code);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Tag.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }
    /**
     * Creates a tag.
     * @param notebook Notebook.
     * @param code Tag code.
     * @return The new tag.
     */
    public Tag create(Connection conn, Notebook notebook, String code) throws SQLException {
        logger.debug("Creating tag {}", code);
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            Date now = new Date();
            stmt.setString(1, code);
            stmt.setString(2, notebook.getCode());
            stmt.setTimestamp(3, new Timestamp(now.getTime()));
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    Tag tag = new Tag();
                    tag.setId(result.getInt(1));
                    tag.setCode(code);
                    tag.setNotebookCode(notebook.getCode());
                    tag.setCreatedOn(now);
                    return tag;
                }
                // Internal error
                logger.error("The id was not returned when creating tag");
                throw new RuntimeException("Unable to create tag");
            }
        }
    }

    /**
     * Gets all tags for a notebook.
     * @param notebookCode Notebook code.
     * @return List of tags.
     */
    public List<Tag> getAll(Connection conn, String notebookCode) throws SQLException {
        logger.debug("Looking for all tags.");
        try (PreparedStatement stmt = conn.prepareStatement(selectAll)) {
            stmt.setString(1, notebookCode);
            try (ResultSet result = stmt.executeQuery()) {
                List<Tag> tags = new LinkedList<>();
                while (result.next()) {
                    tags.add(Tag.fromRow(result));
                }
                return tags;
            }
        }
    }

}
