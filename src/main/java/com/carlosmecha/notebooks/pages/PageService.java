package com.carlosmecha.notebooks.pages;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Page service
 *
 * Created by carlos on 7/01/17.
 */
public class PageService {

    private final static Logger logger = LoggerFactory.getLogger(PageService.class);
    private final static String selectOne = "SELECT id, notebook_code, date, created_on, updated_on, created_by FROM pages WHERE id = ?";
    private final static String selectAll = "SELECT id, notebook_code, date, created_on, updated_on, created_by FROM pages WHERE notebook_code = ? ORDER BY date";
    private final static String selectAllIds = "SELECT id FROM pages WHERE notebook_code = ? ORDER BY date";
    private final static String selectFirst = "SELECT id, notebook_code, date, created_on, updated_on, created_by FROM pages WHERE notebook_code = ? ORDER BY date ASC LIMIT 1";
    private final static String selectLast = "SELECT id, notebook_code, date, created_on, updated_on, created_by FROM pages WHERE notebook_code = ? ORDER BY date DESC LIMIT 1";
    private final static String selectComments = "SELECT id, page_id, content, wrote_on, wrote_by, previous_comment_id, next_comment_id FROM page_comments WHERE page_id = ? ORDER BY previous_comment_id";
    private final static String insertPage = "INSERT INTO pages (notebook_code, date, created_on, updated_on, created_by) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private final static String insertComment = "INSERT INTO page_comments (page_id, content, wrote_on, wrote_by) VALUES (?, ?, ?, ?) RETURNING id";
    private final static String insertPageTag = "INSERT INTO page_tags (page_id, tag_id) VALUES (?, ?)";

    private TagService tags;

    public PageService() {
        this.tags = new TagService();
    }

    /**
     * Creates a new page.
     * @param notebook Notebook.
     * @param date Date.
     * @param tagCodes A list of tags.
     * @param text Text.
     * @param requester The user who request the creation.
     * @return The new expense.
     */
    public Page create(Connection conn, Notebook notebook, Date date, Set<String> tagCodes, String text, User requester) throws SQLException {
        logger.debug("Creating page of {}", notebook.getCode());

        Date now = new Date();

        // Page
        Page page = new Page();

        try (PreparedStatement stmt = conn.prepareStatement(insertPage)) {
            
            stmt.setString(1, notebook.getCode());
            stmt.setTimestamp(2, new Timestamp(date.getTime()));
            stmt.setTimestamp(3, new Timestamp(now.getTime()));
            stmt.setTimestamp(4, new Timestamp(now.getTime()));
            stmt.setString(5, requester.getLoginName());

            try (ResultSet result = stmt.executeQuery()) {
                if (!result.next()) {
                    // Internal error
                    logger.error("The id was not returned when creating page");
                    throw new RuntimeException("Unable to create page");
                }
                page.setId(result.getInt(1));
                page.setNotebookCode(notebook.getCode());
                page.setDate(date);
                page.setCreatedOn(now);
                page.setUpdatedOn(now);
                page.setCreatedBy(requester.getLoginName());
            }
        }

        // Comment
        try (PreparedStatement stmt = conn.prepareStatement(insertComment)) {
            stmt.setInt(1, page.getId());
            stmt.setString(2, text);
            stmt.setTimestamp(3, new Timestamp(now.getTime()));
            stmt.setString(4, requester.getLoginName());

            try (ResultSet result = stmt.executeQuery()) {
                
                if (!result.next()) {
                    // Internal error
                    logger.error("The id was not returned when creating comment");
                    throw new RuntimeException("Unable to create comment");
                }
                Comment comment = new Comment();
                comment.setId(result.getInt(1));
                comment.setPage(page.getId());
                comment.setContent(text);
                comment.setWroteOn(now);
                comment.setWroteBy(requester.getLoginName());

                Set<Comment> comments = new HashSet<>();
                comments.add(comment);
                page.setComments(comments);
            }
        }

        // Tags
        Set<Tag> pageTags = new HashSet<>(); 

        for (String tagCode : tagCodes) {
            Optional<Tag> tag = tags.get(conn, notebook.getCode(), tagCode);
            Tag t = (tag.isPresent()) ? tag.get() : tags.create(conn, notebook, tagCode);
            
            try (PreparedStatement stmt = conn.prepareStatement(insertPageTag)) {
                stmt.setInt(1, page.getId());
                stmt.setInt(2, t.getId());
                stmt.execute();
            }

            pageTags.add(t);
        }

        page.setTags(pageTags);
        return page;
    }

    /**
     * Gets the page.
     * @param id Page id.
     * @return The page if found.
     */
    public Optional<Page> get(Connection conn, int id) throws SQLException {
        logger.debug("Looking for page {}", id);
        try (PreparedStatement stmt = conn.prepareStatement(selectOne)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Page.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Gets all pages of a notebook
     * @param notebook Notebook.
     * @return List of pages, sorted by date.
     */
    public List<Page> getAll(Connection conn, Notebook notebook) throws SQLException {
        logger.debug("Looking for pages for {}", notebook.getCode());
        try (PreparedStatement stmt = conn.prepareStatement(selectAll)) {
            stmt.setString(1, notebook.getCode());
            try (ResultSet result = stmt.executeQuery()) {
                List<Page> pages = new LinkedList<>();
                while (result.next()) {
                    pages.add(Page.fromRow(result));
                }
                return pages;
            }
        }
    }

    /**
     * Get all page ids of a notebook.
     * @param notebook Notebook.
     * @return List of page ids, sorted by date.
     */
    public List<Integer> getAllIds(Connection conn, Notebook notebook) throws SQLException {
        logger.debug("Looking for page ids for {}", notebook.getCode());
        try (PreparedStatement stmt = conn.prepareStatement(selectAllIds)) {
            stmt.setString(1, notebook.getCode());
            try (ResultSet result = stmt.executeQuery()) {
                List<Integer> pages = new LinkedList<>();
                while (result.next()) {
                    pages.add(result.getInt(1));
                }
                return pages;
            }
        }
    }

    /**
     * Returns the first page of the notebook.
     * @param notebook Notebook.
     * @return The first page if found.
     */
    public Optional<Page> getFirst(Connection conn, Notebook notebook) throws SQLException {
        logger.debug("Looking for the first page of {}", notebook.getCode());
        try (PreparedStatement stmt = conn.prepareStatement(selectFirst)) {
            stmt.setString(1, notebook.getCode());
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Page.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Gets the last page of the notebook.
     * @param notebook Notebook.
     * @return The last page if found.
     */
    public Optional<Page> getLast(Connection conn, Notebook notebook) throws SQLException {
        logger.debug("Looking for the last page of {}", notebook.getCode());
        try (PreparedStatement stmt = conn.prepareStatement(selectLast)) {
            stmt.setString(1, notebook.getCode());
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(Page.fromRow(result));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Gets the comments of a page sorted.
     * @param pageId Page id.
     * @return A list of comments.
     */
    public List<Comment> getSortedComments(Connection conn, int pageId) throws SQLException {
        List<Comment> comments = new LinkedList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(selectComments)) {
            stmt.setInt(1, pageId);

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    comments.add(Comment.fromRow(result));
                }
            }
        }

        if(comments.isEmpty()) {
            return Collections.emptyList();
        }

        // Sorting
        List<Comment> sortedComments = new LinkedList<>();
        Map<Integer, Comment> map = new HashMap<>();
        for(Comment comment : comments) {
            map.put(comment.getId(), comment);
        }

        int count = 0;
        Comment next = comments.get(0);
        while(next != null) {
            count++;
            sortedComments.add(next);
            int nextId = next.getNextCommentId();
            if (nextId > 0) {
                next = map.get(new Integer(nextId));
            } else {
                next = null;
            }
        }

        if(count != comments.size()) {
            logger.error("Inconsistent comment ordering for page {}", pageId);
        }

        return sortedComments;

    }

}
