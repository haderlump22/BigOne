package de.rachel.app;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class LoginFx {

    public static Optional<String> showInputDialog() {
        // 1. Dialog erstellen (Rückgabetyp ist String)
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Benutzer eingeben");
        dialog.setHeaderText("Bitte gib deine Daten ein");

        // 2. Standard-Buttons hinzufügen (OK und Abbrechen)
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // 3. Layout und Eingabefelder erstellen
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // 4. Result Converter definieren (Wandelt Button-Klick in den Rückgabewert um)
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return nameField.getText();
            }
            return null; // Bei Abbrechen oder Schließen
        });

        // 5. Dialog anzeigen und auf Antwort warten
        return dialog.showAndWait();
    }
}
