package com.exp.perf.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Book entity – child side of the Author → Book relationship.
 */
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double price;

    /**
     * Many books belong to one author.
     * @JsonBackReference prevents circular JSON serialisation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonBackReference
    private Author author;

    // ─── Constructors ────────────────────────────────────────────────────────
    public Book() {}

    public Book(Long id, String title, Double price, Author author) {
        this.id     = id;
        this.title  = title;
        this.price  = price;
        this.author = author;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getTitle()               { return title; }
    public void setTitle(String title)     { this.title = title; }

    public Double getPrice()               { return price; }
    public void setPrice(Double price)     { this.price = price; }

    public Author getAuthor()              { return author; }
    public void setAuthor(Author author)   { this.author = author; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', price=" + price + "}";
    }
}
