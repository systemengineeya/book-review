-- V1__create_book_table.sql
-- Create book table for book review application

CREATE TABLE book (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

-- Create index on title for better query performance
CREATE INDEX idx_book_title ON book (title);