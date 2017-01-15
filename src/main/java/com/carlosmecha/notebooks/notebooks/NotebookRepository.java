package com.carlosmecha.notebooks.notebooks;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Notebook repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface NotebookRepository extends PagingAndSortingRepository<Notebook, String> {
}
