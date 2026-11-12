-- ============================================================
-- Seed Data for Experiment 2.2.2 – Performance Optimization
-- 10 Authors × 5 Books = 50 rows
-- IDs are auto-generated – no explicit ID inserts to avoid
-- H2 identity sequence conflicts during tests.
-- ============================================================

-- Authors (ID auto-generated)
INSERT INTO author (name, email) VALUES ('Aditya Sharma',  'aditya@example.com');
INSERT INTO author (name, email) VALUES ('Bhavna Patel',   'bhavna@example.com');
INSERT INTO author (name, email) VALUES ('Chetan Mehra',   'chetan@example.com');
INSERT INTO author (name, email) VALUES ('Deepika Joshi',  'deepika@example.com');
INSERT INTO author (name, email) VALUES ('Eshan Verma',    'eshan@example.com');
INSERT INTO author (name, email) VALUES ('Farida Khan',    'farida@example.com');
INSERT INTO author (name, email) VALUES ('Gaurav Singh',   'gaurav@example.com');
INSERT INTO author (name, email) VALUES ('Harini Nair',    'harini@example.com');
INSERT INTO author (name, email) VALUES ('Ishaan Roy',     'ishaan@example.com');
INSERT INTO author (name, email) VALUES ('Jaya Menon',     'jaya@example.com');

-- Books for Author 1 (Aditya Sharma)
INSERT INTO book (title, price, author_id) VALUES ('Spring Boot Essentials',     499.00, 1);
INSERT INTO book (title, price, author_id) VALUES ('Microservices in Practice',  599.00, 1);
INSERT INTO book (title, price, author_id) VALUES ('REST API Design',            349.00, 1);
INSERT INTO book (title, price, author_id) VALUES ('Docker for Developers',      449.00, 1);
INSERT INTO book (title, price, author_id) VALUES ('Kubernetes Basics',          699.00, 1);

-- Books for Author 2 (Bhavna Patel)
INSERT INTO book (title, price, author_id) VALUES ('Java 17 Deep Dive',          549.00, 2);
INSERT INTO book (title, price, author_id) VALUES ('Reactive Programming',       399.00, 2);
INSERT INTO book (title, price, author_id) VALUES ('Spring Security Guide',      479.00, 2);
INSERT INTO book (title, price, author_id) VALUES ('JPA Masterclass',            529.00, 2);
INSERT INTO book (title, price, author_id) VALUES ('Hibernate Internals',        449.00, 2);

-- Books for Author 3 (Chetan Mehra)
INSERT INTO book (title, price, author_id) VALUES ('Database Optimization',      599.00, 3);
INSERT INTO book (title, price, author_id) VALUES ('SQL Performance Tips',       379.00, 3);
INSERT INTO book (title, price, author_id) VALUES ('NoSQL with MongoDB',         499.00, 3);
INSERT INTO book (title, price, author_id) VALUES ('Redis Caching Strategies',   429.00, 3);
INSERT INTO book (title, price, author_id) VALUES ('Elasticsearch in Action',    649.00, 3);

-- Books for Author 4 (Deepika Joshi)
INSERT INTO book (title, price, author_id) VALUES ('Frontend with React',        399.00, 4);
INSERT INTO book (title, price, author_id) VALUES ('TypeScript Fundamentals',    349.00, 4);
INSERT INTO book (title, price, author_id) VALUES ('Angular for Enterprise',     549.00, 4);
INSERT INTO book (title, price, author_id) VALUES ('GraphQL APIs',               479.00, 4);
INSERT INTO book (title, price, author_id) VALUES ('Progressive Web Apps',       429.00, 4);

-- Books for Author 5 (Eshan Verma)
INSERT INTO book (title, price, author_id) VALUES ('DevOps Handbook',            699.00, 5);
INSERT INTO book (title, price, author_id) VALUES ('CI/CD Pipelines',            499.00, 5);
INSERT INTO book (title, price, author_id) VALUES ('Terraform Basics',           449.00, 5);
INSERT INTO book (title, price, author_id) VALUES ('Ansible Automation',         399.00, 5);
INSERT INTO book (title, price, author_id) VALUES ('Jenkins Mastery',            349.00, 5);

-- Books for Author 6 (Farida Khan)
INSERT INTO book (title, price, author_id) VALUES ('Machine Learning Basics',    799.00, 6);
INSERT INTO book (title, price, author_id) VALUES ('Python for Data Science',    599.00, 6);
INSERT INTO book (title, price, author_id) VALUES ('Deep Learning with TF',      849.00, 6);
INSERT INTO book (title, price, author_id) VALUES ('NLP with Python',            649.00, 6);
INSERT INTO book (title, price, author_id) VALUES ('Computer Vision Guide',      749.00, 6);

-- Books for Author 7 (Gaurav Singh)
INSERT INTO book (title, price, author_id) VALUES ('Cloud Architecture',         599.00, 7);
INSERT INTO book (title, price, author_id) VALUES ('AWS Solutions Architect',    699.00, 7);
INSERT INTO book (title, price, author_id) VALUES ('Azure Fundamentals',         549.00, 7);
INSERT INTO book (title, price, author_id) VALUES ('GCP Essentials',             499.00, 7);
INSERT INTO book (title, price, author_id) VALUES ('Serverless Computing',       449.00, 7);

-- Books for Author 8 (Harini Nair)
INSERT INTO book (title, price, author_id) VALUES ('System Design Primer',       749.00, 8);
INSERT INTO book (title, price, author_id) VALUES ('Design Patterns in Java',    549.00, 8);
INSERT INTO book (title, price, author_id) VALUES ('Clean Code',                 399.00, 8);
INSERT INTO book (title, price, author_id) VALUES ('Refactoring',                449.00, 8);
INSERT INTO book (title, price, author_id) VALUES ('Domain-Driven Design',       649.00, 8);

-- Books for Author 9 (Ishaan Roy)
INSERT INTO book (title, price, author_id) VALUES ('Cybersecurity Essentials',   599.00, 9);
INSERT INTO book (title, price, author_id) VALUES ('Ethical Hacking',            699.00, 9);
INSERT INTO book (title, price, author_id) VALUES ('Network Security',           549.00, 9);
INSERT INTO book (title, price, author_id) VALUES ('Penetration Testing',        649.00, 9);
INSERT INTO book (title, price, author_id) VALUES ('OWASP Top 10',              449.00, 9);

-- Books for Author 10 (Jaya Menon)
INSERT INTO book (title, price, author_id) VALUES ('Agile Methodology',          349.00, 10);
INSERT INTO book (title, price, author_id) VALUES ('Scrum Master Guide',         399.00, 10);
INSERT INTO book (title, price, author_id) VALUES ('Product Management',         499.00, 10);
INSERT INTO book (title, price, author_id) VALUES ('Kanban in Practice',         349.00, 10);
INSERT INTO book (title, price, author_id) VALUES ('Lean Startup',               299.00, 10);
