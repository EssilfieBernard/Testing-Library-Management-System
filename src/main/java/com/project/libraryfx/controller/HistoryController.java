package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import com.project.libraryfx.LinkedList;
import com.project.libraryfx.entities.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;


public class HistoryController {

    public Button backButton;
    @FXML
    private TableView booksTable;

    @FXML
    private TableColumn titleColumn;

    @FXML
    private TableColumn authorColumn;

    @FXML
    private TableColumn isbnColumn;

    @FXML
    private TableColumn statusColumn;

    private LinkedList<Book> bookHistory = new LinkedList<>();

    @FXML
    public void initialize() {
        // Set up the columns with the properties from the Book class
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate the TableView
        populateTable();
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
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

    private void populateTable() {
        // Convert LinkedList to ObservableList
        ObservableList<Book> observableBooks = FXCollections.observableArrayList();
        for (Book book : bookHistory) {
            observableBooks.add(book);
        }

        booksTable.setItems(observableBooks);
    }

    // Method to set the book history from another controller
    public void setBookHistory(LinkedList<Book> books) {
        this.bookHistory = books;
        populateTable(); // Refresh table when new data is set
    }
}