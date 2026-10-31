package de.rachel.app;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import de.rachel.app.records.RefuelingRow;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RefuelingDialog {
  private Connection dbConnection = null;
  List<RefuelingRow> refuelingData = new ArrayList<>();
  Stage ownStage = null;

  @FXML
  private TextField kilometer;

  @FXML
  private TextField liter;

  @FXML
  private ComboBox<String> automotiv;

  @FXML
  private ComboBox<String> fuelType;

  @FXML
  public void initialize() {
    fill_cmbKfz();
    fill_cmbTreibstoff();
  }

  @FXML
  private void saveRefuelingData() {
    Integer tmpKilometer;
    Double tmpLiter;
    Integer automotivId;
    Integer fuelId;

    // Wert auslesen
    tmpKilometer = Integer.valueOf(kilometer.getText());
    tmpLiter = Double.valueOf(liter.getText().replace(',', '.'));
    String automotivValue = automotiv.getSelectionModel().getSelectedItem();
    automotivId = Integer.valueOf(automotivValue.substring(automotivValue.indexOf('(') + 1, automotivValue.indexOf(')')));
    String fuelValue = fuelType.getSelectionModel().getSelectedItem();
    fuelId = Integer.valueOf(fuelValue.substring(fuelValue.indexOf('(') + 1, fuelValue.indexOf(')')));

    refuelingData.add(new RefuelingRow(
      tmpKilometer,
      tmpLiter,
      automotivId,
      fuelId
    ));

    // Dialog-Fenster schließen
    ownStage.close();
  }

  void setDbConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }

  List<RefuelingRow> getRefuelingData() {
    return refuelingData;
  }
    // RefuelingDialog(JFrame dialogOwner, String strBetrag, Connection LoginCN) {
    //     cn = LoginCN;
    //     final JDialog dialog = new JDialog(dialogOwner, "Tankdaten: " + strBetrag, true);
    //     dialog.setSize(220, 250);
    //     dialog.setLayout(null);
    //     dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    //     // schriftenfestlegungen
    //     fontTxtFields = new Font("Arial", Font.PLAIN, 16);
    //     fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);

    //     lblKm = new JLabel("Kilometer");
    //     lblKm.setBounds(10, 10, 60, 25);

    //     txtKm = new JFormattedTextField();
    //     txtKm.setFont(fontTxtFields);
    //     txtKm.setHorizontalAlignment(JFormattedTextField.RIGHT);
    //     txtKm.setText("0");
    //     txtKm.addFocusListener(new FocusListener() {
    //         public void focusLost(FocusEvent fe) {

    //         }

    //         public void focusGained(FocusEvent fe) {
    //             txtKm.selectAll();
    //         }
    //     });
    //     txtKm.setBounds(80, 10, 60, 25);

    //     lblLt = new JLabel("Liter");
    //     lblLt.setBounds(10, 40, 60, 25);

    //     txtLt = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
    //     txtLt.setFont(fontTxtFields);
    //     txtLt.setHorizontalAlignment(JFormattedTextField.RIGHT);
    //     txtLt.setText("0,00");
    //     txtLt.addFocusListener(new FocusListener() {
    //         public void focusLost(FocusEvent fe) {

    //         }

    //         public void focusGained(FocusEvent fe) {
    //             txtLt.selectAll();
    //         }
    //     });
    //     txtLt.setBounds(80, 40, 60, 25);

    //     lblKfz = new JLabel("Kfz");
    //     lblKfz.setBounds(10, 70, 60, 25);

    //     cmbKfz = new JComboBox<String>();
    //     cmbKfz.setFont(fontCmbBoxes);
    //     cmbKfz.setBounds(80, 70, 100, 25);
    //     fill_cmbKfz();

    //     lblTreibstoff = new JLabel("Treibstoff");
    //     lblTreibstoff.setBounds(10, 100, 60, 25);

    //     cmbTreibstoff = new JComboBox<String>();
    //     cmbTreibstoff.setFont(fontCmbBoxes);
    //     cmbTreibstoff.setBounds(80, 100, 100, 25);
    //     fill_cmbTreibstoff();

    //     btnTaSa = new JButton("Speichern");
    //     btnTaSa.setBounds(40, 130, 100, 30);
    //     btnTaSa.addActionListener(new ActionListener() {
    //         public void actionPerformed(ActionEvent ae) {
    //             dialog.setVisible(false);
    //             dialog.dispose();

    //         }

    //     });

    //     dialog.add(lblKm);
    //     dialog.add(txtKm);
    //     dialog.add(lblLt);
    //     dialog.add(txtLt);
    //     dialog.add(lblKfz);
    //     dialog.add(cmbKfz);
    //     dialog.add(lblTreibstoff);
    //     dialog.add(cmbTreibstoff);
    //     dialog.add(btnTaSa);

    //     dialog.setVisible(true);
    // }

  private void fill_cmbKfz() {
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT typ, kfz_id
        FROM kfz
        ORDER BY 1
        """);

    try {
      getter.beforeFirst();

      while (getter.next()) {
        automotiv.getItems().add(getter.getString("typ") + " (" + getter.getString("kfz_id") + ")");
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
  }

  private void fill_cmbTreibstoff() {
    DBTools getter = new DBTools(dbConnection);

    getter.select("""
        SELECT bez, kraftstoff_id
        FROM kraftstoffe
        ORDER BY 1
        """);

    try {
      getter.beforeFirst();

      while (getter.next()) {
        fuelType.getItems().add(getter.getString("bez") + " (" + getter.getInt("kraftstoff_id") + ")");
      }
    } catch (Exception e) {
      System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
          + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
    }
  }

  void setOwnStage(Stage ownStage) {
    this.ownStage = ownStage;
  }

    // public String get_km() {
    //     return txtKm.getText();
    // }

    // public String get_liter() {
    //     return txtLt.getText().replace(',', '.');
    // }

    // public String get_kfz_id() {
    //     String strKfz;
    //     strKfz = cmbKfz.getSelectedItem().toString();
    //     return strKfz.substring(strKfz.indexOf('(') + 1, strKfz.indexOf(')'));
    // }

    // public String get_treibstoff_id() {
    //     String strTreibstoff;
    //     strTreibstoff = cmbTreibstoff.getSelectedItem().toString();
    //     return strTreibstoff.substring(strTreibstoff.indexOf('(') + 1, strTreibstoff.indexOf(')'));
    // }
}
