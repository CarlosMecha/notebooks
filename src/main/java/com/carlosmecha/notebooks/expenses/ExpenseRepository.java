package com.carlosmecha.notebooks.expenses;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Expenses repository
 *
 * Created by Carlos on 12/25/16.
 */
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.notebook.code = :notebookCode")
    List<Expense> findAllByNotebookCode(@Param("notebookCode") String notebookCode, Pageable pageable);

    /**
     * Finds all expenses by date range and notebook code.
     * @param startDate Start date.
     * @param endDate End date.
     * @return An iterable.
     */
    @Query("SELECT e FROM Expense e WHERE e.notebook.code = :notebookCode AND e.date >= :startDate AND e.date <= :endDate ORDER BY e.date")
    List<Expense> findAllByNotebookCodeAndDateRange(@Param("notebookCode") String notebookCode,
                                         @Param("startDate") Date startDate, @Param("endDate") Date endDate);


}
