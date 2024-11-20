Library Management System
The Library Management System is a JavaFX application designed to help libraries
manage patrons, books, and transactions efficiently. It provides features for
authentication, book management, and transaction handling with a user-friendly interface.

Features
Authentication
Login functionality for patrons with username and password.
Manager login functionality to add and manage books.
Patron Management
Add new patrons during the signup process.
Book Management
View details of books available in the library.
Manager functionality to add new books to the catalog.
Transactions
Borrow and reserve books.
View transaction history with details such as:
Book name
Date created
Due date
Return borrowed books.
Queue Management
Handle book reservations using a custom queue implementation.
Technologies Used
Java 17
JavaFX 17
PostgreSQL
JDBC for database connectivity
FXML for UI design
Usage
Login
Enter your username and password on the login screen.
Managers can authenticate to access restricted operations like adding books.
Adding Books
Click the Add Book button.
Authenticate as a manager to access this functionality.
Enter book details and save.
Borrowing/Returning Books
Navigate to the Transactions Page.
Select a book and perform the desired operation:
Borrow
Return
Database Schema
Patrons Table
Field	Type	  Description
memberid	    VARCHAR(10)	Unique ID for each patron
username	    VARCHAR(50)	Username of the patron
password	    VARCHAR(100)	Hashed password
welcome_text	TEXT	Welcome message for the patron

Books Table
Field	Type	  Description
isbn	        VARCHAR(50)	Unique ISBN or ID for the book
title	        TEXT Title of the book
author	      TEXT	Author of the book
status	      VARCHAR(50)	Current status of the book
assignedto	  Foreign key	References memberid from patrons

Transactions Table
Field	Type	  Description
reference	    VARCHAR	Unique ID for each transaction
createdby	    VARCHAR(10)	Reference to patrons table
bookisbn	    VARCHAR(50)	Reference to books table
status	      VARCHAR(20)	Status of the transaction
created_at	  TIMESTAMP	Date of creation
due_date	    TIMESTAMP	Date when the book is due
return_date	  TIMESTAMP	Actual return date (if returned)
