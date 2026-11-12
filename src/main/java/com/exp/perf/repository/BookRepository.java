package com.exp.perf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exp.perf.entity.Book;

/**
 * BookRepository – standard CRUD + price filter query.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find books within a price range using JPQL.
     */
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :min AND :max ORDER BY b.price ASC")
    List<Book> findByPriceBetween(@Param("min") Double min, @Param("max") Double max);

    /**
     * Find all books by author ID.
     */
    List<Book> findByAuthorId(Long authorId);
}
