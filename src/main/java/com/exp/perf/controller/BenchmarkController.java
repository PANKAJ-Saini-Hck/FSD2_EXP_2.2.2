package com.exp.perf.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exp.perf.service.AuthorService;

/**
 * BenchmarkController – measures and compares execution times.
 *
 * This controller is the KEY demo endpoint for the experiment.
 * It times three approaches and returns a JSON comparison:
 *
 *  GET /benchmark/n1         → Measures N+1 query time
 *  GET /benchmark/optimized  → Measures JOIN FETCH time
 *  GET /benchmark/cached     → Measures cached response time
 *  GET /benchmark/compare    → Runs all three and returns side-by-side comparison
 *
 * Expected results for JMeter presentation:
 *  n1       : ~50-150 ms  (many SQL queries)
 *  optimized: ~10-30 ms   (single SQL query)
 *  cached   : ~1-5 ms     (no SQL, from memory)
 */
@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final AuthorService authorService;

    public BenchmarkController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Benchmarks the N+1 query approach.
     * Watch the console — multiple SQL queries are fired.
     */
    @GetMapping("/n1")
    public ResponseEntity<Map<String, Object>> benchmarkN1() {
        long start = System.currentTimeMillis();

        authorService.getAuthorsWithBooks_N1Problem();

        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("approach",     "N+1 Query (LAZY fetch)");
        result.put("description",  "1 query for authors + N queries for books = N+1 total");
        result.put("queryCount",   "1 + 10 = 11 SQL queries");
        result.put("timeTakenMs",  elapsed);
        result.put("verdict",      "SLOW ❌ – avoid this pattern!");

        return ResponseEntity.ok(result);
    }

    /**
     * Benchmarks the JOIN FETCH optimized approach.
     * Only 1 SQL query is fired.
     */
    @GetMapping("/optimized")
    public ResponseEntity<Map<String, Object>> benchmarkOptimized() {
        long start = System.currentTimeMillis();

        authorService.getAuthorsWithBooks_Optimized();

        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("approach",     "JOIN FETCH (optimized)");
        result.put("description",  "Single SQL JOIN query fetches everything at once");
        result.put("queryCount",   "1 SQL query only");
        result.put("timeTakenMs",  elapsed);
        result.put("verdict",      "FAST ✅ – use JOIN FETCH for related data!");

        return ResponseEntity.ok(result);
    }

    /**
     * Benchmarks the cached response.
     * No SQL is fired – data returned directly from Ehcache memory.
     * NOTE: Call /benchmark/optimized first to warm up the cache.
     */
    @GetMapping("/cached")
    public ResponseEntity<Map<String, Object>> benchmarkCached() {
        // Warm cache first (if not already warm)
        authorService.getAuthorsWithBooks_Optimized();

        long start = System.currentTimeMillis();

        // This call should hit Ehcache, not DB
        authorService.getAuthorsWithBooks_Optimized();

        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("approach",     "Ehcache (in-memory cache)");
        result.put("description",  "Data served from Ehcache – zero DB queries");
        result.put("queryCount",   "0 SQL queries");
        result.put("timeTakenMs",  elapsed);
        result.put("verdict",      "FASTEST ⚡ – Ehcache eliminates DB round-trip!");

        return ResponseEntity.ok(result);
    }

    /**
     * Runs all three approaches and returns a side-by-side performance comparison.
     * This is the most useful endpoint for the PRESENTATION.
     *
     * Example response:
     * {
     *   "n1QueryMs": 87,
     *   "joinFetchMs": 14,
     *   "cacheMs": 2,
     *   "speedupJoinVsN1": "6.2x faster",
     *   "speedupCacheVsN1": "43.5x faster"
     * }
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareAll() {

        // ── 1. N+1 ──────────────────────────────────────────────
        long t1 = System.currentTimeMillis();
        authorService.getAuthorsWithBooks_N1Problem();
        long n1Ms = System.currentTimeMillis() - t1;

        // ── 2. JOIN FETCH ────────────────────────────────────────
        long t2 = System.currentTimeMillis();
        authorService.getAuthorsWithBooks_Optimized();
        long joinMs = System.currentTimeMillis() - t2;

        // ── 3. CACHED (second call – should be from cache) ───────
        long t3 = System.currentTimeMillis();
        authorService.getAuthorsWithBooks_Optimized();  // cache hit
        long cacheMs = System.currentTimeMillis() - t3;

        // ── Result ───────────────────────────────────────────────
        double joinSpeedup  = n1Ms > 0 ? (double) n1Ms / Math.max(joinMs,  1) : 0;
        double cacheSpeedup = n1Ms > 0 ? (double) n1Ms / Math.max(cacheMs, 1) : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title",             "Performance Comparison – EXP 2.2.2");
        result.put("n1_queryMs",        n1Ms   + " ms  (11 SQL queries)");
        result.put("joinFetch_queryMs", joinMs  + " ms  (1 SQL query)");
        result.put("cache_queryMs",     cacheMs + " ms  (0 SQL queries)");
        result.put("joinFetch_speedup", String.format("%.1fx faster than N+1", joinSpeedup));
        result.put("cache_speedup",     String.format("%.1fx faster than N+1", cacheSpeedup));
        result.put("conclusion",
                "JOIN FETCH resolves N+1; Ehcache further eliminates DB round-trips.");

        return ResponseEntity.ok(result);
    }
}
