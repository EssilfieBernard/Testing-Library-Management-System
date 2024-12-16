package com.project.libraryfx;

import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Reservation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {

    @Test
    public void testConstructorWithAllParameters() {
        Book book = new Book("Test Title", "Test Author", "1234567890");
        Reservation reservation = new Reservation(book, "patron1", "ACTIVE", "2023-12-01", "2023-12-10");

        assertEquals(book, reservation.getBook());
        assertEquals("patron1", reservation.getPatronId());
        assertEquals("ACTIVE", reservation.getStatus());
        assertEquals("2023-12-01", reservation.getReservationDate());
        assertEquals("2023-12-10", reservation.getExpirationDate());
    }

    @Test
    public void testConstructorWithPatronAndBook() {
        Book book = new Book("Test Title", "Test Author", "1234567890");
        Reservation reservation = new Reservation("patron1", book);

        assertEquals(book, reservation.getBook());
        assertEquals("patron1", reservation.getPatronId());
        assertNull(reservation.getStatus()); // Assuming status is not set in this constructor
        assertNull(reservation.getReservationDate()); // Assuming date fields are not set
        assertNull(reservation.getExpirationDate());
    }

    @Test
    public void testGetBookTitle() {
        Book book = new Book("Test Title", "Test Author", "1234567890");
        Reservation reservation = new Reservation("patron1", book);

        assertEquals("Test Title", reservation.getBookTitle());

        // Test with null book
        Reservation nullBookReservation = new Reservation("patron1", null);
        assertEquals("Unknown", nullBookReservation.getBookTitle());
    }

    @Test
    public void testGetBookIsbn() {
        Book book = new Book("Test Title", "Test Author", "1234567890");
        Reservation reservation = new Reservation("patron1", book);

        assertEquals("1234567890", reservation.getBookIsbn());

        // Test with null book
        Reservation nullBookReservation = new Reservation("patron1", null);
        assertEquals("Unknown", nullBookReservation.getBookIsbn());
    }
}

