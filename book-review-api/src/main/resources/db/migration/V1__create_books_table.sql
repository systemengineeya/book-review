-- V1__create_books_table.sql
-- Create books table for book review application

CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

-- Create index on title for better query performance
CREATE INDEX idx_books_title ON books (title);