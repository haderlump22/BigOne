package de.rachel.app;

import java.sql.Connection;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class Transactions {
  private Connection dbConnection = null;

  @FXML
  private ComboBox<String> iban;

  @FXML
  public void initialize() {
    fill_Iban();
  }

  public Transactions(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  private void fill_Iban() {
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
}
