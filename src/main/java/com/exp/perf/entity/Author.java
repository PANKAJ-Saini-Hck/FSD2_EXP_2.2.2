package com.exp.perf.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Author entity – parent side of a one-to-many relationship with Book.
 *
 * IMPORTANT for N+1 demo:
 *   fetch = FetchType.LAZY  means books are NOT loaded with the author.
 *   When we iterate authors and call author.getBooks(), Hibernate fires
 *   a SEPARATE SQL query for each author → N+1 queries total.
 */
@Entity
@Table(name = "author")
public class Author implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * LAZY fetch – this is the root cause of the N+1 problem.
     * When fixed with JOIN FETCH, a single query retrieves all data.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference   // prevents Jackson infinite recursion
    private List<Book> books = new ArrayList<>();

    // ─── Constructors ────────────────────────────────────────────────────────
    public Author() {}

    public Author(Long id, String name, String email, List<Book> books) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.books = books;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }

    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Book> getBooks()               { return books; }
    public void setBooks(List<Book> books)     { this.books = books; }

    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
