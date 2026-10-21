package de.rachel.app;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Login {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public String getUsernameText() {
        return username != null ? username.getText() : "";
    }

    public String getPasswordText() {
        return password != null ? password.getText() : "";
    }
}
