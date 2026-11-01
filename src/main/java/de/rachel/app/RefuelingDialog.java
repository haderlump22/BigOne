package de.rachel.app;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import de.rachel.app.records.RefuelingRow;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class RefuelingDialog {
  private Connection dbConnection = null;
  private List<RefuelingRow> refuelingData = new ArrayList<>();
  private Stage ownStage = null;

  @FXML
  private TextField kilometer;

  @FXML
  private TextField liter;

  @FXML
  private ComboBox<String> automotiv;

  @FXML
  private ComboBox<String> fuelType;

  @FXML
  public void initialize() {
    UnaryOperator<TextFormatter.Change> decimalMaskFilter = change -> {
      String newText = change.getControlNewText();

      if (change.isDeleted()) {
        return change;
      }

      // Erlaube leeres Feld oder reine Ziffern mit maximal einem Komma/Punkt
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

      return null;
    };

    liter.setTextFormatter(new TextFormatter<>(decimalMaskFilter));
    liter.setPromptText("0,00");

    fill_automotiv();
    fill_fuelType();
  }

  @FXML
  private void saveRefuelingData() {
    refuelingData.add(new RefuelingRow(
      Integer.valueOf(kilometer.getText()),
      Double.valueOf(liter.getText().replace(',', '.')),
      BigOneTools.extractId(automotiv.getSelectionModel().getSelectedItem()),
      BigOneTools.extractId(fuelType.getSelectionModel().getSelectedItem())
    ));

    ownStage.close();
  }

  void setDbConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  List<RefuelingRow> getRefuelingData() {
    return refuelingData;
  }

  private void fill_automotiv() {
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT typ, kfz_id
        FROM kfz
        ORDER BY 1
        """);

    try {
      getter.beforeFirst();

      while (getter.next()) {
        automotiv.getItems().add(getter.getString("typ") + " (" + getter.getString("kfz_id") + ")");
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
  }

  private void fill_fuelType() {
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT bez, kraftstoff_id
        FROM kraftstoffe
        ORDER BY 1
        """);

    try {
      getter.beforeFirst();

      while (getter.next()) {
        fuelType.getItems().add(getter.getString("bez") + " (" + getter.getInt("kraftstoff_id") + ")");
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
  }

  void setOwnStage(Stage ownStage) {
    this.ownStage = ownStage;
  }
}
