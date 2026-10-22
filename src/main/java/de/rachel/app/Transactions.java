package de.rachel.app;

import java.sql.Connection;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class Transactions {
  private Connection cn = null;

  @FXML
  protected void openTransaction(ActionEvent event) {
    System.out.println("Connection that got: " + cn);
  }

  public void setConnection(Connection databaseConnection) {
    this.cn = databaseConnection;
  }
}
