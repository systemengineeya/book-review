# Book Review API

## 概要
書籍とレビューを管理するREST APIです。  
Spring Bootで実装し、基本的なCRUD操作を提供します。

---

## 技術スタック
- Java 17（言語）
- Spring Boot（フレームワーク）
- Maven（ビルドツール）
- RESTful API（設計）

---

## 機能

### Book
- 書籍の作成 / 取得 / 更新 / 削除

### Review
- 書籍に対するレビューの作成 / 取得 / 更新 / 削除

---

## エンドポイント

### Book
- POST /books
- GET /books
- GET /books/{bookId}
- PATCH /books/{bookId}
- DELETE /books/{bookId}

### Review
- POST /books/{bookId}/reviews
- GET /books/{bookId}/reviews
- GET /books/{bookId}/reviews/{reviewId}
- PATCH /books/{bookId}/reviews/{reviewId}
- DELETE /books/{bookId}/reviews/{reviewId}

---

## 環境構築

### 必要条件
- Java 17

### 起動方法
```bash
./mvnw spring-boot:run
```

### ビルド
```bash
./mvnw clean package
```

### ビルドしたjarの実行
```
java -jar target/book-review-api-0.0.1-SNAPSHOT.jar
```