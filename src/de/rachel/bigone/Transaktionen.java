package de.rachel.bigone;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.text.*;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.sql.Connection;

import java.text.DecimalFormat;
import java.text.ParseException;

public class Transaktionen {
	private static final int TANKEN = 6;
	private static final int AUFTEILUNG = 52;
	private Connection cn = null;
	private JFrame mainwindow;
	private JPanel sh, liqui, details;
	private JLabel transaktion, eurol, euror, lblDatum, lblBetrag, lblBeschreibung;
	private JLabel lblEreigniss, lblInfoFeld;
	private Font fontTop, fontTxtFields, fontCmbBoxes, fontIban;
	private JFormattedTextField txtLiquiDate, txtBetrag, txtDatum;
	private JTextArea txtBeschreibung;
	private JCheckBox chkLiqui;
	private JRadioButton soll, haben;
	private ButtonGroup grpsh;
	private JComboBox<String> Iban, cmbEreigniss;
	private JButton btnSave, btnClean;
	private String[][] datenAuft;

	Transaktionen(Connection LoginCN) {
		cn = LoginCN;
		mainwindow = new JFrame("Transaktionen");
		mainwindow.setSize(800, 500);
		mainwindow.setLocation(200, 200);
		mainwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainwindow.setLayout(null);
		mainwindow.setResizable(false);

		// fontsettings
		fontTop = new Font("Arial", Font.BOLD, 38);
		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
		fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);
		fontIban = new Font("Arial", Font.PLAIN, 14);

		// Kopf des Fensters layouten
		transaktion = new JLabel("Transaktionen", JLabel.CENTER);
		transaktion.setFont(fontTop);
		transaktion.setBounds(230, 35, 320, 38);
		// transaktion.setBorder(new EtchedBorder());

		ImageIcon imgEuro = new ImageIcon(getClass().getResource("images/Euro.png"));
		eurol = new JLabel(imgEuro, JLabel.CENTER);
		eurol.setBounds(90, 20, 81, 84);
		// eurol.setBorder(new EtchedBorder());
		euror = new JLabel(imgEuro, JLabel.CENTER);
		euror.setBounds(629, 20, 81, 84);
		// euror.setBorder(new EtchedBorder());

		// Panel fuer S/H layouten
		sh = new JPanel(new GridLayout(2, 1));
		sh.setBounds(100, 120, 200, 90);
		sh.setBorder(new TitledBorder("S/H"));
		// Optionsbuttons fuer S/H definieren, gruppieren und in das S/H Panel setzen
		// setzen
		soll = new JRadioButton("Soll");
		haben = new JRadioButton("Haben");
		soll.setSelected(true);
		grpsh = new ButtonGroup();
		grpsh.add(soll);
		grpsh.add(haben);
		// widgets auf das S/Hpanel legen
		sh.add(soll);
		sh.add(haben);

		// add Account choose and fill it with valid Account IDs (IBAN)
		Iban = new JComboBox<String>();
		Iban.setBounds(270, 77, 250, 25);
		Iban.setFont(fontIban);
		// fill it with Values
		fill_Iban();

		// Panel fuer die Liquditaetseinstellungen layouten
		liqui = new JPanel();
		liqui.setLayout(null);
		liqui.setBounds(100, 230, 200, 90);
		liqui.setBorder(new TitledBorder("Liquirelevant"));
		// Checkbox und Textfeld definieren und in das LiquiPanel setzen
		chkLiqui = new JCheckBox("Einrechenbar", true);
		chkLiqui.setBounds(10, 20, 180, 30);
		try {
			txtLiquiDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		txtLiquiDate.setBounds(45, 50, 110, 25);
		txtLiquiDate.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtLiquiDate.setFont(fontTxtFields);
		// widgets auf das liquipanel legen
		liqui.add(chkLiqui);
		liqui.add(txtLiquiDate);

		// Panel fuer die Details definieren
		details = new JPanel();
		details.setLayout(null);
		details.setBounds(320, 120, 380, 220);
		details.setBorder(new TitledBorder("Details"));
		// inhalte fuer das detailpanel definieren/formatieren und zuweisen
		lblDatum = new JLabel("Datum", JLabel.LEFT);
		lblDatum.setBounds(10, 20, 50, 25);
		lblBetrag = new JLabel("Betrag", JLabel.LEFT);
		lblBetrag.setBounds(10, 55, 50, 25);
		lblBeschreibung = new JLabel("Beschreibung", JLabel.LEFT);
		lblBeschreibung.setBounds(10, 90, 90, 25);
		lblEreigniss = new JLabel("Ereigniss", JLabel.LEFT);
		lblEreigniss.setBounds(10, 180, 70, 25);
		try {
			txtDatum = new JFormattedTextField(new MaskFormatter("##-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		txtDatum.setBounds(110, 20, 110, 25);
		txtDatum.setHorizontalAlignment(JTextField.RIGHT);
		txtDatum.setFont(fontTxtFields);
		txtDatum.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent fe) {
				// nach aenderung des Datumsfeldes den Wert in das Feld fuer das
				// liquidatum uebernehmen
				txtLiquiDate.setText("01" + txtDatum.getText().substring(2));
			}

			public void focusGained(FocusEvent fe) {
				// hier commt code hinein wenn das textfeld den focus erhalt
			}
		});
		txtDatum.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					txtBetrag.requestFocus();
				}
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});

		txtBetrag = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		txtBetrag.setBounds(110, 55, 110, 25);
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
		txtBetrag.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent ke) {
			}

			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					txtBeschreibung.requestFocus();
				}
			}

			public void keyTyped(KeyEvent ke) {
			}
		});

		txtBeschreibung = new JTextArea(3, 20);
		txtBeschreibung.setLineWrap(true);
		txtBeschreibung.setWrapStyleWord(true);
		txtBeschreibung.setBounds(110, 90, 200, 75);
		txtBeschreibung.setBorder(BorderFactory.createEtchedBorder());
		txtBeschreibung.setFont(fontTxtFields);
		txtBeschreibung.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent ke) {
			}

			public void keyPressed(KeyEvent ke) {
			}

			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_TAB && txtBeschreibung.getText().length() != 0) {
					txtBeschreibung.setText(txtBeschreibung.getText().trim());
					cmbEreigniss.requestFocus();
				}
			}
		});

		cmbEreigniss = new JComboBox<String>();
		cmbEreigniss.setBounds(110, 180, 200, 25);
		cmbEreigniss.setFont(fontCmbBoxes);
		fill_cmbEreigniss();
		// widgets auf das Detailspanel legen
		details.add(lblDatum);
		details.add(lblBetrag);
		details.add(lblBeschreibung);
		details.add(lblEreigniss);
		details.add(txtDatum);
		details.add(txtBetrag);
		details.add(txtBeschreibung);
		details.add(cmbEreigniss);

		// inhalte fuer das buttonpanel definieren/formatieren und zuweisen
		// dabei gleich den beiden buttons die ereignisse zuweisen
		btnSave = new JButton("Speichern");
		btnSave.setBounds(370, 360, 110, 55);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				SaveData();
			}
		});

		btnClean = new JButton(new ImageIcon(getClass().getResource("images/DevNull.png")));
		btnClean.setBounds(550, 360, 110, 55);
		btnClean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setSomeDefaultValues();
			}
		});

		lblInfoFeld = new JLabel("", JLabel.CENTER);
		lblInfoFeld.setBounds(100, 470, 600, 25);
		// lblInfoFeld.setBorder(new EtchedBorder());
		lblInfoFeld.setFont(fontTxtFields);

		// alle auf das hauptfenster setzen
		mainwindow.add(eurol);
		mainwindow.add(transaktion);
		mainwindow.add(euror);
		mainwindow.add(sh);
		mainwindow.add(Iban);
		mainwindow.add(liqui);
		mainwindow.add(details);
		mainwindow.add(btnSave);
		mainwindow.add(btnClean);
		mainwindow.add(lblInfoFeld);

		mainwindow.setVisible(true);

		txtDatum.requestFocus();

	}

	private void SaveData() {
		if (fuellung_pruefen()) {
			// objekt zum zwischenspeichern der Werte anlegen
			DataToSave BankStatementLine = new DataToSave();

			BankStatementLine.sh = soll_oder_haben();
			BankStatementLine.KontoId = getKontoId(Iban.getSelectedItem().toString().replace(" ", ""));

			if (BankStatementLine.KontoId == -1) {
				lblInfoFeld.setText("Fehler bei Konten!");
				return; // -programm wird abgebrochen!
			}

			BankStatementLine.Datum = BigOneTools.datum_wandeln(txtDatum.getText(), 0);

			// die wiederholung der ersetzung ist bedingt durch die formatierung
			// des textfeldes erst muss der punkt als tausendertrenner entfernt
			// werden dann das komma als dezimaltrenner in einen punkt umgewandelt
			// werden damit es dem amerikanischen zahlenformat entspricht und in die
			// mysql db passt
			BankStatementLine.Betrag = txtBetrag.getText().replace(".", "").replace(',', '.');

			BankStatementLine.Buchungstext = txtBeschreibung.getText();
			if (chkLiqui.isSelected())
				BankStatementLine.LiquiDate = BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0);
			else
				BankStatementLine.LiquiDate = "NULL";

			BankStatementLine.EreignisId = BigOneTools.extractEreigId(cmbEreigniss.getSelectedItem().toString());

			// hier werden diverse unterformulare aufgerufen um
			// detailierte informatioenen zu einer Transaktionen aufzunehmen
			switch (BankStatementLine.EreignisId) {
				case TANKEN:
					// tankwerte zur eintragung aufnehmen
					// das Programm arbeitet weiter wenn
					// dialog geschlossen wird
					TankDialog td = new TankDialog(mainwindow, txtBetrag.getText(), cn);

					BankStatementLine.KfzId = td.get_kfz_id();
					BankStatementLine.TreibstoffId = td.get_treibstoff_id();
					BankStatementLine.KilometerZahl = td.get_km();
					BankStatementLine.LiterZahl = td.get_liter();

					// nun die Daten einfuegen
					transaktionsdaten_einfuegen(BankStatementLine);
					// der funktion wird die ebend durch einfuegen des
					// transaktionsdatensatzes erzeugte transaktionsid uebergeben
					// plus dem BankStatementLine objekt
					tankdaten_einfuegen(get_max_transaktions_id(), BankStatementLine);

					break;
				case AUFTEILUNG:
					// aufteilungsdaten aufnehmen
					// das Programm arbeitet weiter wenn
					// dialog geschlossen wird
					Aufteilung auft = new Aufteilung(mainwindow, Double.valueOf(BankStatementLine.Betrag).doubleValue(),
							BankStatementLine.Buchungstext, cn);

					datenAuft = auft.getDaten();

					// nun die Daten einfuegen
					transaktionsdaten_einfuegen(BankStatementLine);
					// der funktion wird die ebend durch einfuegen des
					// transaktionsdatensatzes erzeugte transaktionsid uebergeben
					// plus dem BankStatementLine objekt
					aufteilungsdaten_einfuegen(get_max_transaktions_id(), datenAuft);

					break;
				default:
					transaktionsdaten_einfuegen(BankStatementLine);
			}
		}
		setSomeDefaultValues();
	}

	private void tankdaten_einfuegen(int transaktions_id, DataToSave BankStatementLine) {
		// fuegt die Tanktaden in die entsprechende Tabelle anhand der im
		// BankStatementLine hinterlegten werte ein
		DBTools pusher = new DBTools(cn);

		pusher.insert("""
				INSERT INTO tankdaten
				(transaktions_id, liter, km, kraftstoff_id, datum_bar, betrag_bar, kfz_id)
				VALUES
				(%d, %s, %s, %s, NULL, NULL, %s)
				""".formatted(transaktions_id, BankStatementLine.LiterZahl, BankStatementLine.KilometerZahl,
				BankStatementLine.TreibstoffId, BankStatementLine.KfzId));

	}

	private int get_max_transaktions_id() {
		// findet die aktuell groesste Transaktonsid
		Number max_transaktons_id;

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT MAX(transaktions_id)
				FROM transaktionen
				""", 1);

		if (getter.getRowCount() == 1)
			max_transaktons_id = (Number) getter.getValueAt(0, 0);
		else
			max_transaktons_id = -1;

		return max_transaktons_id.intValue();
	}

	private void setSomeDefaultValues() {
		// hier werden notwendige Werte und Inhalte des Dialogs
		// wieder auf default gesetzt
		soll.setSelected(true);
		txtBetrag.setText("0,00");
		txtBetrag.requestFocus();
		txtBeschreibung.setText("");
		lblInfoFeld.setText("");

		// chkLiqui.setSelected(true);
		// txtLiquiDate.setText(null);
		// txtDatum.setText(null);
		// cmbEreigniss.setSelectedIndex(0);
		// txtDatum.requestFocus();
	}

	private void fill_cmbEreigniss() {
		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT ereigniss_id, ereigniss_krzbez
				FROM kontenereignisse
				WHERE gueltig = TRUE
				ORDER BY 2
				""", 2);

		Object[][] cmbEreinissValues = getter.getData();

		for (Object[] cmbEreignissValue : cmbEreinissValues)
			cmbEreigniss.addItem(cmbEreignissValue[1] + " (" + cmbEreignissValue[0] + ")");
	}

	private boolean fuellung_pruefen() {

		return true;
	}

	private String soll_oder_haben() {
		if (soll.isSelected())
			return "s";
		else
			return "h";
	}

	private int getKontoId(String Iban) {
		Number KontoId;

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT konten_id
				FROM konten
				WHERE gueltig = TRUE
				AND iban = '%s'
				""".formatted(Iban), 1);

		if (getter.getRowCount() == 1)
			KontoId = (Number) getter.getValueAt(0, 0);
		else
			KontoId = -1;

		return KontoId.intValue();
	}

	private void transaktionsdaten_einfuegen(DataToSave BankStatementLine) {
		DBTools pusher = new DBTools(cn);

		String sql = """
				INSERT INTO transaktionen
				(soll_haben, konten_id, datum, betrag, buchtext, ereigniss_id, liqui_monat)
				VALUES
				('%s', %d, '%s', %s, '%s', %d
				""".formatted(BankStatementLine.sh, BankStatementLine.KontoId, BankStatementLine.Datum,
				BankStatementLine.Betrag, BankStatementLine.Buchungstext, BankStatementLine.EreignisId);

		// falls das feld fuer das Liquidatum NULL sein
		// soll darf kein hochkomma an dieser stelle
		// im sqlstatement vorkommen
		if (BankStatementLine.LiquiDate == "NULL")
			sql = sql + "," + BankStatementLine.LiquiDate + ")";
		else
			sql = sql + ",'" + BankStatementLine.LiquiDate + "')";

		pusher.insert(sql);
	}

	private void aufteilungsdaten_einfuegen(int transaktions_id, String[][] datenAuft) {
		// fuegt die aufteilungsdaten in die entsprechende Tabelle anhand der im
		// array hinterlegten werte ein
		DBTools pusher = new DBTools(cn);

		for (String[] arg : datenAuft) {
			String sql = """
					INSERT INTO aufteilung
					(transaktions_id, betrag, ereigniss_id, liqui)
					VALUES
					(%d, %s, %s, %s)
					""".formatted(transaktions_id, arg[1], arg[0], arg[2]);

			// System.out.println(sql);
			pusher.insert(sql);
		}
	}

	private void fill_Iban() {
		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT iban, bemerkung
				FROM konten
				WHERE gueltig = TRUE
				""", 2);

		Object[][] cmbKtoValues = getter.getData();

		for (Object[] cmbKtoValue : cmbKtoValues)
			Iban.addItem(BigOneTools.getIbanFormatted(cmbKtoValue[0].toString()));
	}
}
