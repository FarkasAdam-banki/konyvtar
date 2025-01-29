package com.example.konyvtar;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private int year;
    private String genre;
    private boolean available;

    public Book(String title, String author, String isbn, int year, String genre, boolean available) {
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setYear(year);
        setGenre(genre);
        setAvailable(available);
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
