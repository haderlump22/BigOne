package de.rachel.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class Transactions {
  private Connection dbConnection = null;

  @FXML
  private ComboBox<String> iban;
  @FXML
  private CheckBox liquiRelevant;

  @FXML
  private TextField liquiDate;

  @FXML
  private TextField valueDate;

  @FXML
  private ComboBox<String> ereignis;

  @FXML
  private TextArea transactionText;

  @FXML
  private TextField amount;

  @FXML
  public void initialize() {

    UnaryOperator<TextFormatter.Change> decimalMaskFilter = change -> {
      String newText = change.getControlNewText();

      // 1. Wenn der Text gelöscht wird (BackSpace / Entf), erlauben wir das immer
      if (change.isDeleted()) {
        return change;
      }

      // 2. Erlaube leeres Feld oder reine Ziffern mit maximal einem Komma/Punkt
      if (newText.matches("^\\d*[.,]?\\d*$")) {
        try {
          // Konvertiere das Komma für Java in einen Punkt, falls vorhanden
          String parseable = newText.replace(",", ".");

          // Wenn nur ein Punkt/Komma dasteht (z.B. "." oder ","), erlauben
          if (parseable.equals(".") || parseable.isEmpty()) {
            return change;
          }

          // pharsen testen
          Double.parseDouble(parseable);

          return change;
        } catch (NumberFormatException e) {
          // Falls das Parsen fehlschlägt, Änderung verwerfen
        }
      }

      return null; // Änderung erlauben
    };

    UnaryOperator<TextFormatter.Change> dateMaskFilter = change -> {
      String fullText = change.getControlNewText();
      String newText = change.getText();

      // 1. Wenn der Text gelöscht wird (BackSpace / Entf), erlauben wir das immer
      if (change.isDeleted()) {
        return change;
      }

      // Maximale Länge auf 10 Zeichen begrenzen (DD-MM-20YY)
      if (fullText.length() > 10) {
        return null;
      }

      // nur Ziffern erlauben
      if (!newText.matches("\\d*")) {
        return null;
      }

      // automatisches setzen der zwischenzeichen
      if (fullText.length() == 2 || fullText.length() == 5) {
        change.setText(newText + ".");

        // Die exakte neue Position berechnen (Start der Eingabe + Länge des neuen Textes inkl. Punkt)
        int newPos = change.getRangeStart() + change.getText().length();

        // Caret UND Anchor auf dieselbe Position setzen (verhindert das Markieren)
        change.setCaretPosition(newPos);
        change.setAnchor(newPos);
      }

      return change; // Änderung erlauben
    };

    liquiDate.setTextFormatter(new TextFormatter<>(dateMaskFilter));
    liquiDate.setPromptText("  .  .20  ");

    valueDate.setTextFormatter(new TextFormatter<>(dateMaskFilter));
    valueDate.setPromptText("  .  .20  ");

    amount.setTextFormatter(new TextFormatter<>(decimalMaskFilter));
    amount.setPromptText("0,00");

    transactionText.setWrapText(true);

    fill_iban();
    fill_ereignis();

    iban.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {fill_ereignis();});
  }

  public Transactions(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  private void fill_iban() {
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT iban
        FROM konten
        WHERE gueltig = TRUE
        """);
    try {
      getter.beforeFirst();

      while (getter.next()) {
        iban.getItems().add(BigOneTools.getIbanFormatted(getter.getString("iban")));
      }

      iban.getSelectionModel().selectLast();

    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
  }

  private void fill_ereignis() {
    DBTools getter = new DBTools(dbConnection);

    // if we change the iban we has to clear first from the old Content
    ereignis.getItems().clear();

    if (isJointAccount(iban.getSelectionModel().getSelectedItem().toString().replace(" ", ""))) {
      getter.select("""
          SELECT ha_kategorie_id id, kategoriebezeichnung bez
          FROM ha_kategorie
          ORDER BY 2
          """);
    } else {
      getter.select("""
          SELECT ereigniss_id id, ereigniss_krzbez bez
          FROM kontenereignisse
          WHERE gueltig = TRUE
          ORDER BY 2
          """);
    }

    try {
      getter.beforeFirst();

      while (getter.next()) {
        ereignis.getItems().add(getter.getString("bez") + " (" + getter.getInt("id") + ")");
      }

      ereignis.getSelectionModel().selectFirst();

    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }

  }

  private boolean isJointAccount(String iban) {
    DBTools getter = new DBTools(dbConnection);
    boolean isJointAccount = false;

    getter.select("""
        SELECT bemerkung
        FROM konten
        WHERE iban = '%s'
        """.formatted(iban));

    try {
      getter.first();
      isJointAccount = getter.getString("bemerkung").equals("Haushaltskonto");
    } catch (SQLException e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
      System.exit(1);
    }

    return isJointAccount;
  }
}
