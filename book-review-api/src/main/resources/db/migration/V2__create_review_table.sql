-- reviewテーブルの作成
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INTEGER NOT NULL,
    CONSTRAINT fk_review_book
        FOREIGN KEY (book_id)
        REFERENCES book (id)
        ON DELETE CASCADE
);