package de.rachel.app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.google.gson.*;

public class Login {

  private JDialog login;
  private int Logincount = 0;
  private JLabel lblUeberschrift, lblName, lblPW;
  private JTextField txtBenutzer;
  private JPasswordField txtPW;
  private JButton btnLogin;
  private String strB, strPW;
  private Connection cn = null;
  private Path configFile;
  private FileWriter jsonConfigFile;
  private boolean devMode = true;
  private Config currentConfig;

  public Login(JFrame dialogOwner) {
    Gson gsonParser = new Gson();

    try {
      if (devMode) {
        configFile = Paths.get(System.getProperty("user.home") + "/BigOneConfig/BigOneConfigDev.json");
      } else {
        configFile = Paths.get(System.getProperty("user.home") + "/BigOneConfig/BigOneConfig.json");
      }

      BufferedReader configReader = Files.newBufferedReader(configFile);

      currentConfig = gsonParser.fromJson(configReader, Config.class);

    } catch (IOException e) {
      System.err.println("Config Datei konnte nicht gelesen/gefunden werden: " + e.getMessage());
      writeTemplateConfigFile();
    } catch (Exception e) {
      System.err.println("Fehler beim JSON-Parsing: " + e.getMessage());
    }

    login = new JDialog(dialogOwner, "LOGIN", true);
    login.setSize(290, 165);
    login.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    login.setLayout(null);
    login.getContentPane().setBackground(Color.white);

    // check if DevMode ist aktive and make it visible
    if (devMode) {
      login.setTitle("LOGIN !!DEVMOD!!");
      login.getContentPane().setBackground(Color.RED);
    }

    lblUeberschrift = new JLabel("Login to BigOne");
    lblUeberschrift.setBounds(10, 10, 270, 25);

    lblName = new JLabel("Username");
    lblName.setBounds(10, 40, 100, 25);

    lblPW = new JLabel("Password");
    lblPW.setBounds(10, 70, 100, 25);

    txtBenutzer = new JTextField();
    txtBenutzer.setBounds(120, 40, 120, 25);
    txtBenutzer.setText(currentConfig.getDbUserName());

    txtPW = new JPasswordField("");
    txtPW.setBounds(120, 70, 120, 25);
    txtPW.setText(currentConfig.getDbPw());

    btnLogin = new JButton("Login");
    btnLogin.setBounds(100, 100, 90, 25);
    btnLogin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        // zuweisung der Textfeldwerte an die lokalen Varablen
        strB = txtBenutzer.getText();
        strPW = new String(txtPW.getPassword());

        try {
          // Select fitting database driver and connect:
          Class.forName(currentConfig.getDbDrv());
          cn = DriverManager.getConnection(currentConfig.getDbUrl() + currentConfig.getDbName(), strB, strPW);
          login.dispose();
        } catch (Exception ex) {
          // ausnahme beschreibung auf der konsole ausgeben
          txtPW.setText("");
          txtPW.requestFocus();
          Logincount++;
          if (Logincount == 3) {
            JOptionPane.showMessageDialog(null, "maximale Anzahl der Loginversuche überschritten", "Achtung",
                JOptionPane.INFORMATION_MESSAGE);
            login.dispose();
          }
          // System.out.println(ex.toString());
        }

      }
    });

    login.add(lblUeberschrift);
    login.add(lblName);
    login.add(lblPW);
    login.add(txtBenutzer);
    login.add(txtPW);
    login.add(btnLogin);
    login.validate();
    login.repaint();
    login.setVisible(true);

    txtBenutzer.requestFocus();
  }

  public Connection getConnection() {
    return cn;
  }

  public String getUser() {
    return txtBenutzer.getText();
  }

  public int getLogincount() {
    return Logincount;
  }

  private void writeTemplateConfigFile() {
    Config exampleConfig = new Config();

    exampleConfig.setDbDrv("org.postgresql.Driver");
    exampleConfig.setDbName("<dbName>");
    exampleConfig.setDbPw("<dbPassword>");
    exampleConfig.setDbUrl("jdbc:postgresql:\\/\\/localhost:5432\\/");
    exampleConfig.setDbUserName("<dbUserName>");

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
