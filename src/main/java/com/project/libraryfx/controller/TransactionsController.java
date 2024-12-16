package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import com.project.libraryfx.data_structures.BookReservationQueue;
import com.project.libraryfx.entities.Reservation;
import com.project.libraryfx.entities.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {

    @FXML
    private Button buttonBack;

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML
    private TableColumn<Transaction, String> bookTitleColumn;

    @FXML
    private TableColumn<Transaction, String> isbnColumn;

    @FXML
    private TableColumn<Transaction, String> statusColumn;

    @FXML
    private TableColumn<Transaction, Double> fineAmountColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<Transaction, Void> selectColumn; // For Radio Buttons

    @FXML
    private Button returnButton;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private String patronId; // Patron ID passed from DashboardController

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
    }

    public void setPatronId(String patronId) {
        this.patronId = patronId;
        loadTransactions();
    }

    @FXML
    public void onBackButtonClick(ActionEvent event) {
        try {
            // Load the dashboard FXML file
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the controller and restore user information
            DashboardController dashboardController = loader.getController();
            String username = SessionManager.getInstance().getLoggedInUsername();
            if (username != null) {
                dashboardController.setUserInformation(username, "Welcome back!");
            }

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        bookTitleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getTitle()));

        isbnColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getIsbn()));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fineAmountColumn.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final RadioButton radioButton = new RadioButton();

            {
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setOnAction(event -> getTableView().getSelectionModel().select(getIndex()));
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

    private void loadTransactions() {
        if (patronId != null) {
            try {
                transactions.clear();
                transactions.addAll(DBUtils.getTransactionsByUser(patronId));
                transactionsTable.setItems(transactions);
            } catch (Exception e) {
                showErrorAlert("Error loading transactions", "Unable to fetch transactions for the user.");
            }
        } else {
            showErrorAlert("Invalid Patron", "Patron ID is null. Unable to load transactions.");
        }
    }



    @FXML
    private void handleReturnBook(ActionEvent event) {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {

            if (selectedTransaction.getStatus().equals("ACTIVE")) {
                double fine = calculateFine(selectedTransaction);
                selectedTransaction.setFineAmount(fine);
                selectedTransaction.setStatus("RETURNED");
                selectedTransaction.getBook().setStatus("AVAILABLE");

                try {
                    // Update transaction and book status in the database
                    DBUtils.updateTransactionStatus(selectedTransaction);
                    DBUtils.updateBookStatus(selectedTransaction.getBook());

                    // Retrieve the queue for the book
                    String bookIsbn = selectedTransaction.getBook().getIsbn();
                    BookReservationQueue queue = DBUtils.getReservationQueue(bookIsbn);  // Retrieve the queue for this book

                    if (queue != null && !queue.isQueueEmpty()) {
                        // Look at the first reservation in the queue
                        Reservation nextReservation = queue.getNextReservation();

                        if (nextReservation != null) {
                            showInformationAlert("Book Available", "The book is now available for patron: " + nextReservation.getPatronId());
                        }

                        // Process reservations and assign the book to the first patron in the queue
                        queue.processReservation();  // This will reserve the book for the first patron in the queue
                    }

                } catch (Exception e) {
                    showErrorAlert("Database Update Error", "Failed to update transaction or book status.");
                    return;
                }

                transactionsTable.refresh();
                String message = fine > 0 ? "Book returned successfully! Fine: " + fine : "Book returned successfully!";
                showInformationAlert("Success", message);
            } else {
                showWarningAlert("Invalid Action", "This book has already been returned.");
            }
        } else {
            showErrorAlert("No Selection", "Please select a transaction to return.");
        }
    }



    private double calculateFine(Transaction transaction) {
        if (transaction.getDueDate() != null && LocalDate.now().isAfter(transaction.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(transaction.getDueDate(), LocalDate.now());
            return daysOverdue * 2;
        }
        return 0.0;
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
