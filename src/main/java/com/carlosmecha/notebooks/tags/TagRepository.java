package com.carlosmecha.notebooks.tags;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Tag repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query("SELECT t FROM Tag t WHERE t.notebook.code = :notebookCode")
    Iterable<Tag> findAllByNotebookCode(@Param("notebookCode") String notebookCode);

    @Query("SELECT t FROM Tag t WHERE t.notebook.code = :notebookCode AND t.code = :code")
    Tag findOneByNotebookCodeAndCode(@Param("notebookCode") String notebookCode, @Param("code") String code);
}
