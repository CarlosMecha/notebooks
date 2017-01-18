package com.carlosmecha.notebooks.pages;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Page repository
 *
 * Created by carlos on 4/01/17.
 */
public interface PageRepository extends PagingAndSortingRepository<Page, Integer>{

    @Query("SELECT p FROM Page p WHERE p.notebook.code = :code ORDER BY p.date")
    List<Page> findAllByNotebookCode(@Param("code") String notebookCode);

    @Query("SELECT p FROM Page p WHERE p.notebook.code = :code")
    List<Page> findAllByNotebookCode(@Param("code") String notebookCode, Pageable pageable);

    @Query("SELECT p.id FROM Page p WHERE p.notebook.code = :code ORDER BY p.date")
    List<Integer> findAllIdsByNotebookCode(@Param("code") String notebookCode);

}
