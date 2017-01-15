package com.carlosmecha.notebooks.tags;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Tag repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query("SELECT t FROM Tag t WHERE t.notebook.code = :notebookCode")
    List<Tag> findAllByNotebookCode(@Param("notebookCode") String notebookCode);

    @Query("SELECT t FROM Tag t WHERE t.notebook.code = :notebookCode AND t.code = :code")
    List<Tag> findByNotebookCodeAndCode(@Param("notebookCode") String notebookCode, @Param("code") String code);
}
