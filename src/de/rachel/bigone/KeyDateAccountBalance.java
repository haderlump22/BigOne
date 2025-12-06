package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class KeyDateAccountBalance {
	private Connection cn = null;
	private int iKontoId;
	private JFrame kdab;
	private JPanel bank, date, amount, taxex;
	private JLabel lblKto, lblBLZ;
	private JComboBox<String> cmbBLZ, cmbKto;
	private JFormattedTextField txtDate, txtAmount, txtTaxEx;
	private String sDate;
	private Font fontTxtFields, fontCmbBoxes, fontAmount;

	KeyDateAccountBalance(Connection LoginCN){
		cn = LoginCN;
		kdab = new JFrame("Kontostand");
		kdab.setSize(400,300);
		kdab.setLocation(200,200);
		kdab.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		kdab.setLayout(null);
		kdab.setResizable(false);

		//schriftenfestlegungen
		fontTxtFields = new Font("Arial",Font.PLAIN,16);
		fontCmbBoxes = new  Font("Arial",Font.PLAIN,16);
		fontAmount = new  Font("Arial",Font.BOLD,16);

		//Panels fuer die Bankverbindung layouten
		bank = new JPanel();
		bank.setLayout(null);
		bank.setBounds(20,30,200,90);
		bank.setBorder(new TitledBorder("Bankverbingung"));
		//die Comboboxen und Bezeichnungen fuer die Bankverbindungen definieren und
		//mitels 2 hilfspanels in ein unterpanel namens bank setzen
		lblBLZ = new JLabel("BLZ");
		lblBLZ.setBounds(10,20,30,25);
		lblKto = new JLabel("Kto");
		lblKto.setBounds(10,55,30,25);
		cmbBLZ = new JComboBox<String>();
		cmbBLZ.setBounds(40,20,150,25);
		cmbBLZ.setFont(fontCmbBoxes);
		fill_cmbBank();
		cmbBLZ.setSelectedIndex(3);

		//ereigniss fuer die fuellung der Kontocombobox in
		//abhaengigkeit der BLZ combobox festlegen
		cmbBLZ.addItemListener( new ItemListener() {
		      public void itemStateChanged( ItemEvent e ) {
		    	  if(e.getStateChange() == ItemEvent.SELECTED) {
		    		  cmbKto.removeAllItems();
		    		  fill_cmbKto(cmbBLZ.getSelectedItem().toString().substring(0,cmbBLZ.getSelectedItem().toString().indexOf(' ')));
		    	  }
		        }
		 } );

		cmbKto = new JComboBox<String>();
		cmbKto.setBounds(40,55,150,25);
		cmbKto.setFont(fontCmbBoxes);
		//widgets auf das bankpanel legen
		bank.add(lblBLZ);
		bank.add(cmbBLZ);
		bank.add(lblKto);
		bank.add(cmbKto);
		//damit die Konten auch schon beim ersten aufruf des Formulars gefuellt werden
		//und nicht erst wenn man das erste mal die BLZ combobox betaetigt
		fill_cmbKto(cmbBLZ.getSelectedItem().toString().substring(0,cmbBLZ.getSelectedItem().toString().indexOf(' ')));

		//Panel fuer das Datumsfeld
		date = new JPanel();
		date.setLayout(null);
		date.setBounds(230,30,140,50);
		date.setBorder(new TitledBorder("Datum/Stichtag"));

			//das Datumsfeld fuer das Panel date
			try {
				txtDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			txtDate.setBounds(10,18,120,25);
			txtDate.setHorizontalAlignment(JTextField.RIGHT);
			txtDate.setFont(fontTxtFields);
			txtDate.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
				}
				public void keyReleased(KeyEvent ke) {
					if( ke.getKeyCode() == KeyEvent.VK_ENTER) {
						iKontoId = konto_id_finden(getBLZ(cmbBLZ.getSelectedItem().toString()),getKto(cmbKto.getSelectedItem().toString()));
						sDate = BigOneTools.datum_wandeln(txtDate.getText(),0);
						calculate_ab(iKontoId,sDate);
						txtTaxEx.setText(String.valueOf(get_taxex(iKontoId)).replace(".", ","));
					}
				}
				public void keyTyped(KeyEvent arg0) {
				}
			});

		date.add(txtDate);

		//Panels fuer den Kontostand layouten
		amount = new JPanel();
		amount.setLayout(null);
		amount.setBounds(20,140,200,50);
		amount.setBorder(new TitledBorder("Kontostand"));
			//ergebnisstextfeld festlegen
			txtAmount = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
			txtAmount.setBounds(20,18,160,25);
			txtAmount.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtAmount.setFont(fontAmount);
			txtAmount.setText("0,00");
		amount.add(txtAmount);

		//Panel fuer den Freistellungsauftrag
		taxex = new JPanel();
		taxex.setLayout(null);
		taxex.setBounds(220,140,160,50);
		taxex.setBorder(new TitledBorder("akt. Freistellungsauftrag"));
			//ergebnisstextfeld festlegen
			txtTaxEx = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
			txtTaxEx.setBounds(20,18,120,25);
			txtTaxEx.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtTaxEx.setFont(fontAmount);
			txtTaxEx.setText("0,00");
		taxex.add(txtTaxEx);

		kdab.add(bank);
		kdab.add(date);
		kdab.add(amount);
		kdab.add(taxex);

		kdab.setVisible(true);
		txtDate.requestFocus();
	}

	private void fill_cmbBank() {
		DBTools getter = new DBTools(cn);
	    getter.select("""
				SELECT blz, kreditinstitut
				FROM kreditinstitut
				WHERE gilt_bis IS NULL ORDER BY 1
				""",2);

	    Object[][] cmbBankValues = getter.getData();

	    for(Object[] cmbBankValue : cmbBankValues)
	    	cmbBLZ.addItem(cmbBankValue[0] + " (" + cmbBankValue[1] + ")");

	      //Standartauswahl auf die Postbank legen


	}

	private void fill_cmbKto(String strAuswahl) {
		DBTools getter = new DBTools(cn);


	    getter.select("""
				SELECT konten.kontonummer, personen.vorname
	      		FROM konten, kreditinstitut, personen
	      		WHERE konten.kreditinstitut_id = kreditinstitut.kreditinstitut_id
	      		AND kreditinstitut.blz = '%s'
	      		AND konten.personen_id = personen.personen_id
				""".formatted(strAuswahl),2);

	    Object[][] cmbKtoValues = getter.getData();

	    for(Object[] cmbKtoValue : cmbKtoValues)
	        cmbKto.addItem(cmbKtoValue[0]+ " (" +cmbKtoValue[1]+ ")");
	}

	private void calculate_ab(int QueryiKontoId, String QuerysDate) {
		Number fHaben, fSoll, fErg;

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT SUM(betrag)
				FROM transaktionen
				WHERE konten_id = %d
				AND soll_haben = 'h'
				AND datum <= '%s'
				AND ereigniss_id NOT IN (94)
				""".formatted(QueryiKontoId, QuerysDate), 1);

		fHaben = (Number) getter.getValueAt(0, 0);

		getter.select("""
				SELECT SUM(betrag)
				FROM transaktionen
				WHERE konten_id = %d
				AND soll_haben = 's'
				AND datum <= '%s'
				AND ereigniss_id NOT IN (94)
				""".formatted(QueryiKontoId, QuerysDate),1);

	    fSoll = (Number) getter.getValueAt(0, 0);


		//ergebniss berechnen und in das Textfeld einfuegen
		fErg = fHaben.floatValue() - fSoll.floatValue();
		txtAmount.setText(fErg.toString().replace('.',','));

		//dieses focusieren und wegnehmen des Focus ist dafuer das
		//das oben festgelgte Format des Textfeldes wirksam wird
		txtAmount.requestFocus();
		txtDate.requestFocus();
	}

	private int konto_id_finden(String strBLZ, String strKto) {
		Integer intKontoId;

		DBTools getter = new DBTools(cn);

	    getter.select("""
				SELECT ko.konten_id
				FROM kreditinstitut kr, konten ko
				WHERE kr.blz = '%s'
				AND kr.gilt_bis IS NULL
				AND ko.kreditinstitut_id = kr.kreditinstitut_id
				AND ko.kontonummer = '%s'
				""".formatted(strBLZ, strKto),1);

	    if(getter.getRowCount() == 1)
	    	intKontoId = (Integer) getter.getValueAt(0, 0);
	    else
	    	intKontoId = -1;

	    return intKontoId;
	}
	private String getBLZ(String strBLZroh) {
		return strBLZroh.substring(0,strBLZroh.indexOf(' '));
	}
	private String getKto(String strKtoroh) {
		return strKtoroh.substring(0,strKtoroh.indexOf(' '));
	}
	private double get_taxex(int QueryiKontoId) {
		/*
		 * ermittelt aktuell gueltigen Freistellungsauftrag des mittels der
		 * KontenID uebergebenen Kontenkennung
		 */
		Number dblWert=0;

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT f.betrag
				FROM freistellungsauftraege f
				WHERE f.gilt_bis IS NULL
				AND f.kreditinstitut_id =
					(
					SELECT  k.kreditinstitut_id
					FROM konten k
					WHERE k.konten_id = %d
					)
				AND f.personen_id =
					(
					SELECT k.personen_id
					FROM konten k
					WHERE k.konten_id = %d
					)
				""".formatted(QueryiKontoId, QueryiKontoId),1);

	    if(getter.getRowCount() == 1)
	    	dblWert = (Number) getter.getValueAt(0, 0);
	    else
	    	dblWert = 0;

	    return dblWert.doubleValue();
	}
}
