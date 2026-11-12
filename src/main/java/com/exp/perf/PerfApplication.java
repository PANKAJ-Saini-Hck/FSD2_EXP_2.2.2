package com.exp.perf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Experiment 2.2.2 – Backend Performance Optimization
 *
 * Concepts demonstrated:
 *   1. N+1 Query Problem  →  /benchmark/n1
 *   2. JOIN FETCH Fix     →  /benchmark/optimized
 *   3. Ehcache Caching    →  /benchmark/cached
 *   4. Native SQL Queries →  /authors/books/price-range
 */
@SpringBootApplication
@EnableCaching   // ← activates Spring's cache abstraction (Ehcache as provider)
public class PerfApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerfApplication.class, args);
    }
}
