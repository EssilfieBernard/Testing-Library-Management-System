package com.project.libraryfx;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.libraryfx.controller.DashboardController;
import com.project.libraryfx.data_structures.BookReservationQueue;
import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Patron;
import com.project.libraryfx.entities.Reservation;
import com.project.libraryfx.entities.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
    private static Map<String, BookReservationQueue> bookReservationQueues = new HashMap<>();


    private final static Connection connection = DatabaseConnection.getConnection();


    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String welcomeText) {
        Parent root = null;


        if (username != null && welcomeText != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.setUserInformation(username, welcomeText);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

    }

    public static void createPatron(ActionEvent event, String username, String password, String welcomeText) {
        String query = "INSERT INTO patrons (memberid, username, password, welcometext) VALUES (?, ?, ?, ?)";
        String memberId = checkMemberId(generateMemberId());

        try {
            if (usernameExists(username)) {
                System.out.println("Username exists.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username already exists");
                alert.show();
                return;  // Exit the method if the username exists
            }

            var statement = connection.prepareStatement(query);
            statement.setString(1, memberId);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setString(4, welcomeText);

            var rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Patron added successfully");

                // If patron is created successfully, show a success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("Account created successfully! Redirecting to login page. Your member id is " + memberId);
                successAlert.showAndWait();


                changeScene(event, "dashboard.fxml", "Welcome!", username, welcomeText);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loginUser(ActionEvent event, String username, String password) {
        String query = "select password, welcometext from patrons where username = ?";

        try {
            var statement = connection.prepareStatement(query);
            statement.setString(1, username);
            var resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("user not found in database");
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid username or password");
                alert.show();
            }
            else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedWelcomeText = resultSet.getString("welcometext");
                    if (retrievedPassword.equals(password))
                        changeScene(event, "dashboard.fxml", "Welcome", username, retrievedWelcomeText);
                    else {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Invalid username or password");
                        alert.show();
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static LinkedList<Book> getBorrowedBooks(String patronId) {
        String query = "SELECT title, author, isbn, status FROM books WHERE assignedto = ?";
        LinkedList<Book> books = new LinkedList<>();

        try {
            var statement = connection.prepareStatement(query);
            statement.setString(1, patronId);

            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                String status = resultSet.getString("status");

                Book book = new Book(title, author, isbn, status);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static String getPatronIdByUsername(String loggedInUsername) {
        var query = "select memberid from patrons where username = ?";

        try {
            var statement = connection.prepareStatement(query);
            statement.setString(1, loggedInUsername);

            var resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getString("memberid");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT title, author, isbn, status FROM books;";

        try (PreparedStatement psGetBooks = connection.prepareStatement(query);
             ResultSet resultSet = psGetBooks.executeQuery()) {
            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    public static void addBook(String title, String author, String isbn) {
        String query = "INSERT INTO books (title, author, isbn, status) VALUES (?, ?, ?, ?)";

        try {
            // Create the prepared statement
            var statement = connection.prepareStatement(query);

            // Set the parameters
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, isbn);
            statement.setString(4, "AVAILABLE"); // Assuming default status is "Available"

            // Execute the statement
            int rowsAffected = statement.executeUpdate();

            // Check the result
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Book added successfully");
                alert.show();
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error adding book: " + e.getMessage());
            alert.show();
            throw new RuntimeException(e);
        }
    }



    public static boolean borrowBook(Book selectedBook, String patronId) {
        String checkAvailabilityQuery = "SELECT status FROM books WHERE isbn = ?";
        String updateBookQuery = "UPDATE books SET status = 'BORROWED', assignedto = ? WHERE isbn = ?";
        String insertTransactionQuery = "INSERT INTO transactions (reference, createdby, bookisbn, status, createdat, duedate) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Debug: Print ISBN
            System.out.println("Borrowing book with ISBN: " + selectedBook.getIsbn());

            // Begin transaction
            connection.setAutoCommit(false);

            // Check if the book is available
            try (PreparedStatement checkStatement = connection.prepareStatement(checkAvailabilityQuery)) {
                checkStatement.setString(1, selectedBook.getIsbn());
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    String status = resultSet.getString("status");

                    System.out.println("Book status: " + status);

                    // If the book is already borrowed, return false
                    if (!status.equalsIgnoreCase("Available")) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Book is not available for borrowing.");
                        alert.show();
                        return false;
                    }
                } else {
                    System.out.println("Book not found in the database.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Book not found in the database.");
                    alert.show();
                    return false;
                }
            }

            // Update the book's status and assign it to the patron
            try (PreparedStatement updateStatement = connection.prepareStatement(updateBookQuery)) {
                updateStatement.setString(1, patronId);
                updateStatement.setString(2, selectedBook.getIsbn());
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Create a new transaction for the borrow action
                    try (PreparedStatement transactionStatement = connection.prepareStatement(insertTransactionQuery)) {
                        String transactionReference = generateUniqueReference(); // Implement this method
                        transactionStatement.setString(1, transactionReference);
                        transactionStatement.setString(2, patronId);
                        transactionStatement.setString(3, selectedBook.getIsbn());
                        transactionStatement.setString(4, "ACTIVE");
                        transactionStatement.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                        transactionStatement.setDate(6, Date.valueOf(LocalDate.now().plusDays(5)));

                        int rowsInserted = transactionStatement.executeUpdate();

                        if (rowsInserted > 0) {
                            // Commit the transaction
                            connection.commit();

                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setContentText("Book borrowed successfully and transaction created!");
                            successAlert.show();
                            return true;
                        } else {
                            System.out.println("Failed to create transaction.");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Failed to create transaction for the borrow action.");
                            alert.show();
                        }
                    }
                } else {
                    System.out.println("Failed to update book status.");
                }
            }

            // Rollback in case of failure
            connection.rollback();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Error borrowing book: " + e.getMessage());
            errorAlert.show();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }


    private static String generateUniqueReference() {
        return java.util.UUID.randomUUID().toString();
    }

    public static List<Transaction> getTransactionsByUser(String patronId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.reference, t.status, t.fineAmount, t.createdAt, t.dueDate, t.returnDate, " +
                "b.title, b.isbn, b.status AS book_status " + // Added isbn and book status
                "FROM transactions t " +
                "JOIN books b ON t.bookisbn = b.isbn " +
                "WHERE t.createdBy = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, patronId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String reference = resultSet.getString("reference");
                String status = resultSet.getString("status");
                double fineAmount = resultSet.getDouble("fineAmount");
                LocalDate createdAt = resultSet.getDate("createdAt").toLocalDate();
                LocalDate dueDate = resultSet.getDate("dueDate") != null ?
                        resultSet.getDate("dueDate").toLocalDate() : null;
                LocalDate returnDate = resultSet.getDate("returnDate") != null ?
                        resultSet.getDate("returnDate").toLocalDate() : null;

                // Get book details
                String bookTitle = resultSet.getString("title");
                String isbn = resultSet.getString("isbn");
                String bookStatus = resultSet.getString("book_status");

                // Create Book with proper ISBN and status
                Book book = new Book(bookTitle,  "", isbn, bookStatus);
                Transaction transaction = new Transaction(reference, book, status, fineAmount, dueDate);

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }



    public static void updateTransactionStatus(Transaction transaction) {
        boolean success = false;
        String query = "UPDATE transactions SET status = ?, returnDate = ? WHERE reference = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "RETURNED");  // Set the status to 'RETURNED'
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, transaction.getReference());  // Set the transaction reference

            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0; // Check if any row was updated
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void updateBookStatus(Book book) {
        String query = "UPDATE books SET status = ? WHERE isbn = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "AVAILABLE");   // Set the new status to 'AVAILABLE'
            ps.setString(2, book.getIsbn());  // Set the ISBN of the book being updated

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) // Check if any row was updated
                System.out.println("Book status changed");
            System.out.println("Book status could not be changed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean reserveBook(Book selectedBook, String patronId) {
        String checkAvailabilityQuery = "SELECT status FROM books WHERE isbn = ?";
        String updateBookQuery = "UPDATE books SET status = 'RESERVED', assignedto = ? WHERE isbn = ?";
        String insertReservationQuery = "INSERT INTO reservations (book_id, patron_id, status, reserved_on, expiration_date) VALUES (?, ?, ?, ?, ?)";

        try {
            // Begin transaction
            connection.setAutoCommit(false);

            // Check if the book is available
            try (PreparedStatement checkStatement = connection.prepareStatement(checkAvailabilityQuery)) {
                checkStatement.setString(1, selectedBook.getIsbn());
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    String status = resultSet.getString("status");

                    // If book is borrowed, add to reservation queue
                    if (status.equalsIgnoreCase("BORROWED")) {
                        addToReservationQueue(selectedBook, patronId);  // Add patron to queue
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book is borrowed, you've been added to the reservation queue.");
                        alert.show();
                        return false;
                    } else if (status.equalsIgnoreCase("AVAILABLE")) {
                        // Book is available, proceed with reservation
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateBookQuery)) {
                            updateStatement.setString(1, patronId);  // Assign reserved by patron ID
                            updateStatement.setString(2, selectedBook.getIsbn());
                            int rowsUpdated = updateStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                // Create a new reservation
                                try (PreparedStatement reservationStatement = connection.prepareStatement(insertReservationQuery)) {
                                    reservationStatement.setString(1, selectedBook.getIsbn());
                                    reservationStatement.setString(2, patronId);
                                    reservationStatement.setString(3, "RESERVED");
                                    reservationStatement.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                                    reservationStatement.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // Reservation duration of 7 days

                                    int rowsInserted = reservationStatement.executeUpdate();

                                    if (rowsInserted > 0) {
                                        connection.commit();  // Commit transaction
                                        return true;
                                    } else {
                                        System.out.println("Failed to create reservation.");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Book not found in the database.");
                    alert.show();
                }
            }

            connection.rollback();  // Rollback if something fails
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Error reserving book: " + e.getMessage());
            errorAlert.show();
        } finally {
            try {
                connection.setAutoCommit(true);  // Reset auto commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }


    public static boolean isBookAvailable(String isbn) {
        String query = "SELECT status FROM books WHERE isbn = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return "AVAILABLE".equalsIgnoreCase(resultSet.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Reservation> getReservedBooks(String patronId) {
        List<Reservation> reservedBooks = new ArrayList<>();
        String sql = "SELECT b.title, b.isbn, b.author, r.status, r.reserved_on, r.expiration_date " +
                "FROM reservations r " +
                "JOIN books b ON r.book_id = b.isbn " +
                "WHERE r.patron_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patronId); // Set the patronId parameter
            ResultSet rs = statement.executeQuery();

            // Iterate through the result set and populate the list of reserved books
            while (rs.next()) {
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                String author = rs.getString("author");
                String status = rs.getString("status");
                Date reservedOn = rs.getDate("reserved_on");
                Date expirationDate = rs.getDate("expiration_date");

                Book book = new Book(title, author, isbn, status);
                Reservation reservation = new Reservation(book, patronId, status, reservedOn.toString(), expirationDate.toString());
                reservedBooks.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservedBooks;
    }


    public static boolean cancelReservation(Reservation reservation) {
        String deleteReservationQuery = "DELETE FROM reservations WHERE book_id = ? AND patron_id = ?";
        String updateBookQuery = "UPDATE books SET status = 'AVAILABLE', assignedto = NULL WHERE isbn = ?";

        try {
            // Begin transaction
            connection.setAutoCommit(false);

            // Delete the reservation
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteReservationQuery)) {
                deleteStatement.setString(1, reservation.getBook().getIsbn());
                deleteStatement.setString(2, reservation.getPatronId());
                int rowsDeleted = deleteStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    // Check if there are other reservations for the book
                    BookReservationQueue queue = bookReservationQueues.get(reservation.getBook().getIsbn());
                    if (queue == null || queue.isQueueEmpty()) {
                        // No other reservations, update the book's status to AVAILABLE
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateBookQuery)) {
                            updateStatement.setString(1, reservation.getBook().getIsbn());
                            updateStatement.executeUpdate();
                        }
                    } else {
                        // Process the next reservation in the queue
                        queue.processReservation();
                    }

                    // Commit the transaction
                    connection.commit();
                    return true;
                }
            }

            // Rollback in case of failure
            connection.rollback();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Error canceling reservation: " + e.getMessage());
            errorAlert.show();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }



    public static BookReservationQueue getReservationQueue(String bookIsbn) {
        BookReservationQueue queue = new BookReservationQueue();
        String query = "SELECT patron_id FROM reservations WHERE book_id = ? AND status = 'QUEUED'";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, bookIsbn);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String patronId = rs.getString("patron_id");
                queue.addReservation(new Reservation(patronId, new Book(bookIsbn)));  // Add patron to the queue
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return queue;
    }


    public static void updateReservationStatusToAssigned(String bookIsbn, String patronId) {
        String query = "UPDATE reservations SET status = 'ASSIGNED' WHERE book_id = ? AND patron_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, bookIsbn);
            ps.setString(2, patronId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reservation status updated to 'ASSIGNED'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Set the new scene for login page
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login Page");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error loading login page.");
            alert.show();
        }
    }

    private static Patron getPatronById(String memberId) throws SQLException{
        String query = "select * from patrons where memberid = ?";

        var statement = connection.prepareStatement(query);
        statement.setString(1, memberId);
        var resultSet = statement.executeQuery();

        if (!resultSet.next())
            return null;

        return new Patron(resultSet.getString("memberid"), resultSet.getString("username"));

    }

    public static void addToReservationQueue(Book book, String patronId) {
        // Add reservation to the database
        String insertReservationQuery = "INSERT INTO reservations (book_id, patron_id, status, reserved_on, expiration_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertReservationQuery)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, patronId);
            stmt.setString(3, "QUEUED"); // Mark as queued
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // Reservation duration of 7 days
            stmt.executeUpdate();

            // Add the reservation to the in-memory queue
            Reservation newReservation = new Reservation(book, patronId, "QUEUED", LocalDate.now().toString(), LocalDate.now().plusDays(7).toString());
            BookReservationQueue queue = getReservationQueue(book.getIsbn()); // Fetch or create queue
            queue.addReservation(newReservation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void saveReservationQueuesToFile() {
        try (FileWriter file = new FileWriter("reservation_queues.json")) {
            Gson gson = new Gson();
            gson.toJson(bookReservationQueues, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadReservationQueuesFromFile() {
        try (FileReader reader = new FileReader("reservation_queues.json")) {
            Gson gson = new Gson();
            bookReservationQueues = gson.fromJson(reader, new TypeToken<Map<String, BookReservationQueue>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static String generateMemberId() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 5;
        StringBuilder id = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++)
            id.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));

        return id.toString();
    }

    private static boolean usernameExists(String username) {
        String query = "select count(*) from patrons where username = ?";

        try {
            var statement = connection.prepareStatement(query);
            statement.setString(1, username);

            var resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static boolean memberIdExists(String id) {
        String query = "select count(*) from patrons where memberid = ?";

        try {
            var statement = connection.prepareStatement(query);
            statement.setString(1, id);

            var resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static String checkMemberId(String id) {
        if (!memberIdExists(id))
            return id;

        return checkMemberId(generateMemberId());

    }




}
