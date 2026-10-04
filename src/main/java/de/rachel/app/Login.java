package de.rachel.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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

    // Statische Methode für den synchronen Aufruf aus Swing
    public static Optional<String> showInputDialog() {
        FutureTask<Optional<String>> task = new FutureTask<>(() -> {
            try {
                // 1. FXML laden
                FXMLLoader loader = new FXMLLoader(Login.class.getResource("LoginView.fxml"));
                DialogPane dialogPane = loader.load();

                // 2. Controller abgreifen
                Login controller = loader.getController();

                // 3. JavaFX Dialog mit der geladenen DialogPane erstellen
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Login");
                dialog.setDialogPane(dialogPane);

                // 4. ResultConverter steuert, was bei OK zurückgegeben wird
                dialog.setResultConverter(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        return controller.getUsernameText();
                    }
                    return null;
                });

                // 5. Anzeigen und blockieren
                return dialog.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });

        Platform.runLater(task);

        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
