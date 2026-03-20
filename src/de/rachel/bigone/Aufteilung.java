package de.rachel.bigone;

//import static de.rachel.bigone.DBTools.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

public class Aufteilung {
	private JLabel lblBetrag, lblEreigniss, lblRestInfo, lblBuchText;
	private JCheckBox chkLiquiFaehig;
	private JComboBox<String> cmbEreigniss;
	private JButton btnIncr, btnSave;
	private JFormattedTextField txtBetrag;
	private Font fontTxtFields, fontCmbBoxes;
	private double flGesBetLager;
	private Connection cn = null;
	// die zweite dimension des Array hat immer eine laenge von 3
	// damit ereignissid, betrag und liqui kennzeichen aufgenommen
	// werden kann (in dieser reihenfolge)
	private String[][] daten = null, lager = null;

	Aufteilung(JFrame dialogOwner, double flGesBetrag, String strBuchText, Connection LoginCN) {
		cn = LoginCN;
		final JDialog dialog = new JDialog(dialogOwner, "Aufteilung", true);
		dialog.setSize(300, 250);
		dialog.setLayout(null);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// das Array daten wird schon mal initialisiert
		daten = new String[1][3];

		// die uebergebene Variable flGesBetrag in die in dieser
		// Klasse ueberall leesbaren Variablen flGesBetLager kopieren
		flGesBetLager = flGesBetrag;

		// schriftenfestlegungen
		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
		fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);

		// objekt festlegungen
		chkLiquiFaehig = new JCheckBox("Liquifaehig", true);
		chkLiquiFaehig.setBounds(80, 10, 110, 25);

		lblBetrag = new JLabel("Betrag");
		lblBetrag.setBounds(10, 45, 38, 25);

		txtBetrag = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		txtBetrag.setBounds(80, 45, 60, 25);
		txtBetrag.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtBetrag.setFont(fontTxtFields);
		txtBetrag.setText("0,00");
		txtBetrag.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent fe) {

			}

			public void focusGained(FocusEvent fe) {
				txtBetrag.selectAll();
			}
		});

		lblEreigniss = new JLabel("Ereigniss");
		lblEreigniss.setBounds(10, 80, 53, 25);

		cmbEreigniss = new JComboBox<String>();
		cmbEreigniss.setFont(fontCmbBoxes);
		cmbEreigniss.setBounds(80, 80, 200, 25);
		fill_cmbEreigniss();

		btnIncr = new JButton("neuer Datensatz");
		btnIncr.setBounds(10, 115, 130, 35);
		btnIncr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// falls der aufteilungssatz niedriger oder gleich dem noch aufzuteilenden
				// restbetrag ist werden die eingetragenen Werte des Formulars in den aktuell
				// freien Datensatz des Arrays (dies ist immer der hoechste) gespeichert wenn
				// nicht wird hier abgebrochen
				if (flGesBetLager < roundScale2(
						Double.valueOf(txtBetrag.getText().replace(".", "").replace(',', '.')).doubleValue())) {
					txtBetrag.requestFocus();
					return;
				} else {
					daten[daten.length - 1][0] = extractEreigId(cmbEreigniss.getSelectedItem().toString());
					daten[daten.length - 1][1] = txtBetrag.getText().replace(".", "").replace(',', '.');
					if (chkLiquiFaehig.isSelected())
						daten[daten.length - 1][2] = "TRUE";
					else
						daten[daten.length - 1][2] = "FALSE";
				}

				// verringert den im Infofeld angegebenen Betrag um den eingegebenen
				// und stellt den rest dar, dieser wird in der Variablen flGesBetLager
				// gleich mitgespeichert fuer die naechste erhoehung
				lblRestInfo.setText("noch " + String
						.valueOf(roundScale2(flGesBetLager
								- Double.valueOf(txtBetrag.getText().replace(".", "").replace(',', '.')).doubleValue()))
						.replace('.', ',') + " EUR");
				flGesBetLager = roundScale2(flGesBetLager
						- Double.valueOf(txtBetrag.getText().replace(".", "").replace(',', '.')).doubleValue());

				// sollte der Aufteilungsbetrag 0 sein (weil schon alles aufgeteilt wurde) wird
				// der plus button deaktiviert und der Focus auf den Speicherbutton gelegt
				if (flGesBetLager == 0) {
					btnSave.setEnabled(true);
					btnSave.requestFocus();
					btnIncr.setEnabled(false);
				}
				// ansonsten wird unter erhalt der bisher im Datenfeld gespeicherten
				// Daten der maximal moegliche Index um 1 erhoeht um einen weiteren
				// Datensatz aufzunehmen
				else {
					erhoehe_datenfeld();

				}

				// nun werden noch die inhalte der Textfelder geleert
				cmbEreigniss.setSelectedIndex(0);
				txtBetrag.setText("0,00");
			}
		});

		btnSave = new JButton("Speichern");
		btnSave.setBounds(150, 115, 130, 35);
		// damit nicht einfach so gespeichert werden kann
		// wird der Button ersteinmal deaktiviert
		// aktiviert wird er erst wieder wenn der gesammte aufteilungs-
		// betrag bis auf 0 aufgeteilt ist (pruefung findet
		// beim klicken auf den Button "neuer Datensatz" statt)
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

		lblRestInfo = new JLabel("noch " + String.valueOf(flGesBetLager).replace('.', ',') + " EUR");
		lblRestInfo.setBounds(10, 160, 290, 25);
		lblRestInfo.setHorizontalAlignment(JLabel.CENTER);

		lblBuchText = new JLabel("Buchungstext (tooltip)");
		lblBuchText.setBounds(10, 180, 290, 25);
		lblBuchText.setHorizontalAlignment(JLabel.CENTER);
		lblBuchText.setToolTipText(strBuchText);

		dialog.add(chkLiquiFaehig);
		dialog.add(lblBetrag);
		dialog.add(txtBetrag);
		dialog.add(lblEreigniss);
		dialog.add(cmbEreigniss);
		dialog.add(btnIncr);
		dialog.add(btnSave);
		dialog.add(lblRestInfo);
		dialog.add(lblBuchText);

		dialog.setVisible(true);
	}

	private String extractEreigId(String strEreigAusCmbBox) {
		return strEreigAusCmbBox.substring(strEreigAusCmbBox.indexOf('(') + 1, strEreigAusCmbBox.indexOf(')'));
	}

	private void erhoehe_datenfeld() {
		// erhoet das datenfeld daten um ein feld unter erhalt der bisher gespeicherten
		// daten
		int i;

		lager = new String[daten.length][3];
		// elemente des datenfeldes in das lager zwischenkopieren
		for (i = 0; i < daten.length; i++) {
			lager[i][0] = daten[i][0];
			lager[i][1] = daten[i][1];
			lager[i][2] = daten[i][2];
		}
		// datenfeld neu erzeugen mit einem zusaetzlichen hauptfeld
		// unterfelder sind ja immer drei
		daten = new String[lager.length + 1][3];
		// nun die vorher im lager datenfeld zwischengespeicherten
		// daten wieder in das hauptdtenfelddaten uebernehmen
		for (i = 0; i < lager.length; i++) {
			daten[i][0] = lager[i][0];
			daten[i][1] = lager[i][1];
			daten[i][2] = lager[i][2];
		}
		// damit de GC aufraeumen kann das lagerdatenfeld
		// auf null setzen
		lager = null;
	}

	private void fill_cmbEreigniss() {
		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT ereigniss_id, ereigniss_krzbez
				FROM kontenereignisse
				WHERE gueltig = 'TRUE'
				ORDER BY 2
				""");

		try {
			getter.beforeFirst();

			while (getter.next()) {
				cmbEreigniss.addItem(getter.getString("ereigniss_krzbez") + " (" + getter.getInt("ereigniss_id") + ")");
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}
	}

	private double roundScale2(double d) {
		return Math.round(d * 100) / 100.;
	}

	public String[][] getDaten() {
		return daten;
	}
}
