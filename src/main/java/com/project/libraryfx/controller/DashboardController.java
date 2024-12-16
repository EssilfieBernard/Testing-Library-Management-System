package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import com.project.libraryfx.LinkedList;
import com.project.libraryfx.entities.Book;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button buttonLogout;

    @FXML
    private Button buttonHistory;

    @FXML
    private Label labelWelcomeText;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, String> statusColumn;

    @FXML
    private TableColumn<Book, Void> selectColumn; // For Radio Buttons

    @FXML
    private TextField searchField;

    @FXML
    private Button borrowButton;

    @FXML
    private Button reserveButton; // Added button for reservation

    private String loggedInUsername;
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Logout button handler
        buttonLogout.setOnAction(event -> {
            SessionManager.getInstance().clearSession();
            DBUtils.changeScene(event, "login.fxml", "Log in!", null, null);
        });

        // Restore user information
        String username = SessionManager.getInstance().getLoggedInUsername();
        if (username != null) {
            setUserInformation(username, "Welcome back!");
        }

        // Initialize table columns and load books
        setupTableColumns();
        loadBooks();

        // Set up search functionality
        setupSearchFunctionality();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add a column for radio buttons
        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final RadioButton radioButton = new RadioButton();

            {
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setOnAction(event -> {
                    // When the radio button is selected, select the row
                    getTableView().getSelectionModel().select(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(radioButton);
                }
            }
        });
    }

    private void loadBooks() {
        ObservableList<Book> books = DBUtils.getAllBooks();
        if (books != null) {
            booksTable.setItems(books);
        }
    }

    private void setupSearchFunctionality() {
        ObservableList<Book> books = booksTable.getItems();
        FilteredList<Book> filteredBooks = new FilteredList<>(books, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseFilter) ||
                        book.getIsbn().toLowerCase().contains(lowerCaseFilter);
            });
        });

        booksTable.setItems(filteredBooks);
    }

    public void setUserInformation(String username, String welcomeText) {
        loggedInUsername = username;
        labelWelcomeText.setText("Welcome " + username);
        String patronId = DBUtils.getPatronIdByUsername(username);
        SessionManager.getInstance().setLoggedInUser(username, patronId);
    }

    @FXML
    private void onMyReservationsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("reservations.fxml"));
            Parent root = loader.load();

            // Pass the logged-in patron's ID to the ReservationsController
            ReservationsController reservationsController = loader.getController();
            String patronId = SessionManager.getInstance().getPatronId();
            reservationsController.setPatronId(patronId); // Set patron ID to load their reservations

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("My Reservations");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleReserveBook(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            String patronId = getCurrentPatronId();
            if (patronId != null) {
                String status = selectedBook.getStatus();

                try {
                    boolean success = DBUtils.reserveBook(selectedBook, patronId);

                    Alert alert;
                    if (success && status.equalsIgnoreCase("Available")) {
                        selectedBook.setStatus("Reserved");
                        booksTable.refresh();
                        alert = new Alert(Alert.AlertType.INFORMATION, "Book reserved successfully!");
                    } else if (!success && status.equalsIgnoreCase("Borrowed")) {
                        alert = new Alert(Alert.AlertType.INFORMATION, "You have been added to the reservation queue.");
                    } else {
                        alert = new Alert(Alert.AlertType.WARNING, "Failed to reserve the book.");
                    }
                    alert.show();

                } catch (RuntimeException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setContentText(e.getMessage());
                    errorAlert.show();

                    System.err.println(e.getMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No book selected for reservation.");
            alert.show();
        }
    }


    private String getCurrentPatronId() {
        return DBUtils.getPatronIdByUsername(loggedInUsername);
    }

    @FXML
    private void onTransactionsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("transactions.fxml"));
            Parent root = loader.load();

            // Pass user data to the TransactionsController
            TransactionsController transactionsController = loader.getController();
            String patronId = SessionManager.getInstance().getPatronId(); // Retrieve the patron ID
            transactionsController.setPatronId(patronId); // Add a method in TransactionsController to accept patron ID

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transactions");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHistoryButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("history.fxml"));
            Parent root = loader.load();

            HistoryController historyController = loader.getController();
            String patronId = SessionManager.getInstance().getPatronId();
            LinkedList<Book> borrowedBooks = DBUtils.getBorrowedBooks(patronId);
            historyController.setBookHistory(borrowedBooks);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("History");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrowBook(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null && selectedBook.getStatus().equalsIgnoreCase("Available")) {
            String patronId = getCurrentPatronId();
            if (patronId != null) {
                // Perform borrow logic
                boolean success = DBUtils.borrowBook(selectedBook, patronId);
                if (success) {
                    selectedBook.setStatus("Borrowed");
                    booksTable.refresh();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book borrowed successfully!");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to borrow the book.");
                    alert.show();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Book is not available to be borrowed.");
            alert.show();
        }
    }
}
