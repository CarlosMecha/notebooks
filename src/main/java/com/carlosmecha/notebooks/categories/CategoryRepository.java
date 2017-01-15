package com.carlosmecha.notebooks.categories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Category repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.notebook.code = :notebookCode")
    List<Category> findAllByNotebookCode(@Param("notebookCode") String notebookCode, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.notebook.code = :notebookCode AND c.code = :code")
    List<Category> findByNotebookCodeAndCode(@Param("notebookCode") String notebookCode, @Param("code") String code);

}
