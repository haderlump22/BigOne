package de.rachel.app;

import java.sql.Connection;
import java.time.LocalDate;

import de.rachel.app.datamodels.AmountFinderRow;
import de.rachel.app.util.InputFormatters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class AmountFinder {
  private Connection dbConnection = null;
  ObservableList<AmountFinderRow> amountFinderData = FXCollections.observableArrayList();

  @FXML private TableView<AmountFinderRow> tableAmountFinder;
  @FXML private TableColumn<AmountFinderRow, Integer> colTransactionId;
  @FXML private TableColumn<AmountFinderRow, String> colCdtDbtIndicator;
  @FXML private TableColumn<AmountFinderRow, LocalDate> colDate;
  @FXML private TableColumn<AmountFinderRow, Double> colAmount;
  @FXML private TableColumn<AmountFinderRow, String> colComment;
  @FXML private TableColumn<AmountFinderRow, LocalDate> colBillingMonth;
  @FXML private TableColumn<AmountFinderRow, String> colBookingEvent;
  @FXML private TextField searchAmount;
  @FXML private ComboBox<String> iban;
  @FXML private CheckBox liquiRelevant;
  @FXML private TextField liquiDate;

  @FXML
  public void initialize() {
    colTransactionId.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty().asObject());
    colCdtDbtIndicator.setCellValueFactory(cellData -> cellData.getValue().cdtDbtIndicatorProperty());

    colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    colDate.setCellFactory(col -> new TextFieldTableCell<>(InputFormatters.GERMAN_DATE_CONVERTER) {
      @Override
      public void startEdit() {
        super.startEdit();

        if (isEditing() && getGraphic() instanceof TextField dateEditTextField) {
          dateEditTextField.setTextFormatter(new TextFormatter<>(InputFormatters.DATE_MASK_FILTER));
        }
      }
    });
    colDate.setOnEditCommit(event -> {
      LocalDate newValue = event.getNewValue();

      if (newValue == null) {
        event.getTableView().refresh();
        return;
      }
      event.getRowValue().setDate(newValue);
    });

    colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
    colAmount.setCellFactory(col -> new TextFieldTableCell<>(InputFormatters.GERMAN_DOUBLE_CONVERTER) {
      @Override
      public void startEdit() {
        super.startEdit();

        // Grafik der Zelle ist während des Editierens das TextField
        if (isEditing() && getGraphic() instanceof TextField amountEditTextField) {
          String currentText = amountEditTextField.getText();
          if (currentText != null && currentText.contains(".")) {
            amountEditTextField.setText(currentText.replace('.', ','));
          }
          amountEditTextField.setTextFormatter(new TextFormatter<>(InputFormatters.DECIMAL_MASK_FILTER));
        }
      }
    });
    colAmount.setOnEditCommit(event -> {
      Double newVal = event.getNewValue();
      if (newVal == null) {
        event.getTableView().refresh();
        return;
      }
      event.getRowValue().setAmount(newVal);
    });

    colComment.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
    colComment.setCellFactory(TextFieldTableCell.forTableColumn());
    colComment.setOnEditCommit(event -> {
      AmountFinderRow row = event.getRowValue();
      row.setComment(event.getNewValue());
    });

    colBillingMonth.setCellValueFactory(cellData -> cellData.getValue().billingMonthProperty());
    colBillingMonth.setCellFactory(col -> new TextFieldTableCell<>(InputFormatters.GERMAN_DATE_CONVERTER) {
      @Override
      public void startEdit() {
        super.startEdit();

        if (isEditing() && getGraphic() instanceof TextField dateEditTextField) {
          dateEditTextField.setTextFormatter(new TextFormatter<>(InputFormatters.DATE_MASK_FILTER));
        }
      }
    });
    colBillingMonth.setOnEditCommit(event -> {
      LocalDate newValue = event.getNewValue();

      if (newValue == null) {
        event.getTableView().refresh();
        return;
      }
      event.getRowValue().setDate(newValue);
    });

    colBookingEvent.setCellValueFactory(cellData -> cellData.getValue().bookingEventProperty());

    liquiDate.setTextFormatter(new TextFormatter<>(InputFormatters.DATE_MASK_FILTER));
    liquiDate.setPromptText("01.  .20  ");

    searchAmount.setTextFormatter(new TextFormatter<>(InputFormatters.DECIMAL_MASK_FILTER));
    searchAmount.setPromptText("0,00");

    // ChangeListener an die selectedProperty hängen
    liquiRelevant.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        liquiDate.setText(null);
        liquiDate.setDisable(true);
        searchAmount.requestFocus();
      } else {
        liquiDate.setText("01");
        liquiDate.setDisable(false);
      }
    });

    fill_iban();

    iban.setTooltip(new Tooltip(getAccountDescription(iban.getValue().replaceAll(" ", ""))));
    iban.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {iban.setTooltip(new Tooltip(getAccountDescription(newValue.replaceAll(" ", ""))));});

    // ObservableList<AmountFinderRow> amountFinderData = FXCollections.observableArrayList(
    //   new AmountFinderRow(0, "s", LocalDate.now(), 45.45, "versuchsdaten", LocalDate.now().plusDays(5), "Hello (9)"),
    //   new AmountFinderRow(0, "s", LocalDate.now(), 45.45, "versuchsdaten", LocalDate.now().plusDays(5), "bye (10)")
    // );

    // System.out.println(amountFinderData.toString());
    tableAmountFinder.setItems(amountFinderData);
  }

  AmountFinder(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  @FXML
  private void searchEnteredValue() {
    /*
     * ermittelt die dem Betrag entsprechenden Datensätze
     * des ausgewählten Kontos und des gesetzten Liquizeitraumes
     * sortiert nach Datum in absteigender Form
     * zur Darstellung in der Tabelle
     * Wird das Liquidatum als leerer String übergeben
     * wird es nicht berücksichtigt
     */

    DBTools getter = new DBTools(dbConnection);
    amountFinderData.clear();
    String newSearchValue = searchAmount.getText().replace(".", "").replace(',', '.');
    String accountType = iban.getTooltip().getText();

    getter.select("""
        SELECT t.transaktions_id, t.soll_haben, t.datum, t.betrag, t.buchtext, t.liqui_monat, k.%s ereignis
        FROM transaktionen t, %s k, konten kto
        WHERE t.betrag = %s
        AND k.%s = t.ereigniss_id
        %s
        AND t.konten_id = kto.konten_id
        AND kto.iban = '%s'
        ORDER BY t.datum DESC
        """.formatted((accountType.equals("Haushaltskonto") ? "kategoriebezeichnung" : "ereigniss_krzbez"),
        (accountType.equals("Haushaltskonto") ? "ha_kategorie" : "kontenereignisse"),
        newSearchValue,
        (accountType.equals("Haushaltskonto") ? "ha_kategorie_id" : "ereigniss_id"),
        ((liquiDate.getText() == null || liquiDate.getText().isEmpty()) ? "" : " and t.liqui_monat = '" + liquiDate.getText() + "'"),
        iban.getValue().replaceAll(" ", "")));

    try {
      getter.beforeFirst();

      while (getter.next()) {
        amountFinderData.add(new AmountFinderRow(getter.getInt("transaktions_id"),
            getter.getString("soll_haben"),
            (getter.getDate("datum")).toLocalDate(),
            getter.getDouble("betrag"),
            getter.getString("buchtext"),
            (getter.getDate("liqui_monat") != null) ? (getter.getDate("liqui_monat")).toLocalDate() : null,
            getter.getString("ereignis")));
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
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

  private String getAccountDescription(String IBAN) {
    // get the transaction wording to the transaktions_id
    DBTools getter = new DBTools(dbConnection);
    String returnValue = "not found";

    getter.select("""
        SELECT bemerkung
        FROM konten
        WHERE iban = '%s'
        """.formatted(IBAN));

    try {
      getter.beforeFirst();

      while (getter.next()) {
        returnValue = getter.getString("bemerkung");
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
      System.err.println("etwas hat beim ermitteln der Konto Bemerkung nicht geklappt!");
      System.exit(1);
    }

    return returnValue;
  }
}
