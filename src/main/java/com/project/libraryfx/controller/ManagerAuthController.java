package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManagerAuthController {

    @FXML
    private TextField textFieldManagerId;

    @FXML
    private void onVerifyClick() {
        String managerId = textFieldManagerId.getText();

        if ("12345".equals(managerId)) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("add_book.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Add Book");
                stage.show();

                // Close the Manager Authentication Page
                Stage currentStage = (Stage) textFieldManagerId.getScene().getWindow();
                currentStage.close();
            } catch (Exception e) {
                showErrorAlert("Error", "Unable to load the Add Book page.");
            }
        } else {
            showErrorAlert("Access Denied", "Invalid Manager ID!");
        }
    }

    // Helper function to show error alerts
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}