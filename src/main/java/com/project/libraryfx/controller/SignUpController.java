package com.project.libraryfx.controller;

import com.project.libraryfx.DBUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

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
                DBUtils.createPatron(actionEvent, textFieldUsername.getText(), passwordFieldPassword.getText(), textFieldUsername.getText());
            }
        });

        buttonLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "login.fxml", "Log in!", null, null);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        });
    }
}