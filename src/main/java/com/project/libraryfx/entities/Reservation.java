package com.project.libraryfx.entities;

public class Reservation {
    private Book book;
    private String patronId;
    private String status;
    private String reservationDate;
    private String expirationDate;

    public Reservation(Book book, String patronId, String status, String reservationDate, String expirationDate) {
        this.book = book;
        this.patronId = patronId;
        this.status = status;
        this.reservationDate = reservationDate;
        this.expirationDate = expirationDate;
    }

    public Reservation(String patronId, Book book) {
        this.patronId = patronId;
        this.book = book;
    }

    // Getters for Reservation properties
    public Book getBook() {
        return book;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getStatus() {
        return status;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getBookTitle() {
        return book != null ? book.getTitle() : "Unknown";
    }

    public String getBookIsbn() {
        return book != null ? book.getIsbn() : "Unknown";
    }


}