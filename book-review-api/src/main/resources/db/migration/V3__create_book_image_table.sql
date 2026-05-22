CREATE TABLE book_image (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id BIGINT NOT NULL,
    s3_key VARCHAR(255) NOT NULL UNIQUE,

    CONSTRAINT fk_book_image_book_id
        FOREIGN KEY (book_id)
        REFERENCES book(id)
);