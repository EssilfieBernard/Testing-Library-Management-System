package com.project.libraryfx;

import com.project.libraryfx.data_structures.BookReservationQueue;
import com.project.libraryfx.entities.Book;
import com.project.libraryfx.entities.Reservation;
import com.project.libraryfx.entities.Transaction;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



class DBUtilsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMock(this);
    }

    @Test
    void testLoginUserSuccess() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate user found with matching password
        when(mockResultSet.isBeforeFirst()).thenReturn(true); // User exists in database
        when(mockResultSet.next()).thenReturn(true); // Simulate one row in result set
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("welcometext")).thenReturn("Welcome back!");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            Optional<String> result = DBUtils.loginUser("testUser", "password123");

            // Verify interactions
            verify(mockStatement).setString(1, "testUser");
            verify(mockStatement).executeQuery();

            // Assert result
            assertTrue(result.isPresent());
            assertEquals("Welcome back!", result.get());
        }
    }

    @Test
    void testLoginUserNotFound() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate user not found in database
        when(mockResultSet.isBeforeFirst()).thenReturn(false); // No user found

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            Optional<String> result = DBUtils.loginUser("nonExistentUser", "password123");

            // Verify interactions
            verify(mockStatement).setString(1, "nonExistentUser");
            verify(mockStatement).executeQuery();

            // Assert result
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testCreatePatronSuccess() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock other static methods
            try (MockedStatic<DBUtils> utilsMock = Mockito.mockStatic(DBUtils.class,
                    Mockito.CALLS_REAL_METHODS)) {

                // Stub specific method behaviors
                utilsMock.when(() -> DBUtils.usernameExists("testUser")).thenReturn(false);
                utilsMock.when(() -> DBUtils.memberIdExists("M12345")).thenReturn(false);
                utilsMock.when(DBUtils::generateMemberId).thenReturn("M12345");

                // Configure mock connection and statement
                when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
                when(mockStatement.executeUpdate()).thenReturn(1);

                // Call the method
                String memberId = DBUtils.createPatron("testUser", "password123", "Welcome!");

                // Verify interactions
                verify(mockConnection).prepareStatement(
                        "INSERT INTO patrons (memberid, username, password, welcometext) VALUES (?, ?, ?, ?)"
                );

                // Use ArgumentCaptor to capture and verify setString calls
                ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
                verify(mockStatement, times(4)).setString(anyInt(), stringCaptor.capture());

                // Additional verifications
                assertEquals("M12345", memberId);
            }
        }
    }

    @Test
    void testUsernameExists() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Scenario 1: Username exists
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(1);

            boolean existingUsername = DBUtils.usernameExists("existingUser");
            assertTrue(existingUsername, "Username should exist");

            // Verify method interactions for existing username
            verify(mockConnection).prepareStatement("SELECT COUNT(*) FROM patrons WHERE username = ?");
            verify(mockStatement).setString(1, "existingUser");

            // Scenario 2: Username does not exist
            reset(mockStatement, mockResultSet);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(0);

            boolean nonExistingUsername = DBUtils.usernameExists("newUser");
            assertFalse(nonExistingUsername, "Username should not exist");

            // Scenario 3: Empty or null username
            assertFalse(DBUtils.usernameExists(""), "Empty username should return false");
            assertFalse(DBUtils.usernameExists(null), "Null username should return false");
        }
    }

    @Test
    void testUsernameExistsSQLException() {
        // Scenario 4: SQL Exception handling
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Simulate SQL Exception
            try {
                when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

                assertThrows(RuntimeException.class, () -> {
                    DBUtils.usernameExists("testUser");
                }, "Should throw RuntimeException on SQL error");
            } catch (SQLException e) {
                fail("Unexpected SQLException during test setup");
            }
        }
    }

    @Test
    void testReserveBookSuccess() throws SQLException {
        // Mock Connection, PreparedStatements, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockCheckStatement = mock(PreparedStatement.class);
        PreparedStatement mockUpdateStatement = mock(PreparedStatement.class);
        PreparedStatement mockReservationStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Create a test book
        Book testBook = new Book("Test Book", "Test Author", "BOOKISBN892321", "AVAILABLE");

        // Mock database interactions
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Disable auto-commit
            when(mockConnection.getAutoCommit()).thenReturn(true);

            // Mock check availability query
            when(mockConnection.prepareStatement("SELECT status FROM books WHERE isbn = ?"))
                    .thenReturn(mockCheckStatement);
            when(mockCheckStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("status")).thenReturn("AVAILABLE");

            // Mock update book status query
            when(mockConnection.prepareStatement("UPDATE books SET status = 'RESERVED', assignedto = ? WHERE isbn = ?"))
                    .thenReturn(mockUpdateStatement);
            when(mockUpdateStatement.executeUpdate()).thenReturn(1);

            // Mock insert reservation query
            when(mockConnection.prepareStatement("INSERT INTO reservations (book_id, patron_id, status, reserved_on, expiration_date) VALUES (?, ?, ?, ?, ?)"))
                    .thenReturn(mockReservationStatement);
            when(mockReservationStatement.executeUpdate()).thenReturn(1);

            // Execute the method
            boolean result = DBUtils.reserveBook(testBook, "MPATRON02937847");

            // Verify interactions
            verify(mockCheckStatement).setString(1, "BOOKISBN892321");
            verify(mockUpdateStatement).setString(1, "MPATRON02937847");
            verify(mockUpdateStatement).setString(2, "BOOKISBN892321");
            verify(mockReservationStatement).setString(1, "BOOKISBN892321");
            verify(mockReservationStatement).setString(2, "MPATRON02937847");
            verify(mockReservationStatement).setString(3, "RESERVED");

            // Assert result
            assertTrue(result, "Book reservation should be successful");
        }
    }


    @Test
    void testAddToReservationQueueSQLException() throws SQLException {
        // Mock Connection
        Connection mockConnection = mock(Connection.class);

        // Create test book and patron
        Book testBook = new Book("Test Book", "Test Author", "BOOKISBN892321", "AVAILABLE");
        String testPatronId = "MPATRON02937847";

        // Mock DatabaseConnection to return mock connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Configure to throw SQLException
            when(mockConnection.prepareStatement(
                    "INSERT INTO reservations (book_id, patron_id, status, reserved_on, expiration_date) VALUES (?, ?, ?, ?, ?)"
            )).thenThrow(new SQLException("Database connection error"));

            // Execute the method and verify exception handling
            DBUtils.addToReservationQueue(testBook, testPatronId);

            // Verify that exception is caught and printed
            // Note: This is a weak verification due to e.printStackTrace()
        }
    }


    @Test
    void testReserveBookSQLException() throws SQLException {
        // Mock Connection
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockCheckStatement = mock(PreparedStatement.class);

        // Create a test book
        Book testBook = new Book("Test Book", "Test Author", "BOOKISBN892321", "AVAILABLE");

        // Mock database interactions
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Simulate SQL Exception
            when(mockConnection.prepareStatement("SELECT status FROM books WHERE isbn = ?"))
                    .thenThrow(new SQLException("Database connection error"));

            // Assert that a RuntimeException is thrown
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                DBUtils.reserveBook(testBook, "MPATRON02937847");
            }, "Should throw RuntimeException on SQL error");

            // Verify exception message
            assertTrue(exception.getMessage().contains("Error reserving book"));
        }
    }


    @Test
    void testGetReservationQueueNoReservations() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Mock book ISBN
            String testBookIsbn = "1234567890";

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Prepare the mock statement and result set
            when(mockConnection.prepareStatement(
                    "SELECT patron_id FROM reservations WHERE book_id = ? AND status = 'QUEUED'"
            )).thenReturn(mockStatement);

            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            // Scenario: No reservations
            when(mockResultSet.next()).thenReturn(false);

            // Execute the method
            BookReservationQueue resultQueue = DBUtils.getReservationQueue(testBookIsbn);

            // Assertions
            assertNotNull(resultQueue, "Reservation queue should not be null");
            assertTrue(resultQueue.isQueueEmpty(), "Queue should be empty");
            assertEquals(0, resultQueue.getQueueSize(), "Queue size should be 0");
        }
    }

    @Test
    void testGetReservationQueueSQLException() {
        // Scenario: SQL Exception handling
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Simulate SQL Exception
            try {
                when(mockConnection.prepareStatement(anyString()))
                        .thenThrow(new SQLException("Database connection error"));

                // Execute and expect no exception (current implementation just prints stack trace)
                BookReservationQueue resultQueue = DBUtils.getReservationQueue("1234567890");

                // Verify empty queue is returned
                assertNotNull(resultQueue, "Queue should be created even on SQL error");
                assertTrue(resultQueue.isQueueEmpty(), "Queue should be empty on SQL error");
            } catch (SQLException e) {
                fail("Unexpected SQLException during test setup");
            }
        }
    }

    @Test
    void testBorrowBookSuccess() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockCheckStatement = mock(PreparedStatement.class);
        PreparedStatement mockUpdateStatement = mock(PreparedStatement.class);
        PreparedStatement mockTransactionStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement("SELECT status FROM books WHERE isbn = ?"))
                .thenReturn(mockCheckStatement);
        when(mockCheckStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate book being available
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("AVAILABLE");

        when(mockConnection.prepareStatement("UPDATE books SET status = 'BORROWED', assignedto = ? WHERE isbn = ?"))
                .thenReturn(mockUpdateStatement);
        when(mockUpdateStatement.executeUpdate()).thenReturn(1);

        when(mockConnection.prepareStatement("INSERT INTO transactions (reference, createdby, bookisbn, status, createdat, duedate) VALUES (?, ?, ?, ?, ?, ?)"))
                .thenReturn(mockTransactionStatement);
        when(mockTransactionStatement.executeUpdate()).thenReturn(1);

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Book object
            Book mockBook = new Book("Test Title", "Test Author", "BOOKISBN892321", "AVAILABLE");

            // Call the method under test
            boolean result = DBUtils.borrowBook(mockBook, "MPATRON02937847");

            // Verify interactions for check availability query
            verify(mockCheckStatement).setString(1, "BOOKISBN892321");
            verify(mockCheckStatement).executeQuery();

            // Verify interactions for update book query
            verify(mockUpdateStatement).setString(1, "MPATRON02937847");
            verify(mockUpdateStatement).setString(2, "BOOKISBN892321");
            verify(mockUpdateStatement).executeUpdate();

            // Verify interactions for insert transaction query
            // Use more precise verifications
            verify(mockTransactionStatement, times(4)).setString(anyInt(), anyString());

            // Assert result
            assertTrue(result);
        }
    }

    @Test
    void testBorrowBookUnavailable() throws SQLException {
        // Mock Connection and PreparedStatements
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockCheckStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockCheckStatement);
        when(mockCheckStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate book being unavailable
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("BORROWED");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Book object
            Book mockBook = new Book("Test Title", "Test Author", "BOOKISBN892321", "BORROWED");

            // Call the method under test
            boolean result = DBUtils.borrowBook(mockBook, "MPATRON02937847");

            // Verify interactions
            verify(mockCheckStatement).setString(1, "BOOKISBN892321");

            // Assert result
            assertFalse(result);
        }
    }

    @Test
    void testGetPatronIdByUsername() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate finding a patron ID
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("memberid")).thenReturn("MPATRON02937847");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            String patronId = DBUtils.getPatronIdByUsername("testUser");

            // Verify interactions
            verify(mockStatement).setString(1, "testUser");
            verify(mockStatement).executeQuery();

            // Assert result
            assertEquals("MPATRON02937847", patronId);
        }
    }

    @Test
    void testGetBorrowedBooks() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate two borrowed books in the ResultSet
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two rows, then end
        when(mockResultSet.getString("title")).thenReturn("Book 1", "Book 2");
        when(mockResultSet.getString("author")).thenReturn("Author 1", "Author 2");
        when(mockResultSet.getString("isbn")).thenReturn("ISBN1", "ISBN2");
        when(mockResultSet.getString("status")).thenReturn("BORROWED", "BORROWED");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            LinkedList<Book> borrowedBooks = DBUtils.getBorrowedBooks("MPATRON02937847");

            // Verify database interactions
            verify(mockConnection).prepareStatement("SELECT title, author, isbn, status FROM books WHERE assignedto = ?");
            verify(mockStatement).setString(1, "MPATRON02937847");
            verify(mockStatement).executeQuery();

            // Assert results
            assertEquals(2, borrowedBooks.size());

            Iterator<Book> iterator = borrowedBooks.iterator();
            assertEquals("Book 1", iterator.next().getTitle()); // First book title
            assertEquals("Book 2", iterator.next().getTitle()); // Second book title
        }
    }

    @Test
    void testAddBookSuccess() throws SQLException {
        // Create mocks for Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Mock the behavior of prepareStatement and executeUpdate
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        // Mock the static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            DBUtils.addBook("Test Book", "Test Author", "123456789");

            // Verify interactions with mocks
            verify(mockConnection).prepareStatement(anyString());
            verify(mockStatement).setString(1, "Test Book");
            verify(mockStatement).setString(2, "Test Author");
            verify(mockStatement).setString(3, "123456789");
            verify(mockStatement).setString(4, "AVAILABLE");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testAddBookSQLException() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Simulate SQL Exception when preparing statement
        try (MockedStatic<DatabaseConnection> mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Configure to throw SQLException
            when(mockConnection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Database connection error"));

            // Assert that a RuntimeException is thrown
            assertThrows(RuntimeException.class, () -> {
                DBUtils.addBook("Test Book", "Test Author", "123456789");
            }, "Should throw RuntimeException on SQL error");
        }
    }

    @Test
    void testGetAllBooks() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Prepare the query
            String query = "SELECT title, author, isbn, status FROM books;";

            // Configure mocks to simulate database interaction
            when(mockConnection.prepareStatement(query)).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            // Simulate result set with two books
            when(mockResultSet.next())
                    .thenReturn(true)   // First book
                    .thenReturn(true)   // Second book
                    .thenReturn(false); // End of results

            // Configure result set to return book details
            when(mockResultSet.getString("title"))
                    .thenReturn("Book 1")
                    .thenReturn("Book 2");
            when(mockResultSet.getString("author"))
                    .thenReturn("Author 1")
                    .thenReturn("Author 2");
            when(mockResultSet.getString("isbn"))
                    .thenReturn("ISBN1")
                    .thenReturn("ISBN2");
            when(mockResultSet.getString("status"))
                    .thenReturn("AVAILABLE")
                    .thenReturn("BORROWED");

            // Execute the method
            ObservableList<Book> resultBooks = DBUtils.getAllBooks();

            // Verify interactions
            verify(mockConnection).prepareStatement(query);
            verify(mockStatement).executeQuery();

            // Assertions
            assertNotNull(resultBooks, "Book list should not be null");
            assertEquals(2, resultBooks.size(), "Should have 2 books");

            // Verify first book details
            Book firstBook = resultBooks.get(0);
            assertEquals("Book 1", firstBook.getTitle(), "First book title should match");
            assertEquals("Author 1", firstBook.getAuthor(), "First book author should match");
            assertEquals("ISBN1", firstBook.getIsbn(), "First book ISBN should match");
            assertEquals("AVAILABLE", firstBook.getStatus(), "First book status should match");

            // Verify second book details
            Book secondBook = resultBooks.get(1);
            assertEquals("Book 2", secondBook.getTitle(), "Second book title should match");
            assertEquals("Author 2", secondBook.getAuthor(), "Second book author should match");
            assertEquals("ISBN2", secondBook.getIsbn(), "Second book ISBN should match");
            assertEquals("BORROWED", secondBook.getStatus(), "Second book status should match");
        }
    }

    @Test
    void testGetAllBooksEmptyResultSet() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Prepare the query
            String query = "SELECT title, author, isbn, status FROM books;";

            // Configure mocks to simulate empty result set
            when(mockConnection.prepareStatement(query)).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Execute the method
            ObservableList<Book> resultBooks = DBUtils.getAllBooks();

            // Verify interactions
            verify(mockConnection).prepareStatement(query);
            verify(mockStatement).executeQuery();

            // Assertions
            assertNotNull(resultBooks, "Book list should not be null");
            assertTrue(resultBooks.isEmpty(), "Book list should be empty");
        }
    }


    @Test
    void testGetTransactionsByUser() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate two transactions in the ResultSet
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two rows, then end
        when(mockResultSet.getString("reference")).thenReturn("Ref1", "Ref2");
        when(mockResultSet.getString("status")).thenReturn("ACTIVE", "RETURNED");
        when(mockResultSet.getDouble("fineAmount")).thenReturn(0.0, 5.0);
        when(mockResultSet.getDate("createdAt")).thenReturn(java.sql.Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDate("dueDate")).thenReturn(java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
        when(mockResultSet.getDate("returnDate")).thenReturn(null, java.sql.Date.valueOf(LocalDate.now().plusDays(10)));

        // Book details
        when(mockResultSet.getString("title")).thenReturn("Book 1", "Book 2");
        when(mockResultSet.getString("isbn")).thenReturn("ISBN1", "ISBN2");
        when(mockResultSet.getString("book_status")).thenReturn("BORROWED", "AVAILABLE");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            List<Transaction> transactions = DBUtils.getTransactionsByUser("MPATRON02937847");

            // Verify database interactions
            verify(mockConnection).prepareStatement("SELECT t.reference, t.status, t.fineAmount, t.createdAt, t.dueDate, t.returnDate, b.title, b.isbn, b.status AS book_status FROM transactions t JOIN books b ON t.bookisbn = b.isbn WHERE t.createdBy = ?");
            verify(mockStatement).setString(1, "MPATRON02937847");
            verify(mockStatement).executeQuery();

            // Assert results
            assertEquals(2, transactions.size());
            assertEquals("Ref1", transactions.get(0).getReference());
            assertEquals("Ref2", transactions.get(1).getReference());
            assertEquals("Book 1", transactions.get(0).getBook().getTitle());
            assertEquals("Book 2", transactions.get(1).getBook().getTitle());
        }
    }

    @Test
    void testUpdateTransactionStatus() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);  // Simulate successful update

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Transaction object
            Transaction mockTransaction = new Transaction("Ref123", null, "", 0.0, null);

            // Call the method under test
            DBUtils.updateTransactionStatus(mockTransaction);

            // Verify interactions
            verify(mockConnection).prepareStatement("UPDATE transactions SET status = ?, returnDate = ? WHERE reference = ?");
            verify(mockStatement).setString(1, "RETURNED");
            verify(mockStatement).setDate(2, Date.valueOf(LocalDate.now()));
            verify(mockStatement).setString(3, "Ref123");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testUpdateBookStatus() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);  // Simulate successful update

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Book object
            Book mockBook = new Book("Test Title", "Test Author", "BOOKISBN892321", "BORROWED");

            // Call the method under test
            DBUtils.updateBookStatus(mockBook);

            // Verify interactions
            verify(mockConnection).prepareStatement("UPDATE books SET status = ? WHERE isbn = ?");
            verify(mockStatement).setString(1, "AVAILABLE");
            verify(mockStatement).setString(2, "BOOKISBN892321");
            verify(mockStatement).executeUpdate();
        }
    }


    @Test
    void testIsBookAvailable() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate book being available
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("AVAILABLE");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            boolean result = DBUtils.isBookAvailable("BOOKISBN892321");

            // Verify interactions
            verify(mockStatement).setString(1, "BOOKISBN892321");
            verify(mockStatement).executeQuery();

            // Assert result
            assertTrue(result);
        }
    }

    @Test
    void testIsBookNotAvailable() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate book not being available
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("BORROWED");

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            boolean result = DBUtils.isBookAvailable("BOOKISBN892321");

            // Verify interactions
            verify(mockStatement).setString(1, "BOOKISBN892321");
            verify(mockStatement).executeQuery();

            // Assert result
            assertFalse(result);
        }
    }

    @Test
    void testGetReservedBooks() throws SQLException {
        // Mock Connection, PreparedStatement, and ResultSet
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate two reserved books in the ResultSet
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two rows, then end
        when(mockResultSet.getString("title")).thenReturn("Book 1", "Book 2");
        when(mockResultSet.getString("isbn")).thenReturn("ISBN1", "ISBN2");
        when(mockResultSet.getString("author")).thenReturn("Author 1", "Author 2");
        when(mockResultSet.getString("status")).thenReturn("RESERVED", "RESERVED");
        when(mockResultSet.getDate("reserved_on")).thenReturn(Date.valueOf("2023-12-01"));
        when(mockResultSet.getDate("expiration_date")).thenReturn(Date.valueOf("2023-12-08"));

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            List<Reservation> reservedBooks = DBUtils.getReservedBooks("MPATRON02937847");

            // Verify database interactions
            verify(mockStatement).setString(1, "MPATRON02937847");
            verify(mockStatement).executeQuery();

            // Assert results
            assertEquals(2, reservedBooks.size());
            assertEquals("Book 1", reservedBooks.get(0).getBook().getTitle());
            assertEquals("Book 2", reservedBooks.get(1).getBook().getTitle());
            assertEquals("Author 1", reservedBooks.get(0).getBook().getAuthor());
            assertEquals("Author 2", reservedBooks.get(1).getBook().getAuthor());
            assertEquals(Date.valueOf("2023-12-01").toString(), reservedBooks.get(0).getReservationDate());
            assertEquals(Date.valueOf("2023-12-08").toString(), reservedBooks.get(0).getExpirationDate());
        }
    }

    @Test
    void testCancelReservationSuccess() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockDeleteStatement = mock(PreparedStatement.class);
        PreparedStatement mockUpdateStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement("DELETE FROM reservations WHERE book_id = ? AND patron_id = ?")).thenReturn(mockDeleteStatement);
        when(mockDeleteStatement.executeUpdate()).thenReturn(1); // Simulate successful deletion

        when(mockConnection.prepareStatement("UPDATE books SET status = 'AVAILABLE', assignedto = NULL WHERE isbn = ?")).thenReturn(mockUpdateStatement);
        when(mockUpdateStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        // Mock BookReservationQueue
        BookReservationQueue mockQueue = mock(BookReservationQueue.class);
        when(mockQueue.isQueueEmpty()).thenReturn(true);

        // Access and modify the bookReservationQueues map directly if possible
        Map<String, BookReservationQueue> bookReservationQueues = new HashMap<>();
        bookReservationQueues.put("BOOKISBN892321", mockQueue);

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Reservation object
            Reservation mockReservation = new Reservation("Patron1", new Book("BOOKISBN892321"));

            // Directly manipulate the static map if accessible, or use a method to set it up
            DBUtils.setBookReservationQueues(bookReservationQueues);  // Hypothetical method to set the map

            // Call the method under test
            boolean result = DBUtils.cancelReservation(mockReservation);

            // Verify interactions
            verify(mockDeleteStatement).setString(1, "BOOKISBN892321");
            verify(mockDeleteStatement).setString(2, "Patron1");
            verify(mockDeleteStatement).executeUpdate();

            verify(mockUpdateStatement).setString(1, "BOOKISBN892321");
            verify(mockUpdateStatement).executeUpdate();

            // Assert result
            assertTrue(result);
        }
    }

    @Test
    void testCancelReservationFailure() throws SQLException {
        // Mock Connection and PreparedStatements
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockDeleteStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement("DELETE FROM reservations WHERE book_id = ? AND patron_id = ?")).thenReturn(mockDeleteStatement);
        when(mockDeleteStatement.executeUpdate()).thenReturn(0); // Simulate no deletion

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Mock Reservation object
            Reservation mockReservation = new Reservation("Patron1", new Book("BOOKISBN892321"));

            // Call the method under test
            boolean result = DBUtils.cancelReservation(mockReservation);

            // Verify interactions
            verify(mockDeleteStatement).setString(1, "BOOKISBN892321");
            verify(mockDeleteStatement).setString(2, "Patron1");
            verify(mockDeleteStatement).executeUpdate();

            // Assert result
            assertFalse(result);
        }
    }

    @Test
    void testUpdateReservationStatusToAssigned() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement("UPDATE reservations SET status = 'ASSIGNED' WHERE book_id = ? AND patron_id = ?")).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            DBUtils.updateReservationStatusToAssigned("BOOKISBN892321", "Patron1");

            // Verify interactions
            verify(mockStatement).setString(1, "BOOKISBN892321");
            verify(mockStatement).setString(2, "Patron1");
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    void testUpdateReservationStatusToAssignedFailure() throws SQLException {
        // Mock Connection and PreparedStatement
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        // Define behavior for the mocked objects
        when(mockConnection.prepareStatement("UPDATE reservations SET status = 'ASSIGNED' WHERE book_id = ? AND patron_id = ?")).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0); // Simulate no rows updated

        // Mock static method DatabaseConnection.getConnection()
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Call the method under test
            DBUtils.updateReservationStatusToAssigned("BOOKISBN892321", "Patron1");

            // Verify interactions
            verify(mockStatement).setString(1, "BOOKISBN892321");
            verify(mockStatement).setString(2, "Patron1");
            verify(mockStatement).executeUpdate();

            // No exception is thrown; just ensure no additional behavior occurs.
        }
    }

    @Test
    void testLoginUserSQLException() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Configure to throw SQLException when preparing statement
            when(mockConnection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Database connection error"));

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Assert that a RuntimeException is thrown
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                DBUtils.loginUser("testUser", "password");
            }, "Should throw RuntimeException on SQL error");

            // Verify the exception message
            assertTrue(exception.getMessage().contains("Database error occurred"));
            assertTrue(exception.getCause() instanceof SQLException);
        }
    }

    @Test
    void testGetBorrowedBooksSQLException() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Configure to throw SQLException when preparing statement
            when(mockConnection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Database connection error"));

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Execute method and verify empty list is returned
            LinkedList<Book> borrowedBooks = DBUtils.getBorrowedBooks("MPATRON02937847");
            assertTrue(borrowedBooks.isEmpty(), "Should return empty list on SQL error");
        }
    }

    @Test
    void testGetPatronIdByUsernameSQLException() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Configure to throw SQLException when preparing statement
            when(mockConnection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Database connection error"));

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Assert that a RuntimeException is thrown
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                DBUtils.getPatronIdByUsername("testUser");
            }, "Should throw RuntimeException on SQL error");

            // Verify the exception is a RuntimeException with the original SQLException
            assertTrue(exception.getCause() instanceof SQLException);
        }
    }

    @Test
    void testGetAllBooksSQLException() throws SQLException {
        // Mock DatabaseConnection and Connection
        try (MockedStatic<DatabaseConnection> dbConnectionMock = Mockito.mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);

            // Configure to throw SQLException when preparing statement
            when(mockConnection.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Database connection error"));

            // Mock DatabaseConnection to return mock connection
            dbConnectionMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

            // Execute method and verify empty list is returned
            ObservableList<Book> books = DBUtils.getAllBooks();
            assertTrue(books.isEmpty(), "Should return empty list on SQL error");
        }
    }

}