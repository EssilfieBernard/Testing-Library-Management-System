package com.project.libraryfx.data_structures;

import com.project.libraryfx.DBUtils;
import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Reservation;

public class BookReservationQueue {
    private Queue<Reservation> reservationQueue = new Queue<>();  // Your custom Queue class

    // Add a reservation to the queue
    public void addReservation(Reservation reservation) {
        reservationQueue.enqueue(reservation);
    }

    // Process reservations (handle when book becomes available)
    public void processReservation() {
        if (!reservationQueue.isEmpty()) {
            Reservation reservation = reservationQueue.peek(); // Look at the next reservation
            if (checkIfBookAvailable(reservation.getBook())) {
                reserveBookForPatron(reservation);
                reservationQueue.dequeue(); // Remove the reservation after successful allocation
            }
        }
    }

    // Method to get the size of the queue (how many people are waiting)
    public int getQueueSize() {
        return reservationQueue.size();
    }

    // Check if the queue is empty
    public boolean isQueueEmpty() {
        return reservationQueue.isEmpty();
    }

    public Reservation getNextReservation() {
        return reservationQueue.isEmpty() ? null : reservationQueue.peek();
    }


    // Helper method to check if the book is available (with database interaction)
    private boolean checkIfBookAvailable(Book book) {
        return DBUtils.isBookAvailable(book.getIsbn());
    }

    // Helper method to reserve the book for the patron
    private void reserveBookForPatron(Reservation reservation) {
        if (DBUtils.reserveBook(reservation.getBook(), reservation.getPatronId())) {
            System.out.println("Book reserved for patron: " + reservation.getPatronId());
        } else {
            System.out.println("Failed to reserve book for patron: " + reservation.getPatronId());
        }
    }
}
