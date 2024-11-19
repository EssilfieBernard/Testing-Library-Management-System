package com.project.libraryfx.entities;

import java.time.LocalDate;

public class Transaction {
    private String reference;
    private Patron createdBy;
    private String status;
    private double fineAmount;
    private LocalDate createdAt;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Book book;

    public Transaction() {}



    public Transaction(Patron createdBy, Book book) {
        this.createdBy = createdBy;
        this.book = book;
        this.createdAt = LocalDate.now();
        this.dueDate = createdAt.plusDays(5);
        this.status = "ACTIVE";
        this.fineAmount = 0.00;
        this.returnDate = null;
    }

    public Transaction(String reference, Book book, String status, double fineAmount, LocalDate dueDate) {
        this.reference = reference;
        this.book = book;
        this.status = status;
        this.fineAmount = fineAmount;
        this.dueDate = dueDate;
    }

    // Getters and Setters


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Patron getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Patron createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

