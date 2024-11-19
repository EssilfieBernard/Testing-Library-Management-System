package com.project.libraryfx.entities;


public class Book {
    private String title;
    private String author;
    private String isbn;
    private String status;
    private Patron assignedTo = null;

    public Book () {}

    public Book(String title, String author, String isbn, String status) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
    }
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = "AVAILABLE";
        this.assignedTo = null;
    }

    public Book(String title, String author, String isbn, String status, Patron assignedTo) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    public Book(String bookIsbn) {
        this.isbn = bookIsbn;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Patron getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Patron assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", status=" + status +
                ", assignedTo=" + assignedTo +
                '}';
    }
}
