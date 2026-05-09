CREATE TABLE book_image (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id BIGINT NOT NULL,
    extension VARCHAR(10) NOT NULL,

    CONSTRAINT fk_book_image_book_id
        FOREIGN KEY (book_id)
        REFERENCES book(id)
);