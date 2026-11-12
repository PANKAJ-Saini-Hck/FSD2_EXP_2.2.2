package com.exp.perf.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exp.perf.entity.Author;
import com.exp.perf.entity.Book;
import com.exp.perf.repository.AuthorRepository;
import com.exp.perf.repository.BookRepository;

/**
 * AuthorService – the heart of the experiment.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Caching Annotations Used                                   │
 * │                                                             │
 * │  @Cacheable  – Return value stored in cache; next call      │
 * │                returns cached data WITHOUT hitting DB.       │
 * │                                                             │
 * │  @CachePut   – Always executes method AND updates cache.    │
 * │                Used on update so cache stays fresh.          │
 * │                                                             │
 * │  @CacheEvict – Removes entry from cache.                    │
 * │                Used on create/delete so stale data is gone. │
 * └─────────────────────────────────────────────────────────────┘
 */
@Service
@Transactional
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;
    private final BookRepository   bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository   = bookRepository;
    }

    // ═══════════════════════════════════════════════════════════════
    //  READ – with Caching
    // ═══════════════════════════════════════════════════════════════

    /**
     * Returns all authors (without books).
     * First call hits DB; subsequent calls within 60 s return cached data.
     *
     * @Cacheable stores the returned List under the key "allAuthors"
     * in the "authors" Ehcache region.
     */
    @Cacheable(value = "authors", key = "'allAuthors'")
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        log.info(">>> [DB HIT] Fetching all authors from DATABASE");
        return authorRepository.findAll();
    }

    /**
     * Returns a single author by ID.
     * Cached individually by authorId in the "authors" cache.
     */
    @Cacheable(value = "authors", key = "#id")
    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        log.info(">>> [DB HIT] Fetching author id={} from DATABASE", id);
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
    }

    // ═══════════════════════════════════════════════════════════════
    //  N+1 PROBLEM DEMONSTRATION  (intentionally slow)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Demonstrates the N+1 problem.
     *
     * Flow:
     *   1. SELECT * FROM author          → 1 query
     *   2. For each author: SELECT * FROM book WHERE author_id = ?  → N queries
     *   TOTAL = 1 + N queries (N = number of authors)
     *
     * Watch the SQL logs — you'll see 11 queries for 10 authors!
     */
    @Transactional(readOnly = true)
    public List<Author> getAuthorsWithBooks_N1Problem() {
        log.warn("=== [N+1 PROBLEM] Starting N+1 query demonstration ===");
        List<Author> authors = authorRepository.findAll();  // Query 1: fetch all authors

        // Accessing books triggers a SEPARATE query for EACH author (N queries)
        authors.forEach(author -> {
            int bookCount = author.getBooks().size();  // ← triggers N individual queries!
            log.warn("  Author '{}' has {} books", author.getName(), bookCount);
        });

        log.warn("=== [N+1 PROBLEM] Total queries fired = 1 + {} = {} ===",
                authors.size(), 1 + authors.size());
        return authors;
    }

    // ═══════════════════════════════════════════════════════════════
    //  JOIN FETCH – N+1 SOLUTION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Resolves the N+1 problem using JOIN FETCH.
     *
     * Flow:
     *   SELECT DISTINCT a.*, b.* FROM author a LEFT JOIN book b ON b.author_id = a.id
     *   TOTAL = 1 query (regardless of number of authors!)
     *
     * This result is also CACHED so repeated calls don't hit DB at all.
     */
    @Cacheable(value = "authors", key = "'withBooks'")
    @Transactional(readOnly = true)
    public List<Author> getAuthorsWithBooks_Optimized() {
        log.info(">>> [JOIN FETCH] Fetching authors with books using JOIN FETCH (1 query)");
        return authorRepository.findAllWithBooks();  // Single query!
    }

    /**
     * Fetch a single author with all their books – JOIN FETCH version.
     */
    @Cacheable(value = "authors", key = "'withBooks_' + #id")
    @Transactional(readOnly = true)
    public Author getAuthorWithBooks(Long id) {
        log.info(">>> [JOIN FETCH] Fetching author id={} with books", id);
        return authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
    }

    // ═══════════════════════════════════════════════════════════════
    //  NATIVE SQL QUERY
    // ═══════════════════════════════════════════════════════════════

    /**
     * Uses a native SQL query to find authors by book price range.
     * Demonstrates raw SQL usage when JPQL is insufficient.
     */
    @Transactional(readOnly = true)
    public List<Author> findAuthorsByPriceRange(Double min, Double max) {
        log.info(">>> [NATIVE SQL] Finding authors with books priced between {} and {}", min, max);
        return authorRepository.findAuthorsByBookPriceRange(min, max);
    }

    /**
     * Search authors by name (partial match).
     */
    @Transactional(readOnly = true)
    public List<Author> searchAuthors(String name) {
        return authorRepository.searchByName(name);
    }

    // ═══════════════════════════════════════════════════════════════
    //  WRITE OPERATIONS – with cache management
    // ═══════════════════════════════════════════════════════════════

    /**
     * Creates a new author.
     * @CacheEvict removes the "allAuthors" cache so stale data isn't served.
     */
    @CacheEvict(value = "authors", key = "'allAuthors'")
    public Author createAuthor(Author author) {
        log.info(">>> [CACHE EVICT] Creating author and evicting 'allAuthors' cache");
        return authorRepository.save(author);
    }

    /**
     * Updates an existing author.
     * @CachePut updates the cache entry with the new data.
     */
    @CachePut(value = "authors", key = "#id")
    public Author updateAuthor(Long id, Author updatedAuthor) {
        log.info(">>> [CACHE PUT] Updating author id={} and updating cache", id);
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));
        existing.setName(updatedAuthor.getName());
        existing.setEmail(updatedAuthor.getEmail());
        return authorRepository.save(existing);
    }

    /**
     * Deletes an author.
     * @CacheEvict removes all author cache entries.
     */
    @CacheEvict(value = "authors", allEntries = true)
    public void deleteAuthor(Long id) {
        log.info(">>> [CACHE EVICT ALL] Deleting author id={} and clearing all author caches", id);
        authorRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════
    //  BOOKS
    // ═══════════════════════════════════════════════════════════════

    @Cacheable(value = "books", key = "'all'")
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        log.info(">>> [DB HIT] Fetching all books from DATABASE");
        return bookRepository.findAll();
    }

    @Cacheable(value = "books", key = "'priceRange_' + #min + '_' + #max")
    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(Double min, Double max) {
        log.info(">>> [CACHED QUERY] Books between price {} and {}", min, max);
        return bookRepository.findByPriceBetween(min, max);
    }
}
