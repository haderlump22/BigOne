package de.rachel.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class MainMenu {
  private Connection dbConnection = null;

  @FXML
  protected void openTransaction(ActionEvent event) {
    Stage transactionStage = new Stage();

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Transaction.fxml"));
      fxmlLoader.setControllerFactory(controllerClass -> {
        return new Transaction(dbConnection);
      });

      Scene transactionScene = new Scene(fxmlLoader.load());
      Transaction transactionsController = fxmlLoader.getController();

      transactionsController.setStage(transactionStage); // needed for setting as Parrent for a Dialogstage, creating in this controller
      transactionStage.setResizable(false);
      transactionStage.setTitle("Transaktionen");
      transactionStage.setScene(transactionScene);
      transactionStage.show();
    } catch (IOException ex) {
      Alert fxmlLoadFailure = new Alert(AlertType.ERROR);

      fxmlLoadFailure.setTitle("Fehler");
      fxmlLoadFailure.setHeaderText("konnte fxml Datei nicht laden...");
      fxmlLoadFailure.setContentText(ex.getMessage());
      fxmlLoadFailure.showAndWait();
    }
  }

  @FXML
  protected void openAmountFinder(ActionEvent event) {
    Stage amountFinderStage = new Stage();

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("AmountFinder.fxml"));
      fxmlLoader.setControllerFactory(controllerClass -> {
        return new AmountFinder(dbConnection);
      });

      Scene amountFinderScene = new Scene(fxmlLoader.load());

      amountFinderStage.setResizable(false);
      amountFinderStage.setTitle("Beträge finden");
      amountFinderStage.setScene(amountFinderScene);
      amountFinderStage.show();
    } catch (IOException ex) {
      Alert fxmlLoadFailure = new Alert(AlertType.ERROR);

      fxmlLoadFailure.setTitle("Fehler");
      fxmlLoadFailure.setHeaderText("konnte fxml Datei nicht laden...");
      fxmlLoadFailure.setContentText(ex.getMessage());
      fxmlLoadFailure.showAndWait();
    }
  }

  void setConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }
}
