package com.project.libraryfx;

import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Patron;
import com.project.libraryfx.entities.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private Patron patron;
    private Book book;

    @BeforeEach
    public void setUp() {
        patron = new Patron("John Doe", "12345");
        book = new Book("1234567890", "Test Book", "Test Author");
    }

    @Test
    public void testDefaultConstructor() {
        Transaction transaction = new Transaction();

        assertNull(transaction.getReference());
        assertNull(transaction.getCreatedBy());
        assertNull(transaction.getBook());
        assertNull(transaction.getStatus());
        assertEquals(0.00, transaction.getFineAmount());
        assertNull(transaction.getCreatedAt());
        assertNull(transaction.getDueDate());
        assertNull(transaction.getReturnDate());
    }

    @Test
    public void testParameterizedConstructorWithPatronAndBook() {
        Transaction transaction = new Transaction(patron, book);

        assertEquals(patron, transaction.getCreatedBy());
        assertEquals(book, transaction.getBook());
        assertEquals("ACTIVE", transaction.getStatus());
        assertEquals(0.00, transaction.getFineAmount());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getDueDate());
        assertEquals(transaction.getCreatedAt().plusDays(5), transaction.getDueDate());
        assertNull(transaction.getReturnDate());
    }

    @Test
    public void testParameterizedConstructorWithDetails() {
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Transaction transaction = new Transaction("ref123", book, "COMPLETED", 10.00, dueDate);

        assertEquals("ref123", transaction.getReference());
        assertEquals(book, transaction.getBook());
        assertEquals("COMPLETED", transaction.getStatus());
        assertEquals(10.00, transaction.getFineAmount(), 0.01);
        assertEquals(dueDate, transaction.getDueDate());
    }

    @Test
    public void testSettersAndGetters() {
        Transaction transaction = new Transaction();

        transaction.setReference("ref123");
        transaction.setCreatedBy(patron);
        transaction.setBook(book);
        transaction.setStatus("OVERDUE");
        transaction.setFineAmount(15.00);

        LocalDate createdAt = LocalDate.now();
        LocalDate returnDate = createdAt.plusDays(10);

        transaction.setCreatedAt(createdAt);
        transaction.setDueDate(createdAt.plusDays(5));
        transaction.setReturnDate(returnDate);

        assertEquals("ref123", transaction.getReference());
        assertEquals(patron, transaction.getCreatedBy());
        assertEquals(book, transaction.getBook());
        assertEquals("OVERDUE", transaction.getStatus());
        assertEquals(15.00, transaction.getFineAmount(), 0.01);
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(createdAt.plusDays(5), transaction.getDueDate());
        assertEquals(returnDate, transaction.getReturnDate());
    }
}
