package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddBookController {

    @FXML
    private Button buttonLogout;

    @FXML
    private TextField textFieldTitle;

    @FXML
    private TextField textFieldAuthor;

    @FXML
    private TextField textFieldISBN;

    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonSave;

    @FXML
    private void onSaveClick() {
        String title = textFieldTitle.getText().trim();
        String author = textFieldAuthor.getText().trim();
        String isbn = textFieldISBN.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        try {
            boolean success = DBUtils.addBook(title, author, isbn);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Book added successfully");
                alert.show();
                System.out.println("Saving book: " + title + " by " + author + ", ISBN: " + isbn);
            }
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());

            alert.show();

            System.err.println(e.getMessage());
        }
    }


    @FXML
    private void onCancelClick() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) buttonCancel.getScene().getWindow();

            // Set the login page scene
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            // Handle errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to load the login page: " + e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }
}
