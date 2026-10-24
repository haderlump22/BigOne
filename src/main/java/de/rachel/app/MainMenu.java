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
    Stage transactionsStage = new Stage();

    try {
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Transactions.fxml"));
      fxmlLoader.setControllerFactory(controllerClass -> {
        return new Transactions(dbConnection);
      });
      Scene transactionsScene = new Scene(fxmlLoader.load());

      transactionsStage.setResizable(false);
      transactionsStage.setTitle("Transaktionen");
      transactionsStage.setScene(transactionsScene);
      transactionsStage.show();
    } catch (IOException ex) {
      Alert loginFehler = new Alert(AlertType.ERROR);

      loginFehler.setTitle("Fehler");
      loginFehler.setHeaderText("konnte fxml Datei nicht laden...");
      loginFehler.setContentText(ex.getMessage());
      loginFehler.showAndWait();
    }
  }

  public void setConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }
}
