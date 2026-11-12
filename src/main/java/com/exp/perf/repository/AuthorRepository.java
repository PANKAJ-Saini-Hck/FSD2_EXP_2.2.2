package com.exp.perf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exp.perf.entity.Author;

/**
 * AuthorRepository – demonstrates three query styles:
 *
 *  1. findAll()              → default JPA (triggers N+1 when books are accessed)
 *  2. findAllWithBooks()     → JPQL JOIN FETCH (resolves N+1 in ONE query)
 *  3. findByIdWithBooks()    → JOIN FETCH for single author
 *  4. findBooksByPriceRange()→ Native SQL query (raw SQL control)
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // ─────────────────────────────────────────────────────────────────────
    // JPQL JOIN FETCH – solves N+1 by fetching Author + Books in ONE query
    // ─────────────────────────────────────────────────────────────────────
    /**
     * Retrieves all authors along with their books in a single SQL JOIN query.
     * Compare the SQL output in logs against the N+1 version.
     *
     * Generated SQL (approximately):
     *   SELECT a.*, b.*
     *   FROM author a
     *   LEFT JOIN book b ON b.author_id = a.id
     */
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllWithBooks();

    /**
     * JOIN FETCH for a single author by ID.
     */
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);

    // ─────────────────────────────────────────────────────────────────────
    // NATIVE SQL QUERY – full SQL control for complex filtering
    // ─────────────────────────────────────────────────────────────────────
    /**
     * Finds authors who have at least one book within the given price range.
     * Uses raw SQL for demonstration of native query capabilities.
     *
     * @param minPrice minimum book price
     * @param maxPrice maximum book price
     */
    @Query(
        value = "SELECT DISTINCT a.* FROM author a " +
                "INNER JOIN book b ON b.author_id = a.id " +
                "WHERE b.price BETWEEN :minPrice AND :maxPrice",
        nativeQuery = true
    )
    List<Author> findAuthorsByBookPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    /**
     * Search authors by name (case-insensitive, partial match).
     */
    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> searchByName(@Param("name") String name);
}
