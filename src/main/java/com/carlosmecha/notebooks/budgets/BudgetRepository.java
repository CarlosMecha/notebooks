package com.carlosmecha.notebooks.budgets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Budget repository.
 *
 * Created by carlos on 22/01/17.
 */
public interface BudgetRepository extends PagingAndSortingRepository<Budget, Integer> {

    @Query("SELECT b FROM Budget b WHERE b.notebook.code = :notebookCode")
    Page<Budget> findAllByNotebookCode(@Param("notebookCode") String notebookCode, Pageable pageable);
}
