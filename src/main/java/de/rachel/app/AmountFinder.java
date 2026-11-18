package de.rachel.app;

import java.sql.Connection;
import java.time.LocalDate;

import de.rachel.app.datamodels.AmountFinderRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class AmountFinder {
  private Connection dbConnection = null;

  @FXML private TableView<AmountFinderRow> tableAmountFinder;
  @FXML private TableColumn<AmountFinderRow, Integer> colTransactionId;
  @FXML private TableColumn<AmountFinderRow, String> colCdtDbtIndicator;
  @FXML private TableColumn<AmountFinderRow, LocalDate> colDate;
  @FXML private TableColumn<AmountFinderRow, Double> colAmount;
  @FXML private TableColumn<AmountFinderRow, String> colComment;
  @FXML private TableColumn<AmountFinderRow, LocalDate> colBillingMonth;
  @FXML private TableColumn<AmountFinderRow, String> colBookingEvent;

  @FXML
  public void initialize() {
    colTransactionId.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty().asObject());
    colCdtDbtIndicator.setCellValueFactory(cellData -> cellData.getValue().cdtDbtIndicatorProperty());

    colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    colDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
    colDate.setOnEditCommit(event -> {
      AmountFinderRow row = event.getRowValue();
      row.setDate(event.getNewValue());
    });

    colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
    colAmount.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
    colAmount.setOnEditCommit(event -> {
      AmountFinderRow row = event.getRowValue();
      row.setAmount(event.getNewValue());
    });

    colComment.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
    colComment.setCellFactory(TextFieldTableCell.forTableColumn());
    colComment.setOnEditCommit(event -> {
      AmountFinderRow row = event.getRowValue();
      row.setComment(event.getNewValue());
    });
  }

  AmountFinder(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

}
