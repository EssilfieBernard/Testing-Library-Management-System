package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

import static com.project.libraryfx.DBUtils.changeScene;

public class SignUpController implements Initializable {
    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private Button buttonSignup;

    @FXML
    private Button buttonLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonSignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String username = textFieldUsername.getText();
                String password = passwordFieldPassword.getText();
                String welcomeText = textFieldUsername.getText();

                try {
                    // Perform patron creation logic
                    String memberId = DBUtils.createPatron(username, password, welcomeText);

                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setContentText("Account created successfully! Redirecting to login page. Your member id is " + memberId);
                    successAlert.showAndWait();

                    // Change scene after successful creation
                    changeScene(actionEvent, "dashboard.fxml", "Welcome!", username, welcomeText);

                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(e.getMessage());
                    alert.show();

                } catch (RuntimeException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(e.getMessage());
                    alert.show();

                    System.err.println(e.getMessage());
                }
            }
        });


        buttonLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                changeScene(actionEvent, "login.fxml", "Log in!", null, null);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        });
    }
}