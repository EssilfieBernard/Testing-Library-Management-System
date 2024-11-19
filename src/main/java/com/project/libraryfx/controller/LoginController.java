package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public Button buttonAddBook;
    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private Button buttonLogin;

    @FXML
    private Button buttonSignup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String username = textFieldUsername.getText();
                String password = passwordFieldPassword.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please enter both username and password.");
                    alert.show();
                    return;
                }

                DBUtils.loginUser(actionEvent, username, password);
            }
        });

        buttonSignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "signup.fxml", "Sign up!", null, null);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        });
    }

    @FXML
    public void onAddBookClick() {
        try {
            // Load Manager Authentication Page
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource("ManagerAuth.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Manager Authentication");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}