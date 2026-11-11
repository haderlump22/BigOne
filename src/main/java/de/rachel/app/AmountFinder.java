package de.rachel.app;

import java.sql.Connection;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AmountFinder {
  private Connection dbConnection = null;
  private Stage ownStage;

  @FXML
  public void initialize() {

  }

  AmountFinder(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

}
