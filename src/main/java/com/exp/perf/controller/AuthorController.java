package com.exp.perf.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exp.perf.entity.Author;
import com.exp.perf.entity.Book;
import com.exp.perf.service.AuthorService;

/**
 * AuthorController – REST API for Author and Book management.
 *
 * Base URL: http://localhost:8080
 *
 * Author Endpoints:
 *  GET    /authors                          → All authors (cached)
 *  GET    /authors/{id}                     → Single author (cached)
 *  GET    /authors/with-books/n1            → N+1 problem demo
 *  GET    /authors/with-books/optimized     → JOIN FETCH solution (cached)
 *  GET    /authors/{id}/with-books          → Single author + books (JOIN FETCH)
 *  GET    /authors/search?name=xyz          → Name search (JPQL)
 *  GET    /authors/by-price?min=300&max=600 → Native SQL price range filter
 *  POST   /authors                          → Create (evicts cache)
 *  PUT    /authors/{id}                     → Update (updates cache)
 *  DELETE /authors/{id}                     → Delete (clears cache)
 *
 * Book Endpoints:
 *  GET    /books                            → All books (cached)
 *  GET    /books/price-range?min=300&max=600→ Books in price range (cached)
 */
@RestController
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ─── Author CRUD ────────────────────────────────────────────────────────

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return ResponseEntity.ok(authorService.createAuthor(author));
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id,
                                               @RequestBody Author author) {
        return ResponseEntity.ok(authorService.updateAuthor(id, author));
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    // ─── N+1 vs JOIN FETCH demo ─────────────────────────────────────────────

    /**
     * Demonstrates the N+1 query problem.
     * Watch the console — you'll see 11 SQL queries for 10 authors!
     */
    @GetMapping("/authors/with-books/n1")
    public ResponseEntity<List<Author>> getWithBooksN1() {
        return ResponseEntity.ok(authorService.getAuthorsWithBooks_N1Problem());
    }

    /**
     * Demonstrates the JOIN FETCH solution.
     * Only 1 SQL query is fired — and result is cached!
     */
    @GetMapping("/authors/with-books/optimized")
    public ResponseEntity<List<Author>> getWithBooksOptimized() {
        return ResponseEntity.ok(authorService.getAuthorsWithBooks_Optimized());
    }

    /**
     * Single author with their books — JOIN FETCH version.
     */
    @GetMapping("/authors/{id}/with-books")
    public ResponseEntity<Author> getAuthorWithBooks(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorWithBooks(id));
    }

    // ─── Queries ────────────────────────────────────────────────────────────

    /**
     * Search authors by name — JPQL LIKE query.
     * Example: GET /authors/search?name=sha
     */
    @GetMapping("/authors/search")
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam String name) {
        return ResponseEntity.ok(authorService.searchAuthors(name));
    }

    /**
     * Find authors who have books within price range — Native SQL.
     * Example: GET /authors/by-price?min=300&max=600
     */
    @GetMapping("/authors/by-price")
    public ResponseEntity<List<Author>> getAuthorsByPrice(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(authorService.findAuthorsByPriceRange(min, max));
    }

    // ─── Books ──────────────────────────────────────────────────────────────

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(authorService.getAllBooks());
    }

    @GetMapping("/books/price-range")
    public ResponseEntity<List<Book>> getBooksByPrice(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(authorService.getBooksByPriceRange(min, max));
    }
}
