package com.project.libraryfx;

import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Patron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testDefaultConstructor() {
        Book book = new Book();

        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getIsbn());
        assertNull(book.getStatus());
        assertNull(book.getAssignedTo());
    }

    @Test
    public void testConstructorWithTitleAuthorIsbnStatus() {
        Book book = new Book("Test Title", "Test Author", "1234567890", "CHECKED_OUT");

        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals("CHECKED_OUT", book.getStatus());
        assertNull(book.getAssignedTo());
    }

    @Test
    public void testConstructorWithTitleAuthorIsbn() {
        Book book = new Book("Test Title", "Test Author", "1234567890");

        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals("AVAILABLE", book.getStatus());
        assertNull(book.getAssignedTo());
    }

    @Test
    public void testConstructorWithAllParameters() {
        Patron patron = new Patron("John Doe", "12345");
        Book book = new Book("Test Title", "Test Author", "1234567890", "RESERVED", patron);

        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals("RESERVED", book.getStatus());
        assertEquals(patron, book.getAssignedTo());
    }

    @Test
    public void testConstructorWithIsbnOnly() {
        Book book = new Book("1234567890");

        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertNull(book.getStatus());
        assertNull(book.getAssignedTo());
    }

    @Test
    public void testSettersAndGetters() {
        Book book = new Book();

        Patron patron = new Patron("Jane Doe", "54321");

        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setIsbn("0987654321");
        book.setStatus("LOST");
        book.setAssignedTo(patron);

        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("0987654321", book.getIsbn());
        assertEquals("LOST", book.getStatus());
        assertEquals(patron, book.getAssignedTo());
    }

}
