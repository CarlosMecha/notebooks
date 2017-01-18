package com.carlosmecha.notebooks.pages;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Comment repository
 *
 * Created by carlos on 4/01/17.
 */
public interface CommentRepository extends PagingAndSortingRepository<Comment, Integer>{

    @Query("SELECT c FROM Comment c WHERE c.page.id = :id")
    List<Comment> findAllByPageId(@Param("id") int id);

}
