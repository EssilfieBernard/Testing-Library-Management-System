package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import com.project.libraryfx.entities.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class ReservationsController {

    @FXML
    private TableView<Reservation> reservedBooksTable;

    @FXML
    private TableColumn<Reservation, String> reservedTitleColumn;

    @FXML
    private TableColumn<Reservation, String> reservedIsbnColumn;

    @FXML
    private TableColumn<Reservation, String> reservedStatusColumn;

    @FXML
    private TableColumn<Reservation, String> reservedDateColumn;

    @FXML
    private TableColumn<Reservation, String> reservedExpireColumn;

    @FXML
    private TableColumn<Reservation, Void> reservedActionColumn;

    private String patronId;

    // Method to set the patron ID and load reservations
    public void setPatronId(String patronId) {
        this.patronId = patronId;
        loadReservations();
    }

    // Load the reservations for the given patron
    private void loadReservations() {
        List<Reservation> reservedBooks = DBUtils.getReservedBooks(patronId);

        if (reservedBooks != null && !reservedBooks.isEmpty()) {
            ObservableList<Reservation> booksObservableList = FXCollections.observableArrayList(reservedBooks);
            reservedBooksTable.setItems(booksObservableList);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have no active reservations.");
            alert.show();
        }
    }

    @FXML
    private void initialize() {
        // Initialize the table columns
        reservedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        reservedIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));
        reservedStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reservedDateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        reservedExpireColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        // Add a button to the action column
        reservedActionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                final TableCell<Reservation, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Cancel");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            cancelReservation(reservation);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    private void cancelReservation(Reservation reservation) {
        boolean success = DBUtils.cancelReservation(reservation);
        if (success) {
            loadReservations(); // Refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reservation cancelled successfully.");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to cancel reservation.");
            alert.show();
        }
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
            showErrorAlert("Error", "Unable to load the dashboard.");
        }
    }


    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}