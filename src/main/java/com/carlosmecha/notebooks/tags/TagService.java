package com.carlosmecha.notebooks.tags;

import com.carlosmecha.notebooks.notebooks.Notebook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Tag's common operations
 *
 * Created by carlos on 15/01/17.
 */
@Service
public class TagService {

    private final static Logger logger = LoggerFactory.getLogger(TagService.class);

    private TagRepository repository;

    @Autowired
    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a tag.
     * @param id tag id.
     * @return The tag if found.
     */
    public Optional<Tag> get(int id) {
        Tag tag = repository.findOne(id);
        return Optional.ofNullable(tag);
    }

    /**
     * Retrieves a tag.
     * @param notebookCode Notebook code.
     * @param code Tag code.
     * @return The tag if found.
     */
    public Optional<Tag> get(String notebookCode, String code) {
        Tag tag = repository.findOneByNotebookCodeAndCode(notebookCode, code);
        return Optional.ofNullable(tag);
    }
    /**
     * Creates a tag.
     * @param notebook Notebook.
     * @param code Tag code.
     * @return The new tag.
     */
    @Transactional
    public Tag createTag(Notebook notebook, String code) {
        logger.debug("Creating tag {}", code);
        Tag tag = new Tag(notebook, code);
        repository.save(tag);
        return tag;
    }

    /**
     * Gets all tags for a notebook.
     * @param notebookCode Notebook code.
     * @return List of tags.
     */
    public Iterable<Tag> getTags(String notebookCode) {
        logger.debug("Looking for all tags.");
        return repository.findAllByNotebookCode(notebookCode);
    }

}
