package jp.systemsengineeya.bookreview.api.dto;

public class Review {
    private Long id;
    private Long bookId;
    private String content;
    private int rating;

    // Constructors, getters, setters
    public Review() {}

    public Review(Long id, Long bookId, String content, int rating) {
        this.id = id;
        this.bookId = bookId;
        this.content = content;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}