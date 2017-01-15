package com.carlosmecha.notebooks.notebooks;

import com.carlosmecha.notebooks.users.User;
import com.carlosmecha.notebooks.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Notebook common operations.
 *
 * Created by carlos on 15/01/17.
 */
@Service
public class NotebookService {

    private final static Logger logger = LoggerFactory.getLogger(NotebookService.class);

    private NotebookRepository repository;

    @Autowired
    public NotebookService(NotebookRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a notebook.
     * @param code Notebook code.
     * @return The notebook if found.
     */
    public Optional<Notebook> get(String code) {
        logger.debug("Looking for notebook {}", code);
        Notebook notebook = repository.findOne(code);
        return Optional.ofNullable(notebook);
    }

    /**
     * Gets all notebooks.
     * @return A list of notebooks.
     */
    public List<Notebook> getAll() {
        logger.debug("Looking for all notebooks");
        return ListUtils.toList(repository.findAll());
    }

    /**
     * Creates the notebook.
     * @param code Category code.
     * @param name Category name.
     * @param requester The user.
     * @return New notebook.
     */
    @Transactional
    public Notebook create(String code, String name, User requester) {
        logger.debug("Creating notebook {} by {}", name, requester.getName());
        Notebook notebook = new Notebook(code, name, requester);
        repository.save(notebook);
        return notebook;
    }

}
