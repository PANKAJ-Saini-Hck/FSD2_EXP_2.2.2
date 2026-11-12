# Backend Performance Optimization – Caching and Query Optimization

**Experiment 2.2.2**

## Overview

This Spring Boot application demonstrates backend performance optimization techniques using Spring Data JPA, Hibernate, Spring Cache, and Ehcache. It provides author/book APIs and benchmark endpoints to compare a typical N+1 query approach, an optimized fetch approach, and cached requests.

## Objectives

- Understand the N+1 query problem in ORM-based applications.
- Compare normal entity loading with an optimized fetch strategy.
- Configure application-level caching with Ehcache.
- Measure and compare endpoint execution times.
- Use Spring Boot Actuator and JMeter for basic performance inspection.

## Technology Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA / Hibernate
- Spring Cache and Ehcache 3
- H2 in-memory database
- Spring Boot Actuator
- Apache JMeter (test plan: `jmeter/perf-test.jmx`)
- Maven

## Project Structure

```text
FSD2_EXP_2.2.2/
├── jmeter/
│   └── perf-test.jmx
├── src/
│   ├── main/java/com/exp/perf/
│   │   ├── controller/       # Author and benchmark REST endpoints
│   │   ├── entity/           # Author and Book entities
│   │   ├── repository/       # JPA repositories
│   │   ├── service/           # Business logic
│   │   └── PerfApplication.java
│   ├── main/resources/       # application.properties, data.sql, ehcache.xml
│   └── test/                 # Application tests
└── pom.xml
```

## Prerequisites

- JDK 17
- Maven 3.8 or later
- IntelliJ IDEA or VS Code (optional)

The application uses an H2 in-memory database, so a separate database server is not required.

## Run the Application

Open a terminal in this project directory and execute:

```bash
mvn clean spring-boot:run
```

Alternatively, build and run the JAR:

```bash
mvn clean package
java -jar target/perf-optimization-1.0.0.jar
```

The application starts at `http://localhost:8080`.

## Useful Endpoints

### Author and book APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/authors` | List authors |
| GET | `/authors/{id}` | Get an author by ID |
| POST | `/authors` | Create an author |
| GET | `/authors/with-books/n1` | Demonstrate loading with the N+1 pattern |
| GET | `/authors/with-books/optimized` | Load authors and books using the optimized query |
| GET | `/authors/{id}/with-books` | Get one author with books |
| GET | `/authors/search?name=...` | Search authors by name |
| GET | `/books` | List books |

See `AuthorController.java` for the complete route list and request formats.

### Benchmark APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/benchmark/n1` | Measure the N+1 approach |
| GET | `/benchmark/optimized` | Measure the optimized approach |
| GET | `/benchmark/cached` | Measure the cached approach |
| GET | `/benchmark/compare` | Compare the available approaches |

## H2 Console and Monitoring

- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:perfdb`
- Username: `sa`
- Password: leave blank
- Actuator endpoints are exposed by the current demo configuration. For example: `http://localhost:8080/actuator/health`.

## Performance Testing with JMeter

1. Start the Spring Boot application.
2. Open Apache JMeter.
3. Open `jmeter/perf-test.jmx`.
4. Review the configured HTTP requests and target URL.
5. Run the test plan and compare response times and throughput.

Results depend on the machine, test load, warm-up, and cache state. Do not treat one run as a universal performance result.

## Run Tests

```bash
mvn test
```

## Notes

- H2 is configured as an in-memory database; data is recreated when the application restarts.
- The project is an educational performance demonstration, not a production deployment template.
- Actuator exposure and verbose SQL logging should be restricted before deploying outside a local lab environment.
