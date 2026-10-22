package de.rachel.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Connection;


public class MainMenu {
  private Connection cn = null;

  @FXML
  protected void openTransaction(ActionEvent event) {
    System.out.println("Connection that got: " + cn);
  }

  public void setConnection(Connection databaseConnection) {
    this.cn = databaseConnection;
  }
}
