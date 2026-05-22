INSERT INTO book (id, title, author)
VALUES
    (1, 'Spring Boot実践入門', '山田太郎'),
    (2, 'Docker & LocalStackハンズオン', '鈴木花子')
ON CONFLICT (id) DO NOTHING;

INSERT INTO book_image (book_id, s3_key)
VALUES
    (
        1,
        '66732f30-4952-4c2b-90a7-401ede7076bc.png'
    ),
    (
        2,
        '3bf5b4e7-8aac-4bae-8dbc-0b6ac766d091.png'
    ),
    (
        2,
        '342c2891-fcb2-4c5e-aeaa-e8bd41bf3e17.png'
    )
ON CONFLICT (s3_key) DO NOTHING;