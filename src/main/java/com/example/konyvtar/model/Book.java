package com.example.konyvtar.model;

public class Book {

    private String title;
    private String author;
    private String isbn;
    private int year;
    private String genre;
    private String available;
    private String serial;


    public Book(String title, String author, String serial, String isbn, int year, String genre, String available) {
        setTitle(title);
        setAuthor(author);
        setSerial(serial);
        setIsbn(isbn);
        setYear(year);
        setGenre(genre);
        setAvailable(available);
    }
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

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

    public String isAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
