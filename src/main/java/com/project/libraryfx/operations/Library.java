package com.project.libraryfx.operations;

import com.project.libraryfx.entities.Book;
import com.project.libraryfx.LinkedList;

public class Library {
    private final LinkedList<Book> books;

    public Library() {
        books = new LinkedList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public LinkedList<Book> getBooks() {
        return books;
    }

    public Book findBookByISBN(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null; // Book not found
    }
}

