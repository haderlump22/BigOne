package de.rachel.bigone;

import static de.rachel.bigone.DatabaseConstants.DRIVER;
import static de.rachel.bigone.DatabaseConstants.PASS;
import static de.rachel.bigone.DatabaseConstants.URL;
import static de.rachel.bigone.DatabaseConstants.USER;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private int iKontoId;
	private JFrame kdab;
	private JPanel bank, date, amount, taxex;
	private JLabel lblKto, lblBLZ;
	private JComboBox cmbBLZ, cmbKto;
	private JFormattedTextField txtDate, txtAmount, txtTaxEx;
	private String sDate;
	private Font fontTxtFields, fontCmbBoxes, fontAmount;
	
	KeyDateAccountBalance(){
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
		cmbBLZ = new JComboBox();
		cmbBLZ.setBounds(40,20,150,25);
		cmbBLZ.setFont(fontCmbBoxes);
		fill_cmbBank();
		cmbBLZ.setSelectedIndex(3);
				
		//ereigniss fuer die fuellung der Kontocombobox in 
		//abhaengigkeit der BLZ combobox festlegen
		cmbBLZ.addItemListener( new ItemListener() {
		      public void itemStateChanged( ItemEvent e ) {
		    	  if(e.getStateChange() == 1) {
		    		  cmbKto.removeAllItems();
		    		  fill_cmbKto(cmbBLZ.getSelectedItem().toString().substring(0,cmbBLZ.getSelectedItem().toString().indexOf(' ')));
		    	  }
		        }
		 } );
		
		cmbKto = new JComboBox();
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
				// TODO Automatisch erstellter Catch-Block
				e1.printStackTrace();
			}
			txtDate.setBounds(10,18,120,25);
			txtDate.setHorizontalAlignment(JTextField.RIGHT);
			txtDate.setFont(fontTxtFields);
			txtDate.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
					// TODO Automatisch erstellter Methoden-Stub
					
				}
				public void keyReleased(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
					if( ke.getKeyCode() == KeyEvent.VK_ENTER) {
						iKontoId = konto_id_finden(getBLZ(cmbBLZ.getSelectedItem().toString()),getKto(cmbKto.getSelectedItem().toString()));
						sDate = BigOneTools.datum_wandeln(txtDate.getText(),0);
						calculate_ab(iKontoId,sDate);
						txtTaxEx.setText(String.valueOf(get_taxex(iKontoId)).replace(".", ","));
					}
				}
				public void keyTyped(KeyEvent arg0) {
					// TODO Automatisch erstellter Methoden-Stub
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
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT blz, kreditinstitut FROM kreditinstitut where gilt_bis IS NULL order by 1;" );
	      while ( rs.next() )
	        cmbBLZ.addItem(rs.getString(1) + " (" + rs.getString(2) + ")");
	      rs.close();
	      stmt.close();
	      
	      //Standartauswahl auf die Postbank legen
	      
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
	}
	private void fill_cmbKto(String strAuswahl) {
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT konten.kontonummer, personen.vorname " +
	      		"FROM konten, kreditinstitut, personen " +
	      		"where konten.kreditinstitut_id = kreditinstitut.kreditinstitut_id " +
	      		"and kreditinstitut.blz = '"+ strAuswahl +"' " +
	      		"and konten.personen_id = personen.personen_id;" );
	      while ( rs.next() )
	        cmbKto.addItem(rs.getString(1)+ " (" +rs.getString(2)+ ")");
	      rs.close();
	      stmt.close();
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
	   
	}
	private void calculate_ab(int QueryiKontoId, String QuerysDate) {
		Float fHaben, fSoll, fErg;
		
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	      ResultSet rs = stmt.executeQuery("SELECT sum(betrag) FROM transaktionen where konten_id = " + 
	    		  QueryiKontoId + " and soll_haben = 'h' and datum <= '" + QuerysDate + "'"+
	    		  " and ereigniss_id not in (94);");
	      rs.last();
	      fHaben = rs.getFloat(1);
	      
	      rs = stmt.executeQuery("SELECT sum(betrag) FROM transaktionen where konten_id = " + 
	    		  QueryiKontoId + " and soll_haben = 's' and datum <= '" + QuerysDate + "'" +
	    		  " and ereigniss_id not in (94);");
	      rs.last();
	      fSoll = rs.getFloat(1);
	      	
	      rs.close();
	      stmt.close();
	      
	      //ergebniss berechnen und in das Textfeld einfuegen
	      fErg = fHaben - fSoll;
	      txtAmount.setText(fErg.toString().replace('.',','));
	      //dieses focusieren und wegnehmen des Focus ist dafuer das
	      //das oben festgelgte Format des Textfeldes wirksam wird
	      txtAmount.requestFocus();
	      txtDate.requestFocus();
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
	}
	private int konto_id_finden(String strBLZ, String strKto) {
		int intKontoId;
		
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return -1;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	      ResultSet rs = stmt.executeQuery( "SELECT ko.konten_id FROM kreditinstitut kr, konten ko " +
	    		  "where kr.blz = '" + strBLZ + "' " +
	    		  "and kr.gilt_bis is NULL " +
	    		  "and ko.kreditinstitut_id = kr.kreditinstitut_id " +
	    		  "and ko.kontonummer = '" + strKto + "' ;" );
	      rs.last();
	      if(rs.getRow() == 1)
	    	  intKontoId = rs.getInt(1);
	      else
	    	  intKontoId = -1;
	      
	      rs.close();
	      stmt.close();
	            
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return -1;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }		
	    return intKontoId;
	}
	private String getBLZ(String strBLZroh) {
		return strBLZroh.substring(0,strBLZroh.indexOf(' '));
	}
	private String getKto(String strKtoroh) {
		return strKtoroh.substring(0,strKtoroh.indexOf(' '));
	}
	private double get_taxex(int QueryiKontoId) {
		double dblWert=0;
		
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return 0;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	      ResultSet rs = stmt.executeQuery( "SELECT f.betrag FROM freistellungsauftraege f " +
	    		  							"WHERE f.gilt_bis is null " +
	    		  							"AND f.kreditinstitut_id=(select  k.kreditinstitut_id from konten k where k.konten_id = "+QueryiKontoId+") " +
	      									"AND f.personen_id =(select k.personen_id from konten k where k.konten_id = "+QueryiKontoId+");");
	      
	      while (rs.next()) {
	      	rs.last();
		    if(rs.getDouble(1) != 0)
		    	dblWert = rs.getDouble(1);
		    else
		    	dblWert = 0;
	      }
	      rs.close();
	      stmt.close();
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return 0;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
	    
	    return dblWert;
	}
}
