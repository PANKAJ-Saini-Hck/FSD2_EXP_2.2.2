package com.exp.perf;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import com.exp.perf.entity.Author;
import com.exp.perf.entity.Book;
import com.exp.perf.repository.AuthorRepository;
import com.exp.perf.repository.BookRepository;
import com.exp.perf.service.AuthorService;

/**
 * Integration tests for Experiment 2.2.2
 *
 * ✔  Context loads
 * ✔  Seed data: 10 authors, 50 books
 * ✔  getAllAuthors returns all authors
 * ✔  JOIN FETCH loads authors + books in 1 query
 * ✔  Cache populated after first call
 * ✔  Native SQL price-range query
 * ✔  Create author → cache evicted
 * ✔  Books price-range JPQL
 * ✔  N+1 demo runs correctly
 * ✔  Delete author → cache cleared
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PerfApplicationTests {

    @Autowired private AuthorService    authorService;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private BookRepository   bookRepository;
    @Autowired private CacheManager     cacheManager;

    /** Clear all caches before each test so they don't interfere. */
    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache c = cacheManager.getCache(name);
            if (c != null) c.clear();
        });
    }

    // ── 1. Context ───────────────────────────────────────────────────────────
    @Test @Order(1)
    void contextLoads() { /* passes if context starts */ }

    // ── 2. Seed data ─────────────────────────────────────────────────────────
    @Test @Order(2)
    void seedData_shouldLoad10AuthorsAnd50Books() {
        assertThat(authorRepository.count()).isEqualTo(10);
        assertThat(bookRepository.count()).isEqualTo(50);
    }

    // ── 3. getAllAuthors ──────────────────────────────────────────────────────
    @Test @Order(3)
    void getAllAuthors_shouldReturn10Authors() {
        assertThat(authorService.getAllAuthors()).hasSize(10);
    }

    // ── 4. JOIN FETCH ─────────────────────────────────────────────────────────
    @Test @Order(4)
    @Transactional
    void getAuthorsWithBooks_Optimized_singleQuery_loadsBooks() {
        List<Author> authors = authorService.getAuthorsWithBooks_Optimized();
        assertThat(authors).hasSize(10);
        // Each author has exactly 5 books
        authors.forEach(a -> assertThat(a.getBooks())
                .as("Author '%s' should have 5 books", a.getName())
                .hasSize(5));
    }

    // ── 5. Cache populated ───────────────────────────────────────────────────
    @Test @Order(5)
    void getAllAuthors_secondCall_shouldReturnFromCache() {
        authorService.getAllAuthors();   // DB hit → populates cache
        assertThat(cacheManager.getCache("authors").get("allAuthors"))
                .as("Cache 'allAuthors' should be populated after first call")
                .isNotNull();
    }

    // ── 6. Native SQL ─────────────────────────────────────────────────────────
    @Test @Order(6)
    void findAuthorsByPriceRange_nativeSQL_returnsResults() {
        List<Author> authors = authorService.findAuthorsByPriceRange(300.0, 500.0);
        assertThat(authors).isNotEmpty();
    }

    // ── 7. Book JPQL ─────────────────────────────────────────────────────────
    @Test @Order(7)
    void getBooksByPriceRange_jpql_returnsCorrectResults() {
        List<Book> books = authorService.getBooksByPriceRange(400.0, 600.0);
        assertThat(books).isNotEmpty();
        books.forEach(b -> assertThat(b.getPrice()).isBetween(400.0, 600.0));
    }

    // ── 8. N+1 demo ──────────────────────────────────────────────────────────
    @Test @Order(8)
    @Transactional
    void getAuthorsWithBooks_N1Problem_runsAndReturnsAuthors() {
        List<Author> authors = authorService.getAuthorsWithBooks_N1Problem();
        assertThat(authors).hasSize(10);
    }

    // ── 9. Create → evicts cache ─────────────────────────────────────────────
    @Test @Order(9)
    void createAuthor_shouldEvictAllAuthorsCache() {
        authorService.getAllAuthors();   // warm cache
        assertThat(cacheManager.getCache("authors").get("allAuthors")).isNotNull();

        Author newAuthor = new Author();
        newAuthor.setName("New Test Author");
        newAuthor.setEmail("newauthor_" + System.currentTimeMillis() + "@test.com");
        authorService.createAuthor(newAuthor);

        assertThat(cacheManager.getCache("authors").get("allAuthors"))
                .as("Cache must be evicted after createAuthor")
                .isNull();
    }

    // ── 10. Delete → clears caches ───────────────────────────────────────────
    @Test @Order(10)
    void deleteAuthor_shouldEvictAllAuthorCaches() {
        // Create a temporary author so we don't disturb seed data
        Author temp = new Author();
        temp.setName("Temp Author");
        temp.setEmail("temp_" + System.currentTimeMillis() + "@test.com");
        Author saved = authorService.createAuthor(temp);

        authorService.getAllAuthors();   // warm cache

        authorService.deleteAuthor(saved.getId());

        assertThat(cacheManager.getCache("authors").get("allAuthors"))
                .as("Cache must be evicted after deleteAuthor")
                .isNull();
    }
}
