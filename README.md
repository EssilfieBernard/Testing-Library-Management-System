Test Cases Documentation

DBUtils Test

1. Test Case: Login User Success
Test Scenario: Verify that a user can successfully log in with valid credentials.
Preconditions:
The user "testUser" exists in the database with the password "password123".
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a successful query execution that returns a matching user.
Call the DBUtils.loginUser method with "testUser" and "password123".
Expected Outcome: The method should return an Optional containing the welcome message "Welcome back!".
Dependencies: Requires a functioning database connection and mock setup.
2. Test Case: Login User Not Found
Test Scenario: Verify that login fails when the user does not exist.
Preconditions:
The user "nonExistentUser" does not exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns no user.
Call the DBUtils.loginUser method with "nonExistentUser" and "password123".
Expected Outcome: The method should return an empty Optional.
Dependencies: Requires a functioning database connection and mock setup.
3. Test Case: Create Patron Success
Test Scenario: Verify that a new patron can be created successfully.
Preconditions:
The username "testUser" and member ID "M12345" do not exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate successful execution of insert statements for creating a patron.
Call the DBUtils.createPatron method with "testUser", "password123", and "Welcome!".
Expected Outcome: The method should return the generated member ID "M12345".
Dependencies: Requires a functioning database connection and mock setup.
4. Test Case: Reserve Book Success
Test Scenario: Verify that a book can be reserved successfully by a patron.
Preconditions:
The book with ISBN "BOOKISBN892321" is available in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for checking availability, updating status, and inserting reservation.
Simulate successful execution of all queries.
Call the DBUtils.reserveBook method with the test book and patron ID "MPATRON02937847".
Expected Outcome: The method should return true, indicating successful reservation.
Dependencies: Requires a functioning database connection and mock setup.
5. Test Case: Borrow Book Success
Test Scenario: Verify that a book can be successfully borrowed by a patron.
Preconditions:
The book with ISBN "BOOKISBN892321" is available in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for checking availability, updating status, and inserting a transaction.
Simulate successful execution of all queries.
Call the DBUtils.borrowBook method with the test book and patron ID "MPATRON02937847".
Expected Outcome: The method should return true, indicating successful borrowing.
Dependencies: Requires a functioning database connection and mock setup.
6. Test Case: Borrow Book Unavailable
Test Scenario: Verify that borrowing fails when the book is already borrowed.
Preconditions:
The book with ISBN "BOOKISBN892321" is marked as "BORROWED" in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for checking availability.
Simulate a query execution that returns the status "BORROWED".
Call the DBUtils.borrowBook method with the test book and patron ID "MPATRON02937847".
Expected Outcome: The method should return false, indicating borrowing failure due to unavailability.
Dependencies: Requires a functioning database connection and mock setup.
7. Test Case: Get Patron ID by Username Success
Test Scenario: Verify that the correct patron ID is retrieved using a valid username.
Preconditions:
The username "testUser" exists in the database with member ID "MPATRON02937847".
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a successful query execution that returns a matching member ID.
Call the DBUtils.getPatronIdByUsername method with "testUser".
Expected Outcome: The method should return "MPATRON02937847".
Dependencies: Requires a functioning database connection and mock setup.
8. Test Case: Add Book Success
Test Scenario: Verify that a new book can be added to the library database successfully.
Preconditions:
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate successful execution of insert statements for adding a new book.
Call the DBUtils.addBook method with "Test Book", "Test Author", and ISBN "123456789".
Expected Outcome: The method should complete without exceptions, indicating successful addition of the book.
Dependencies: Requires a functioning database connection and mock setup.
9. Test Case: Get Borrowed Books Success
Test Scenario: Verify that the system retrieves a list of books borrowed by a specific patron.
Preconditions:
The patron ID "MPATRON02937847" exists in the database.
The patron has borrowed at least two books.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns two borrowed books in the result set.
Call the DBUtils.getBorrowedBooks method with "MPATRON02937847".
Expected Outcome:
The method should return a list containing two books with correct titles, authors, ISBNs, and statuses.
Dependencies: Requires a functioning database connection and mock setup.
10. Test Case: Get Borrowed Books SQL Exception
Test Scenario: Verify that an empty list is returned when an SQL exception occurs while retrieving borrowed books.
Preconditions:
Database connection is established but throws an exception during query execution.
Test Steps:
Mock the database connection to throw an SQLException.
Call the DBUtils.getBorrowedBooks method with "MPATRON02937847".
Expected Outcome:
The method should return an empty list and handle the exception gracefully.
Dependencies: Requires mock setup to simulate SQL exceptions.
11. Test Case: Add Book SQL Exception
Test Scenario: Verify that a RuntimeException is thrown when an SQL exception occurs while adding a book.
Preconditions:
Database connection is established but throws an exception during query preparation or execution.
Test Steps:
Mock the database connection to throw an SQLException during prepareStatement.
Call the DBUtils.addBook method with valid book details ("Test Book", "Test Author", "123456789").
Expected Outcome:
The method should throw a RuntimeException with an appropriate error message and include the original SQLException.
Dependencies: Requires mock setup to simulate SQL exceptions.
12. Test Case: Update Book Status Success
Test Scenario: Verify that a book's status is updated successfully in the database.
Preconditions:
The book with ISBN "BOOKISBN892321" exists in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for updating book status.
Simulate successful execution of the update query.
Call the DBUtils.updateBookStatus method with a valid Book object (e.g., status changed to "AVAILABLE").
Expected Outcome:
The method should complete without exceptions, indicating successful update of book status.
Dependencies: Requires mock setup for database interaction.
13. Test Case: Cancel Reservation Success
Test Scenario: Verify that a reservation can be canceled successfully and the book status is updated to "AVAILABLE".
Preconditions:
A reservation exists for patron ID "Patron1" and book ISBN "BOOKISBN892321".
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for deleting reservations and updating book status.
Simulate successful deletion of reservation and update of book status queries.
Call the DBUtils.cancelReservation method with a valid Reservation object.
Expected Outcome:
The method should return true, indicating successful cancellation of reservation and update of book status.
Dependencies: Requires mock setup for database interaction.
14. Test Case: Get All Books Success
Test Scenario: Verify that all books in the library are retrieved successfully.
Preconditions:
At least two books exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns a list of books.
Call the DBUtils.getAllBooks method.
Expected Outcome:
The method should return an ObservableList containing two books with correct titles, authors, ISBNs, and statuses.
Dependencies: Requires a functioning database connection and mock setup.
15. Test Case: Get All Books Empty Result Set
Test Scenario: Verify that an empty list is returned when no books exist in the database.
Preconditions:
No books exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns no results.
Call the DBUtils.getAllBooks method.
Expected Outcome:
The method should return an empty ObservableList.
Dependencies: Requires a functioning database connection and mock setup.
16. Test Case: Get Transactions By User Success
Test Scenario: Verify that transactions for a specific user are retrieved successfully.
Preconditions:
The user "MPATRON02937847" has at least two transactions in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns two transactions for the user.
Call the DBUtils.getTransactionsByUser method with "MPATRON02937847".
Expected Outcome:
The method should return a list containing two transactions with correct details such as reference, status, fine amount, and book information.
Dependencies: Requires a functioning database connection and mock setup.
17. Test Case: Update Transaction Status Success
Test Scenario: Verify that a transaction's status is updated successfully in the database.
Preconditions:
The transaction reference "Ref123" exists in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for updating transaction status.
Simulate successful execution of the update query.
Call the DBUtils.updateTransactionStatus method with a valid Transaction object (e.g., status changed to "RETURNED").
Expected Outcome:
The method should complete without exceptions, indicating successful update of transaction status.
Dependencies: Requires mock setup for database interaction.
18. Test Case: Get Reserved Books Success
Test Scenario: Verify that the system retrieves a list of books reserved by a specific patron.
Preconditions:
The patron ID "MPATRON02937847" has reserved at least two books in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that returns two reserved books in the result set.
Call the DBUtils.getReservedBooks method with "MPATRON02937847".
Expected Outcome:
The method should return a list containing two reservations with correct details, including book title, author, ISBN, reservation date, and expiration date.
Dependencies: Requires a functioning database connection and mock setup.
19. Test Case: Cancel Reservation Failure
Test Scenario: Verify that the system does not cancel a reservation if no matching record is found in the database.
Preconditions:
The reservation for patron ID "Patron1" and book ISBN "BOOKISBN892321" does not exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for deleting reservations.
Simulate a query execution that deletes zero rows (indicating no matching record).
Call the DBUtils.cancelReservation method with a valid Reservation object.
Expected Outcome:
The method should return false, indicating that no reservation was canceled.
Dependencies: Requires mock setup for database interaction.
20. Test Case: Update Reservation Status to Assigned Success
Test Scenario: Verify that a reservation's status is updated to "ASSIGNED" successfully in the database.
Preconditions:
The reservation for book ISBN "BOOKISBN892321" and patron ID "Patron1" exists in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for updating reservation status.
Simulate successful execution of the update query.
Call the DBUtils.updateReservationStatusToAssigned method with valid book ISBN and patron ID.
Expected Outcome:
The method should complete without exceptions, indicating successful update of reservation status to "ASSIGNED".
Dependencies: Requires mock setup for database interaction.
21. Test Case: Is Book Available Success
Test Scenario: Verify that the system correctly identifies if a book is available for borrowing or reservation.
Preconditions:
The book with ISBN "BOOKISBN892321" exists in the database with status "AVAILABLE".
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for checking book status.
Simulate a query execution that returns "AVAILABLE" as the status of the book.
Call the DBUtils.isBookAvailable method with "BOOKISBN892321".
Expected Outcome:
The method should return true, indicating that the book is available.
Dependencies: Requires mock setup for database interaction.
22. Test Case: Add to Reservation Queue SQL Exception
Test Scenario: Verify that an exception is handled gracefully when adding to a reservation queue fails due to an SQL error.
Preconditions:
Database connection is established but throws an exception during query preparation or execution.
Test Steps:
Mock the database connection to throw an SQLException during prepareStatement.
Call the DBUtils.addToReservationQueue method with valid book and patron details.
Expected Outcome:
The method should catch and handle the exception gracefully without crashing, ensuring no changes are made to the database state.
Dependencies: Requires mock setup to simulate SQL exceptions.
23. Test Case: Add Book with Existing ISBN
Test Scenario: Verify that adding a book with an existing ISBN results in a failure.
Preconditions:
A book with ISBN "123456789" already exists in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects.
Simulate a query execution that checks for existing ISBN and finds a match.
Call the DBUtils.addBook method with "Duplicate Book", "Author", and ISBN "123456789".
Expected Outcome:
The method should throw a RuntimeException, indicating a duplicate entry error.
Dependencies: Requires mock setup to simulate database interaction.
24. Test Case: Update Transaction Status Failure
Test Scenario: Verify that updating a transaction status fails when no matching transaction is found.
Preconditions:
The transaction reference "Ref123" does not exist in the database.
Database connection is established.
Test Steps:
Mock the database connection and prepare statement objects for updating transaction status.
Simulate an update query execution that affects zero rows (indicating no matching transaction).
Call the DBUtils.updateTransactionStatus method with a non-existent Transaction object.
Expected Outcome:
The method should complete without exceptions, but no changes should be made to the database.
Dependencies: Requires mock setup for database interaction.
25. Test Case: Borrow Book SQL Exception
Test Scenario: Verify that an SQL exception is handled gracefully when borrowing a book fails due to a database error.
Preconditions:
Database connection is established but throws an exception during query preparation or execution.
Test Steps:
Mock the database connection to throw an SQLException during prepareStatement.
Call the DBUtils.borrowBook method with valid book and patron details.
Expected Outcome:
The method should catch and handle the exception gracefully without crashing, ensuring no changes are made to the database state.
Dependencies: Requires mock setup to simulate SQL exceptions.

Reservation Test 


1. Test Case: Constructor with All Parameters
Test Scenario: Verify that the Reservation object is correctly initialized when all parameters are provided.
Preconditions:
A Book object with title "Test Title", author "Test Author", and ISBN "1234567890" is created.
Test Steps:
Create a Reservation object using the constructor with all parameters: book, patron ID, status, reservation date, and expiration date.
Check the values of each field in the Reservation object.
Expected Outcome:
The Reservation object should have the correct book, patron ID, status, reservation date, and expiration date.
Dependencies: Requires a valid Book object.
2. Test Case: Constructor with Patron and Book
Test Scenario: Verify that the Reservation object is correctly initialized when only patron and book are provided.
Preconditions:
A Book object with title "Test Title", author "Test Author", and ISBN "1234567890" is created.
Test Steps:
Create a Reservation object using the constructor with patron ID and book.
Check the values of each field in the Reservation object.
Expected Outcome:
The Reservation object should have the correct book and patron ID.
Status, reservation date, and expiration date should be null (assuming default behavior).
Dependencies: Requires a valid Book object.
3. Test Case: Get Book Title
Test Scenario: Verify that the correct book title is returned from a reservation.
Preconditions:
A Book object with title "Test Title" is associated with a reservation.
Test Steps:
Create a Reservation object with a valid book.
Call the getBookTitle method on the reservation.
Expected Outcome:
The method should return "Test Title".
Additional Test Steps for Null Book:
Create a Reservation object with a null book.
Call the getBookTitle method on the reservation.
Expected Outcome for Null Book:
The method should return "Unknown".
4. Test Case: Get Book ISBN
Test Scenario: Verify that the correct book ISBN is returned from a reservation.
Preconditions:
A Book object with ISBN "1234567890" is associated with a reservation.
Test Steps:
Create a Reservation object with a valid book.
Call the getBookIsbn method on the reservation.
Expected Outcome:
The method should return "1234567890".
Additional Test Steps for Null Book:
Create a Reservation object with a null book.
Call the getBookIsbn method on the reservation.
Expected Outcome for Null Book:
The method should return "Unknown".
General Notes for Documenting Reservation Test Cases
Ensure constructors are tested for different initialization scenarios to verify default values and correct parameter handling.
Include edge cases such as null inputs to ensure robustness against unexpected conditions.

Book Test


1. Test Case: Default Constructor
Test Scenario: Verify that a Book object is correctly initialized with default values when using the default constructor.
Preconditions: None.
Test Steps:
Create a Book object using the default constructor.
Check the values of each field in the Book object.
Expected Outcome:
The title, author, isbn, status, and assignedTo fields should all be null.
2. Test Case: Constructor with Title, Author, ISBN, and Status
Test Scenario: Verify that a Book object is correctly initialized when title, author, ISBN, and status are provided.
Preconditions: None.
Test Steps:
Create a Book object using the constructor with title, author, ISBN, and status parameters.
Check the values of each field in the Book object.
Expected Outcome:
The title, author, and isbn fields should match the provided values.
The status field should match the provided status ("CHECKED_OUT").
The assignedTo field should be null.
3. Test Case: Constructor with Title, Author, and ISBN
Test Scenario: Verify that a Book object is correctly initialized when title, author, and ISBN are provided without status.
Preconditions: None.
Test Steps:
Create a Book object using the constructor with title, author, and ISBN parameters.
Check the values of each field in the Book object.
Expected Outcome:
The title, author, and isbn fields should match the provided values.
The status field should default to "AVAILABLE".
The assignedTo field should be null.
4. Test Case: Constructor with All Parameters
Test Scenario: Verify that a Book object is correctly initialized when all parameters are provided, including a patron.
Preconditions:
A valid Patron object is created.
Test Steps:
Create a Book object using the constructor with all parameters including a patron.
Check the values of each field in the Book object.
Expected Outcome:
The title, author, and isbn fields should match the provided values.
The status field should match the provided status ("RESERVED").
The assignedTo field should match the provided patron.
5. Test Case: Constructor with ISBN Only
Test Scenario: Verify that a Book object is correctly initialized when only ISBN is provided.
Preconditions: None.
Test Steps:
Create a Book object using the constructor with only an ISBN parameter.
Check the values of each field in the Book object.
Expected Outcome:
The isbn field should match the provided value.
The other fields (title, author, status, and assignedTo) should be null.
6. Test Case: Setters and Getters
Test Scenario: Verify that setters and getters work correctly for all fields in a Book object.
Preconditions:
A valid Patron object is created for assignment.
Test Steps:
Create a default Book object.
Use setters to set new values for each field in the book.
Use getters to retrieve each value and verify correctness.
Expected Outcome:
Each getter should return the value set by its corresponding setter.

Patron Test


1. Test Case: Default Constructor
Test Scenario: Verify that a Patron object is correctly initialized with default values when using the default constructor.
Preconditions: None.
Test Steps:
Create a Patron object using the default constructor.
Check the values of each field in the Patron object.
Expected Outcome:
The username and memberId fields should both be null.
2. Test Case: Parameterized Constructor
Test Scenario: Verify that a Patron object is correctly initialized when a member ID and username are provided.
Preconditions: None.
Test Steps:
Create a Patron object using the constructor with member ID and username parameters.
Check the values of each field in the Patron object.
Expected Outcome:
The username field should match "JohnDoe".
The memberId field should match "12345".
3. Test Case: Setters and Getters
Test Scenario: Verify that setters and getters work correctly for all fields in a Patron object.
Preconditions: None.
Test Steps:
Create a default Patron object.
Use setters to set new values for each field in the patron.
Use getters to retrieve each value and verify correctness.
Expected Outcome:
The getUsername method should return "JaneDoe".
The getMemberId method should return "67890".

Session Manager Test

1. Test Case: Singleton Instance
Test Scenario: Verify that the SessionManager class implements the singleton pattern correctly.
Preconditions: None.
Test Steps:
Retrieve two instances of SessionManager using SessionManager.getInstance().
Compare the two instances to ensure they are the same.
Expected Outcome:
Both instances should be identical (i.e., assertSame should pass).
Dependencies: None.
2. Test Case: Set Logged-In User
Test Scenario: Verify that the logged-in user details are correctly set in the session.
Preconditions: None.
Test Steps:
Call sessionManager.setLoggedInUser("testUser", "12345").
Retrieve the username and patron ID using getLoggedInUsername() and getPatronId().
Expected Outcome:
The username should be "testUser".
The patron ID should be "12345".
Dependencies: None.
3. Test Case: Clear Session
Test Scenario: Verify that clearing the session removes all logged-in user details.
Preconditions:
A user is already set in the session with setLoggedInUser("testUser", "12345").
Test Steps:
Call sessionManager.clearSession().
Retrieve the username and patron ID using getLoggedInUsername() and getPatronId().
Expected Outcome:
Both username and patron ID should be null after clearing the session.
Dependencies: None.
4. Test Case: Get Logged-In Username and Patron ID Initially Null
Test Scenario: Verify that the username and patron ID are null before any user is logged in.
Preconditions: None.
Test Steps:
Retrieve the username using getLoggedInUsername().
Retrieve the patron ID using getPatronId().
Expected Outcome:
Both methods should return null since no user has been logged in yet.
Dependencies: None.

Stack Test

1. Test Case: Push Operation
Test Scenario: Verify that elements are correctly pushed onto the stack.
Preconditions: The stack is initially empty.
Test Steps:
Push the integer 1 onto the stack.
Push the integer 2 onto the stack.
Check the size of the stack.
Peek at the top element of the stack.
Expected Outcome:
The size of the stack should be 2.
The top element should be 2, indicating that it is the last pushed element.
2. Test Case: Pop Operation
Test Scenario: Verify that elements are correctly popped from the stack.
Preconditions: The stack contains two elements, 1 and 2.
Test Steps:
Pop an element from the stack.
Check the popped element.
Check the size of the stack after popping.
Peek at the top element of the stack.
Expected Outcome:
The popped element should be 2, which is the last pushed element.
The size of the stack should be 1.
The top element should now be 1.
3. Test Case: Peek Operation
Test Scenario: Verify that peeking returns the top element without removing it from the stack.
Preconditions: The stack contains two elements, 1 and 2.
Test Steps:
Peek at the top element of the stack.
Pop an element from the stack.
Peek at the top element again after popping.
Expected Outcome:
The first peek should return 2.
After popping, the second peek should return 1.
4. Test Case: IsEmpty Operation
Test Scenario: Verify that isEmpty correctly identifies whether the stack is empty or not.
Preconditions: The stack is initially empty.
Test Steps:
Check if the stack is empty initially.
Push an element onto the stack.
Check if the stack is empty after pushing.
Pop an element from the stack.
Check if the stack is empty after popping.
Expected Outcome:
Initially, isEmpty should return true.
After pushing, isEmpty should return false.
After popping, isEmpty should return true.
5. Test Case: Size Operation
Test Scenario: Verify that size returns the correct number of elements in the stack.
Preconditions: The stack is initially empty.
Test Steps:
Check initial size of the stack.
Push two elements onto the stack.
Check size after pushing elements.
Pop an element from the stack.
Check size after popping an element.
Expected Outcome:
Initial size should be 0.
Size after pushing two elements should be 2.
Size after popping one element should be 1.

Transaction Test

1. Test Case: Default Constructor
Test Scenario: Verify that a Transaction object is correctly initialized with default values when using the default constructor.
Preconditions: None.
Test Steps:
Create a Transaction object using the default constructor.
Check the values of each field in the Transaction object.
Expected Outcome:
The reference, createdBy, book, status, createdAt, dueDate, and returnDate fields should all be null.
The fineAmount should be 0.00.
2. Test Case: Parameterized Constructor with Patron and Book
Test Scenario: Verify that a Transaction object is correctly initialized when a patron and book are provided.
Preconditions:
A valid Patron object and a valid Book object are created.
Test Steps:
Create a Transaction object using the constructor with patron and book parameters.
Check the values of each field in the Transaction object.
Expected Outcome:
The createdBy field should match the provided patron.
The book field should match the provided book.
The status field should be "ACTIVE".
The fineAmount should be 0.00.
The createdAt field should not be null, indicating initialization time.
The dueDate should be five days after the creation date.
The returnDate should be null.
3. Test Case: Parameterized Constructor with Details
Test Scenario: Verify that a Transaction object is correctly initialized when all details are provided.
Preconditions: None.
Test Steps:
Create a Transaction object using the constructor with reference, book, status, fine amount, and due date parameters.
Check the values of each field in the Transaction object.
Expected Outcome:
The reference, book, and status fields should match the provided values.
The fineAmount should match the provided value (10.00).
The dueDate should match the provided due date.
4. Test Case: Setters and Getters
Test Scenario: Verify that setters and getters work correctly for all fields in a Transaction object.
Preconditions:
A valid Patron object and a valid Book object are created.
Test Steps:
Create a default Transaction object.
Use setters to set new values for each field in the transaction.
Use getters to retrieve each value and verify correctness.
Expected Outcome:
Each getter should return the value set by its corresponding setter, including reference, createdBy, book, status, fineAmount, createdAt, dueDate, and returnDate.
