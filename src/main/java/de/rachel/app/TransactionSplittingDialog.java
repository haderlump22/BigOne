package de.rachel.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import de.rachel.app.records.SplittingRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.controlsfx.control.SearchableComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class TransactionSplittingDialog {
  private Connection dbConnection = null;
  private Stage ownStage = null;
  private Double amountToSplit = 0.0;
  private Double amountRestToSplit = 0.0;
  private String iban = "";
  private List<SplittingRow> splittingData = new ArrayList<>();

  @FXML
  private CheckBox liquiRelevant;

  @FXML
  private TextField amount;

  @FXML
  private SearchableComboBox<String> event;

  @FXML
  private Button save;

  @FXML
  private Button nextSplitValue;

  @FXML
  private Label openToSplit;

  @FXML
  private Label transactionInfo;

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

    amount.setTextFormatter(new TextFormatter<>(decimalMaskFilter));
    amount.setPromptText("0,00");

    fill_event();

  }

  @FXML
  private void addNextSplitValue(ActionEvent actionEvent) {
    splittingData.add(new SplittingRow(
      Double.valueOf(amount.getText().replace(',', '.')),
      BigOneTools.extractId(event.getSelectionModel().getSelectedItem()),
      liquiRelevant.isSelected()));

    amountRestToSplit = amountRestToSplit - Double.valueOf(amount.getText().replace(',', '.'));

    if (amountRestToSplit == 0.0) {
      nextSplitValue.setDisable(true);
      save.setDisable(false);
      openToSplit.setText("noch " + amountRestToSplit.toString().replace('.', ',') + " EUR");
    } else {
      amount.setText(null);
      event.getSelectionModel().selectFirst();
      openToSplit.setText("noch " + amountRestToSplit.toString().replace('.', ',') + " EUR");
    }
  }

  @FXML
  private void saveSplitting(ActionEvent actionEvent) {
    ownStage.close();
  }

  void setDbConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  void setOwnStage(Stage ownStage) {
    this.ownStage = ownStage;
  }

  void setAmountToSplit(Double amountToSplit) {
    this.amountToSplit = amountToSplit;
    this.amountRestToSplit = amountToSplit;
    ownStage.setTitle("Aufteilung: " + this.amountToSplit.toString().replace('.', ','));
    openToSplit.setText("noch " + amountRestToSplit.toString().replace('.', ',') + " EUR");
  }

  void setComment(String comment) {
    transactionInfo.setTooltip(new Tooltip(comment));
  }

  void setIban(String iban) {
    this.iban = iban;
  }

  List<SplittingRow> getSplittingData() {
    return splittingData;
  }

  private void fill_event() {
    DBTools getter = new DBTools(dbConnection);

    // if we change the iban we has to clear first from the old Content
    event.getItems().clear();

    if (isJointAccount(iban)) {
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
        event.getItems().add(getter.getString("bez") + " (" + getter.getInt("id") + ")");
      }

      event.getSelectionModel().selectFirst();

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
