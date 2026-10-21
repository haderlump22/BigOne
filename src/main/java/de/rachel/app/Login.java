package de.rachel.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

import com.google.gson.*;

public class Login {
  private Path configFile;
  private boolean devMode = true;
  private Config currentConfig;
  private FileWriter jsonConfigFile;
  private Connection cn = null;
  private int Logincount = 0;

  @FXML
  private VBox rootContainer;

  @FXML
  private TextField username;

  @FXML
  private PasswordField password;

  @FXML
  protected void onCancelClick(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
  }

  @FXML
  protected void onLoginClick(ActionEvent event) {

    try {
      Class.forName(currentConfig.getDatabaseDriver());
      cn = DriverManager.getConnection(currentConfig.getDatabaseUrl() + currentConfig.getDatabaseName(), username.getText(), password.getText());

      System.out.println("Connection established: " + cn.toString());

    } catch (Exception ex) {
      // ausnahme beschreibung auf der konsole ausgeben
      Logincount++;
      if (Logincount == 3) {
        Alert loginFehler = new Alert(AlertType.ERROR);

        loginFehler.setTitle("Verbindungsfehler");
        loginFehler.setHeaderText("Verbindung zur Datenbank konnte nicht aufgebaut werden...");
        loginFehler.setContentText(ex.getMessage());
        loginFehler.showAndWait();

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
      }
      // System.out.println(ex.toString());
    }
  }

  @FXML
  public void initialize() {
    Gson gsonParser = new Gson();

    try {
      if (devMode) {
        configFile = Paths.get(System.getProperty("user.home") + "/BigOneConfig/BigOneConfigDev.json");
        rootContainer.setStyle("-fx-background-color: red;");
      } else {
        configFile = Paths.get(System.getProperty("user.home") + "/BigOneConfig/BigOneConfig.json");
      }

      BufferedReader configReader = Files.newBufferedReader(configFile);

      currentConfig = gsonParser.fromJson(configReader, Config.class);

      username.setText(currentConfig.getUserName());
      password.setText(currentConfig.getPassword());

    } catch (IOException e) {
      System.err.println("Config Datei konnte nicht gelesen/gefunden werden: " + e.getMessage());
      writeTemplateConfigFile();
    } catch (Exception e) {
      System.err.println("Fehler beim JSON-Parsing: " + e.getMessage());
    }
  }

  private void writeTemplateConfigFile() {
    Config exampleConfig = new Config();

    exampleConfig.setDatabaseDriver("org.postgresql.Driver");
    exampleConfig.setDatabaseUrl("jdbc:postgresql:\\/\\/localhost:5432\\/");
    exampleConfig.setDatabaseName("<dbName>");
    exampleConfig.setUserName("<dbUserName>");
    exampleConfig.setPassword("<dbPassword>");

    Gson gsonWriter = new GsonBuilder().setPrettyPrinting().create();

    try {
      if (devMode) {
        jsonConfigFile = new FileWriter(System.getProperty("user.home") + "/BigOneConfig/BigOneConfigDev.json", StandardCharsets.UTF_8);
      } else {
        jsonConfigFile = new FileWriter(System.getProperty("user.home") + "/BigOneConfig/BigOneConfig.json", StandardCharsets.UTF_8);
      }

      gsonWriter.toJson(exampleConfig, jsonConfigFile);
      jsonConfigFile.close();

      System.out.println("Eine Beispiel Config ist in den HomeFolder unter dem Verzeichnis BigOneConfig, geschrieben worden. Bitte passen Sie diese an und starten das Programm neu..");
      System.exit(0);
    } catch (IOException e) {
      System.err.println("Beispiel Config konnte nicht geschrieben werden: " + e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      System.err.println("Fehler beim erstellen der Beispieldatei: " + e.getMessage());
      System.exit(1);
    }
  }
}
