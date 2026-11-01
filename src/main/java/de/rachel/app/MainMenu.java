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
      Alert loginFehler = new Alert(AlertType.ERROR);

      loginFehler.setTitle("Fehler");
      loginFehler.setHeaderText("konnte fxml Datei nicht laden...");
      loginFehler.setContentText(ex.getMessage());
      loginFehler.showAndWait();
    }
  }

  void setConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }
}
