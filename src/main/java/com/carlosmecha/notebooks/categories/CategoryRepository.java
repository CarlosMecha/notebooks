package com.carlosmecha.notebooks.categories;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Category repository.
 *
 * Created by Carlos on 12/25/16.
 */
public interface CategoryRepository extends PagingAndSortingRepository<Category, String> {
}
