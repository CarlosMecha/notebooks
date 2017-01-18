package com.carlosmecha.notebooks.pages;

import com.carlosmecha.notebooks.notebooks.Notebook;
import com.carlosmecha.notebooks.tags.Tag;
import com.carlosmecha.notebooks.tags.TagService;
import com.carlosmecha.notebooks.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Page service
 *
 * Created by carlos on 7/01/17.
 */
@Service
public class PageService {

    private final static Logger logger = LoggerFactory.getLogger(PageService.class);

    private PageRepository pages;
    private CommentRepository comments;
    private TagService tags;

    @Autowired
    public PageService(PageRepository pages, CommentRepository comments, TagService tags) {
        this.pages = pages;
        this.comments = comments;
        this.tags = tags;
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
    @Transactional
    public Page create(Notebook notebook, Date date, Set<String> tagCodes, String text, User requester) {
        logger.debug("Creating page of {}", notebook.getCode());

        Set<Tag> tagSet = tagCodes.stream().map(t -> {
            Optional<Tag> tag = tags.get(notebook.getCode(), t);
            return (tag.isPresent()) ? tag.get() : new Tag(notebook, t);
        }).collect(Collectors.toSet());

        Page page = new Page(notebook, date, requester, tagSet);
        pages.save(page);

        Comment comment = new Comment(page, text, new Date(), requester);
        comments.save(comment);

        return page;
    }

    /**
     * Gets all pages of a notebook
     * @param notebook Notebook.
     * @return List of pages, sorted by date.
     */
    public List<Page> getAll(Notebook notebook) {
        logger.debug("Looking for pages for {}", notebook.getCode());
        return pages.findAllByNotebookCode(notebook.getCode());
    }

    /**
     * Get all page ids of a notebook.
     * @param notebook Notebook.
     * @return List of page ids, sorted by date.
     */
    public List<Integer> getAllIds(Notebook notebook) {
        logger.debug("Looking for page ids for {}", notebook.getCode());
        return pages.findAllIdsByNotebookCode(notebook.getCode());
    }

    /**
     * Gets the page.
     * @param id Page id.
     * @return The page if found.
     */
    public Optional<Page> get(int id) {
        logger.debug("Looking for page {}", id);
        return Optional.ofNullable(pages.findOne(id));
    }

    /**
     * Returns the first page of the notebook.
     * @param notebook Notebook.
     * @return The first page if found.
     */
    public Optional<Page> getFirst(Notebook notebook) {
        logger.debug("Looking for the first page of {}", notebook.getCode());
        List<Page> pags = pages.findAllByNotebookCode(notebook.getCode(), new PageRequest(0, 1, Sort.Direction.ASC, "date"));
        if(pags.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pags.get(0));
    }

    /**
     * Gets the last page of the notebook.
     * @param notebook Notebook.
     * @return The last page if found.
     */
    public Optional<Page> getLast(Notebook notebook) {
        logger.debug("Looking for the last page of {}", notebook.getCode());
        List<Page> pags = pages.findAllByNotebookCode(notebook.getCode(), new PageRequest(0, 1, Sort.Direction.DESC, "date"));
        if(pags.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pags.get(0));
    }

    /**
     * Gets the comments of a page sorted.
     * @param pageId Page id.
     * @return A list of comments.
     */
    public List<Comment> getSortedComments(int pageId) {
        List<Comment> commts = comments.findAllByPageId(pageId);
        if(commts.isEmpty()) {
            return Collections.emptyList();
        }

        // Sorting
        List<Comment> sortedComments = new LinkedList<>();
        Map<Integer, Comment> map = new HashMap<>();
        for(Comment comment : commts) {
            map.put(comment.getId(), comment);
        }

        int count = 0;
        Comment next = commts.get(0);
        while(next != null) {
            sortedComments.add(next);
            next = next.getNextComment();
            count++;
        }

        if(count != commts.size()) {
            logger.error("Inconsistent comment ordering for page {}", pageId);
        }

        return sortedComments;

    }

}
