package de.rachel.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import de.rachel.app.records.RefuelingRow;
import de.rachel.app.records.SplittingRow;
import de.rachel.app.records.TransactionsRow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import org.controlsfx.control.SearchableComboBox;

public class Transaction {
  private Connection dbConnection = null;
  private static final int TANKEN = 6;
  private static final int AUFTEILUNG = 52;
  private Stage ownStage;

  @FXML
  private ComboBox<String> iban;

  @FXML
  private CheckBox liquiRelevant;

  @FXML
  private TextField liquiDate;

  @FXML
  private TextField valueDate;

  @FXML
  private SearchableComboBox<String> event;

  @FXML
  private TextArea comment;

  @FXML
  private TextField amount;

  @FXML
  private RadioButton credit;

  @FXML
  private RadioButton debit;

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

    UnaryOperator<TextFormatter.Change> dateMaskFilter = change -> {
      if (change.isDeleted() || change.getControlNewText().isEmpty()) {
        return change;
      }

      String fullText = change.getControlNewText();
      String newText = change.getText();

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
    liquiDate.setPromptText("01.  .20  ");

    valueDate.setTextFormatter(new TextFormatter<>(dateMaskFilter));
    valueDate.setPromptText("  .  .20  ");

    amount.setTextFormatter(new TextFormatter<>(decimalMaskFilter));
    amount.setPromptText("0,00");

    comment.setWrapText(true);

    fill_iban();
    fill_event();

    iban.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {fill_event();});

    // ChangeListener an die selectedProperty hängen
    liquiRelevant.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        liquiDate.setText(null);
      } else {
        liquiDate.setText("01");
      }
    });
  }

  Transaction(Connection dbConnection) {
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

  private void fill_event() {
    DBTools getter = new DBTools(dbConnection);

    // if we change the iban we has to clear first from the old Content
    event.getItems().clear();

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

  @FXML
  private void onClickDelete(ActionEvent event) {
    resetContent();
  }

  private void resetContent() {
    debit.setSelected(true);
    liquiRelevant.setSelected(false);
    liquiDate.setText(null);
    valueDate.setText(null);
    amount.setText(null);
    comment.setText(null);
    event.getSelectionModel().selectFirst();
  }

  @FXML
  private void saveData() {
    List<TransactionsRow> dataToSave = new ArrayList<>();
    List<RefuelingRow> refuelingData = new ArrayList<>();
    List<SplittingRow> splittingData = new ArrayList<>();
    Integer transaktionsId = null;

    dataToSave.add(new TransactionsRow(
      debitOrCredit(),
      getKontoId(iban.getSelectionModel().getSelectedItem().replace(" ", "")),
      (liquiRelevant.isSelected() ? LocalDate.parse(liquiDate.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy")) : null),
      LocalDate.parse(valueDate.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
      Double.valueOf(amount.getText().replace(',', '.')),
      comment.getText(),
      BigOneTools.extractId(event.getSelectionModel().getSelectedItem())));

    // hier werden diverse unterformulare aufgerufen um
    // detailierte informatioenen zu einer Transaktionen aufzunehmen
    switch (dataToSave.get(0).eventId()) {
      case TANKEN:
        refuelingData = getRefulingData();

        if (refuelingData == null ) {
          System.exit(1);
        }

        // insert Data in that ordner, because we need the transaction_id for the refuelingData
        transaktionsId = insertTransaction(dataToSave);
        if (transaktionsId == null || transaktionsId < 0)
          System.exit(1);

        insertRefuelingData(transaktionsId, refuelingData);

        break;
      case AUFTEILUNG:
        splittingData = getSplittingData(dataToSave.get(0).amount(), comment.getText());

        // nun die Daten einfuegen
        transaktionsId = insertTransaction(dataToSave);
        if (transaktionsId == null || transaktionsId < 0)
          System.exit(1);

        insertSplittingData(transaktionsId, splittingData);

        break;
      default:
        insertTransaction(dataToSave);
    }
    resetContent();
  }

  private String debitOrCredit() {
    if (debit.isSelected())
      return "s";
    else
      return "h";
  }

  private Integer getKontoId(String Iban) {
    Integer kontoId = -1;
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT konten_id
        FROM konten
        WHERE gueltig = TRUE
        AND iban = '%s'
        """.formatted(Iban));

    try {
      // we can have one or no one Record
      // so is never mind that we run through
      // the resultset
      getter.beforeFirst();

      while (getter.next()) {
        kontoId = getter.getInt("konten_id");
      }
    } catch (Exception ex) {
      System.err.println(this.getClass().getName() + "/" + ex.getStackTrace()[2].getMethodName() + " (Line: "
          + ex.getStackTrace()[0].getLineNumber() + "): " + ex.toString());
      Alert kontoIdError = new Alert(AlertType.ERROR);

      kontoIdError.setTitle("Kontofehler");
      kontoIdError.setHeaderText("Kontoid zur ausgewählten Iban konnte nicht ermittelt werden...");
      kontoIdError.setContentText(ex.getMessage());
      kontoIdError.showAndWait();
      System.exit(1);
    }

    return kontoId;
  }

  private List<RefuelingRow> getRefulingData() {
    try {
      // 1. FXML laden
      FXMLLoader refuelingLoader = new FXMLLoader(getClass().getResource("RefuelDialog.fxml"));
      Parent refuelingDialogRoot = refuelingLoader.load();

      // 2. Controller des Dialogs holen (wichtig, um später Daten abzufragen)
      RefuelingDialog refuelingDialogController = refuelingLoader.getController();

      Stage refuelingDialogStage = new Stage();

      refuelingDialogController.setDbConnection(dbConnection);
      refuelingDialogController.setOwnStage(refuelingDialogStage);

      // 4. MODAL machen (blockiert das Hauptfenster)
      refuelingDialogStage.setTitle("Tankdaten: " + "0,00");
      refuelingDialogStage.initModality(Modality.APPLICATION_MODAL);
      refuelingDialogStage.initOwner(ownStage);
      refuelingDialogStage.setScene(new Scene(refuelingDialogRoot));
      refuelingDialogStage.showAndWait();

      // 6. Werte auslesen, NACHDEM der Dialog geschlossen wurde
      return refuelingDialogController.getRefuelingData();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private List<SplittingRow> getSplittingData(Double amount, String comment) {
    try {
      // 1. FXML laden
      FXMLLoader splittingLoader = new FXMLLoader(getClass().getResource("TransactionSplittingDialog.fxml"));
      Parent splittingDialogRoot = splittingLoader.load();

      // 2. Controller des Dialogs holen (wichtig, um später Daten abzufragen)
      TransactionSplittingDialog splittingDialogController = splittingLoader.getController();

      Stage splittingDialogStage = new Stage();

      splittingDialogController.setDbConnection(dbConnection);
      splittingDialogController.setAmountToSplit(amount);
      splittingDialogController.setComment(comment);
      splittingDialogController.setIban(iban.getSelectionModel().getSelectedItem().replace(" ", ""));
      splittingDialogController.setOwnStage(splittingDialogStage);

      // 4. MODAL machen (blockiert das Hauptfenster)
      splittingDialogStage.setTitle("Aufteilung");
      splittingDialogStage.initModality(Modality.APPLICATION_MODAL);
      splittingDialogStage.initOwner(ownStage);
      splittingDialogStage.setScene(new Scene(splittingDialogRoot));
      splittingDialogStage.showAndWait();

      // 6. Werte auslesen, NACHDEM der Dialog geschlossen wurde
      return splittingDialogController.getSplittingData();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  void setStage(Stage ownStage) {
    this.ownStage = ownStage;
  }

  private Integer insertTransaction(List<TransactionsRow> dataToSave) {
    DBTools dbTool = new DBTools(dbConnection);

    String sql = """
        INSERT INTO transaktionen
        (soll_haben, konten_id, datum, betrag, buchtext, ereigniss_id, liqui_monat)
        VALUES
        ('%s', %d, '%s', %s, '%s', %d
        """.formatted(dataToSave.get(0).creditDebitIndicator(),
                      dataToSave.get(0).accountId(),
                      dataToSave.get(0).valueDate(),
                      dataToSave.get(0).amount(),
                      dataToSave.get(0).comment(),
                      dataToSave.get(0).eventId());

    // if liqui should be null, then no single quotes allow
    if (dataToSave.get(0).liquiDate() == null)
      sql = sql + "," + dataToSave.get(0).liquiDate() + ") RETURNING transaktions_id";
    else
      sql = sql + ",'" + dataToSave.get(0).liquiDate() + "') RETURNING transaktions_id";

    if (!dbTool.insertWithReturn(sql)) {
      System.err.println("Fehler beim Einfügen der Transaktionsdaten...");
      return -1;
    } else {
      try {
        dbTool.first();
        return dbTool.getInt("transaktions_id");
      } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Fehler beim ermitteln des Insert - Rückgabewertes: transaktions_id");
        return -1;
      }
    }
  }

  private void insertRefuelingData(Integer transaktions_id, List<RefuelingRow> refuelingData) {
    // fuegt die Tanktaden in die entsprechende Tabelle anhand der im
    // BankStatementLine hinterlegten werte ein
    DBTools dbTool = new DBTools(dbConnection);

    dbTool.insert("""
        INSERT INTO tankdaten
        (transaktions_id, liter, km, kraftstoff_id, datum_bar, betrag_bar, kfz_id)
        VALUES
        (%d, %s, %s, %s, NULL, NULL, %s)
        """.formatted(transaktions_id,
                      refuelingData.get(0).liters(),
                      refuelingData.get(0).mileage(),
                      refuelingData.get(0).fuelId(),
                      refuelingData.get(0).automotivId()));
  }

  private void insertSplittingData(Integer transaktions_id, List<SplittingRow> splittingData) {
    // fuegt die aufteilungsdaten in die entsprechende Tabelle anhand der im
    // array hinterlegten werte ein
    DBTools dbTool = new DBTools(dbConnection);

    StringBuilder splittingImportData = new StringBuilder();

    for (SplittingRow splitRow : splittingData) {
      splittingImportData.append("(" +
          transaktions_id + "," +
          splitRow.amount() + "," +
          splitRow.eventId() + "," +
          splitRow.liquiRelevant() +
          "),");
    }

    // delete the Last commata from the String that represent the Import Values for the Import Statement
    splittingImportData.delete(splittingImportData.length() - 1, splittingImportData.length());

    if (!dbTool.insert(
        """
          INSERT INTO aufteilung
          (transaktions_id, betrag, ereigniss_id, liqui)
          VALUES
          %s
          """.formatted(splittingImportData))) {
      System.err.println("Einfügen der Abschlusssummenaufteilung fehlgeschlagen!");
      System.exit(1);
    }
  }
}
