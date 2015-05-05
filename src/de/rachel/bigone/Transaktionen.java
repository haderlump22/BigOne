package de.rachel.bigone;

//define Imports
import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.border.*;


import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;

import static de.rachel.bigone.DatabaseConstants.*;

public class Transaktionen {
	private static final int TANKEN = 6;
	private static final int AUFTEILUNG = 52;
	private JFrame mainwindow;
	private JPanel sh, bank, liqui, details;
	private JLabel transaktion, eurol, euror, lblKto, lblBLZ, lblDatum, lblBetrag, lblBeschreibung;
	private JLabel lblEreigniss, lblInfoFeld;
	private Font fontTop, fontTxtFields, fontCmbBoxes;
	private JFormattedTextField txtLiquiDate, txtBetrag, txtDatum;
	private JTextArea txtBeschreibung;
	private JCheckBox chkLiqui;
	private JRadioButton rb1, rb2;
	private ButtonGroup grpsh;
	private JComboBox cmbBLZ, cmbKto, cmbEreigniss;
	private JButton btnSave, btnClean;
	private String[][] datenAuft;
		
	Transaktionen(){
		mainwindow = new JFrame("Transaktionen");
		mainwindow.setSize(800,500);
		mainwindow.setLocation(200,200);
		mainwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainwindow.setLayout(null);
		mainwindow.setResizable(false);

		//schriftenfestlegungen
		fontTop= new Font("Arial",Font.BOLD,38);
		fontTxtFields = new Font("Arial",Font.PLAIN,16);
		fontCmbBoxes = new  Font("Arial",Font.PLAIN,16);
		
		//Kopf des Fensters layouten
		transaktion = new JLabel("Transaktionen",JLabel.CENTER);
		transaktion.setFont(fontTop);
		transaktion.setBounds(260,43,280,38);
		//transaktion.setBorder(new EtchedBorder());
		eurol = new JLabel(new ImageIcon(getClass().getResource("images/euro.gif")), JLabel.CENTER);
		eurol.setBounds(100,20,81,84);
		//eurol.setBorder(new EtchedBorder());
		euror = new JLabel(new ImageIcon(getClass().getResource("images/euro.gif")),JLabel.CENTER);
		euror.setBounds(619,20,81,84);
		//euror.setBorder(new EtchedBorder());
		
		//Panel fuer S/H layouten
		sh = new JPanel(new GridLayout(2,1));
		sh.setBounds(100,120,200,90);
		sh.setBorder(new TitledBorder("S/H"));
		//Optionsbuttons fuer S/H definieren, gruppieren und in das S/H Panel setzen setzen
		rb1 = new JRadioButton( "Soll" );
		rb2 = new JRadioButton( "Haben" );
		rb1.setSelected(true);
		grpsh = new ButtonGroup();
		grpsh.add(rb1);
		grpsh.add(rb2);
		//widgets auf das S/Hpanel legen
		sh.add(rb1);
		sh.add(rb2);
		
		//Panels fuer die Bankverbindung layouten
		bank = new JPanel();
		bank.setLayout(null);
		bank.setBounds(100,230,200,90);
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
		cmbBLZ.setSelectedIndex(1);
				
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
		
		//Panel fuer die Liquditaetseinstellungen layouten
		liqui = new JPanel();
		liqui.setLayout(null);
		liqui.setBounds(100,340,200,90);
		liqui.setBorder(new TitledBorder("Liquirelevant"));
		//Checkbox und Textfeld definieren und in das LiquiPanel setzen 
		chkLiqui = new JCheckBox("Einrechenbar",true);
		chkLiqui.setBounds(10,20,180,30);
		try {
			txtLiquiDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
		} catch (ParseException e1) {
			// TODO Automatisch erstellter Catch-Block
			e1.printStackTrace();
		}
		txtLiquiDate.setBounds(45,50,110,25);
		txtLiquiDate.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtLiquiDate.setFont(fontTxtFields);
		//widgets auf das liquipanel legen
		liqui.add(chkLiqui);
		liqui.add(txtLiquiDate);
		
		//Panel fuer die Details definieren
		details = new JPanel();
		details.setLayout(null);
		details.setBounds(320,120,380,220);
		details.setBorder(new TitledBorder("Details"));
			//inhalte fuer das detailpanel definieren/formatieren und zuweisen
			lblDatum = new JLabel("Datum",JLabel.LEFT);
			lblDatum.setBounds(10,20,50,25);
			lblBetrag = new JLabel("Betrag",JLabel.LEFT);
			lblBetrag.setBounds(10,55,50,25);
			lblBeschreibung = new JLabel("Beschreibung",JLabel.LEFT);
			lblBeschreibung.setBounds(10,90,90,25);
			lblEreigniss = new JLabel("Ereigniss",JLabel.LEFT);
			lblEreigniss.setBounds(10,180,70,25);
			try {
				txtDatum = new JFormattedTextField(new MaskFormatter("##-##-20##"));
			} catch (ParseException e1) {
				// TODO Automatisch erstellter Catch-Block
				e1.printStackTrace();
			}
			txtDatum.setBounds(110,20,110,25);
			txtDatum.setHorizontalAlignment(JTextField.RIGHT);
			txtDatum.setFont(fontTxtFields);
			txtDatum.addFocusListener( new FocusListener() {
			      public void focusLost( FocusEvent fe ) {
			    	  //nach aenderung des Datumsfeldes den Wert in das Feld fuer das
			    	  //liquidatum uebernehmen
			    	  txtLiquiDate.setText("01" + txtDatum.getText().substring(2));
			        }
			      public void focusGained( FocusEvent fe) {
			    	  //hier commt code hinein wenn das textfeld den focus erhalt

			      }
			 } );
			txtDatum.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
					// TODO Automatisch erstellter Methoden-Stub
					
				}
				public void keyReleased(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
					if( ke.getKeyCode() == KeyEvent.VK_ENTER) {
						txtBetrag.requestFocus();
					}
				}
				public void keyTyped(KeyEvent arg0) {
					// TODO Automatisch erstellter Methoden-Stub
				}
			});
			
			txtBetrag = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
			txtBetrag.setBounds(110,55,110,25);		
			txtBetrag.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtBetrag.setFont(fontTxtFields);
			txtBetrag.setText("0,00");
			txtBetrag.addFocusListener( new FocusListener() {
				public void focusLost( FocusEvent fe ) {
			    	 
				}
				public void focusGained(FocusEvent fe) {
					txtBetrag.selectAll();
				}
			});
			txtBetrag.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
					
				}
				public void keyReleased(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
					if( ke.getKeyCode() == KeyEvent.VK_ENTER) {
						txtBeschreibung.requestFocus();
					}
				}
				public void keyTyped(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
				}
			});
			
			txtBeschreibung = new JTextArea(3,20);
			txtBeschreibung.setLineWrap(true); //automatischer Zeilenumbruch am Zeilenende
			txtBeschreibung.setBounds(110,90,200,75);
			txtBeschreibung.setBorder(BorderFactory.createEtchedBorder());
			txtBeschreibung.setFont(fontTxtFields);
			txtBeschreibung.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
				}
				public void keyPressed(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
				}
				public void keyReleased(KeyEvent ke) {
					// TODO Automatisch erstellter Methoden-Stub
					if( ke.getKeyCode() == KeyEvent.VK_TAB && txtBeschreibung.getText().length() != 0) {
						txtBeschreibung.setText(txtBeschreibung.getText().trim());
						cmbEreigniss.requestFocus();
					}
				}			
			});
			
			cmbEreigniss = new JComboBox();
			cmbEreigniss.setBounds(110,180,200,25);
			cmbEreigniss.setFont(fontCmbBoxes);
		fill_cmbEreigniss();
		//widgets auf das Detailspanel legen
		details.add(lblDatum);
		details.add(lblBetrag);
		details.add(lblBeschreibung);
		details.add(lblEreigniss);
		details.add(txtDatum);
		details.add(txtBetrag);
		details.add(txtBeschreibung);
		details.add(cmbEreigniss);
		
		//inhalte fuer das buttonpanel definieren/formatieren und zuweisen
		//dabei gleich den beiden buttons die ereignisse zuweisen
		btnSave = new JButton("Speichern");
		btnSave.setBounds(370,360,110,55);
		btnSave.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent ae){ 
                SaveData();
            }
        });
		
		btnClean = new JButton(new ImageIcon(getClass().getResource("images/korb.gif")));
		btnClean.setBounds(550,360,110,55);
		btnClean.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent ae){ 
                CleanAll();
            }
        });
		
		lblInfoFeld = new JLabel("",JLabel.CENTER);
		lblInfoFeld.setBounds(100,470,600,25);
		//lblInfoFeld.setBorder(new EtchedBorder());
		lblInfoFeld.setFont(fontTxtFields);
		
		
		//alle auf das hauptfenster setzen
		mainwindow.add(eurol);
	    mainwindow.add(transaktion); 
	    mainwindow.add(euror);
	    mainwindow.add(sh);
	    mainwindow.add(bank);
	    mainwindow.add(liqui);
	    mainwindow.add(details);
	    mainwindow.add(btnSave);
	    mainwindow.add(btnClean);
	    mainwindow.add(lblInfoFeld);
	    
	    
		mainwindow.setVisible(true);
		
		txtDatum.requestFocus();
		
	}
	private void SaveData() {
		if(fuellung_pruefen()) {
			//objekt zum zwischenspeichern der Werte anlegen
			DataToSave lager = new DataToSave();
			
			lager.sh = soll_oder_haben();
			lager.iKontoId = konto_id_finden(getBLZ(cmbBLZ.getSelectedItem().toString()),getKto(cmbKto.getSelectedItem().toString()));
						 
			if (lager.iKontoId == -1) 
			{
			    lblInfoFeld.setText("Fehler bei Konten!");
				return;    //-programm wird abgebrochen!
			}        
			
			 lager.strDatum = BigOneTools.datum_wandeln(txtDatum.getText(),0);
			 
			 //die wiederholung der ersetzung ist bedingt durch die formatierung
			 //des textfeldes erst muss der punkt als tausendertrenner entfernt 
			 //werden dann das komma als dezimaltrenner in einen punkt umgewandelt
			 //werden damit es dem amerikanischen zahlenformat entspricht und in die
			 //mysql db passt
			 lager.strBetrag = txtBetrag.getText().replace(".","").replace(',','.');
			 			 
			 lager.strBuchtext = txtBeschreibung.getText();
             if (chkLiqui.isSelected())
                lager.strLiquiDate = BigOneTools.datum_wandeln(txtLiquiDate.getText(),0);
             else
             	lager.strLiquiDate = "NULL";
                          
             lager.iEreigId = BigOneTools.extractEreigId(cmbEreigniss.getSelectedItem().toString());
             
             //hier werden diverse unterformulare aufgerufen um
             //detailierte informatioenen zu einer Transaktionen aufzunehmen
             switch( lager.iEreigId) {
             case TANKEN:
                 //tankwerte zur eintragung aufnehmen
                 //das Programm arbeitet weiter wenn
                 //dialog geschlossen wird
                 TankDialog td = new TankDialog(mainwindow, txtBetrag.getText());
                 
                 lager.strKfzId = td.get_kfz_id();
                 lager.strTreibstoffId = td.get_treibstoff_id();
                 lager.strKm = td.get_km();
                 lager.strLiter = td.get_liter();
                 
                 //nun die Daten einfuegen
                 transaktionsdaten_einfuegen(lager);
                 //der funktion wird die ebend durch einfuegen des
                 //transaktionsdatensatzes erzeugte transaktionsid uebergeben
                 //plus dem lager objekt
                 tankdaten_einfuegen(get_max_transaktions_id(), lager);
                 
                 break;
             case AUFTEILUNG:
            	 //aufteilungsdaten aufnehmen
            	 //das Programm arbeitet weiter wenn
                 //dialog geschlossen wird
            	 Aufteilung auft = new Aufteilung(mainwindow, Double.valueOf(lager.strBetrag).doubleValue());
            	 
            	 datenAuft = auft.getDaten();
            	 
            	 //nun die Daten einfuegen
                 transaktionsdaten_einfuegen(lager);
                 //der funktion wird die ebend durch einfuegen des
                 //transaktionsdatensatzes erzeugte transaktionsid uebergeben
                 //plus dem lager objekt
                 aufteilungsdaten_einfuegen(get_max_transaktions_id(),datenAuft);
            	 
                 break;
             default: 
            	 transaktionsdaten_einfuegen(lager);
             }
		}
		CleanAll();
	}
	private void tankdaten_einfuegen(int transaktions_id, DataToSave lager) {
		//fuegt die Tanktaden in die entsprechende Tabelle anhand der im
		//lager hinterlegten werte ein
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
	      String sql = "INSERT INTO tankdaten " +
	      			   "( transaktions_id, liter, km, kraftstoff_id, datum_bar, betrag_bar, kfz_id) " +
	      			   "VALUES " +
	      			   "(" + transaktions_id + ", " +
	      			   lager.strLiter + ", " +
	      			   lager.strKm + ", " +
	      			   lager.strTreibstoffId + ", " +
	      			   "NULL, " +
	      			   "NULL, " +
	      			   lager.strKfzId + ");";
	      
	      stmt.executeUpdate(sql);
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
	private int get_max_transaktions_id() {
		//findet die aktuell groesste Transaktonsid
		int max_transaktons_id;
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
	      String sql = "SELECT max(transaktions_id) from transaktionen;";
	      ResultSet rs = stmt.executeQuery(sql);
	      rs.last();
	      if(rs.getRow() == 1)
	    	  max_transaktons_id = rs.getInt(1);
	      else
	    	  max_transaktons_id = -1;
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
	    return max_transaktons_id;
	}
	private void CleanAll() {
		//hier werden alle Werte und Inhalte des Dialogs wieder 
		//auf die Startwerte zurueckgesetzt
		rb1.setSelected(true);
		chkLiqui.setSelected(true);
		txtLiquiDate.setText(null);
		txtDatum.setText(null);
		txtBetrag.setText("0,00");
		txtBeschreibung.setText("");
		lblInfoFeld.setText("");
		cmbEreigniss.setSelectedIndex(0);
		cmbBLZ.setSelectedIndex(1);
		txtDatum.requestFocus();
	}
	private void fill_cmbEreigniss() {
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
	      ResultSet rs = stmt.executeQuery( "SELECT ereigniss_id, ereigniss_krzbez FROM kontenereignisse order by 2;" );
	      while ( rs.next() )
	        cmbEreigniss.addItem(rs.getString(2) + " (" + rs.getInt(1)+")");
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
	private boolean fuellung_pruefen() {
		
		return true;
	}
	private String soll_oder_haben() {
		if(rb1.isSelected())
			return "s";
		else
			return "h";
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
	private void transaktionsdaten_einfuegen(DataToSave lager) {
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
	      String sql = "INSERT INTO transaktionen " +
	      			   "( soll_haben, konten_id, datum, betrag, buchtext, ereigniss_id, liqui_monat) " +
	      			   "VALUES " +
	      			   "('" + lager.sh + "', " +
	      			   lager.iKontoId + ", '" +
	      			   lager.strDatum + "', " +
	      			   lager.strBetrag + ", '" +
	      			   lager.strBuchtext + "', " +
	      			   lager.iEreigId + ", ";
	      			   
	      			   //falls das feld fuer das Liquidatum NULL sein
	      			   //soll darf kein hochkomma an dieser stelle 
	      			   //im sqlstatement vorkommen
	      			   if(lager.strLiquiDate == "NULL")
	      				   sql = sql + lager.strLiquiDate + ");";
	      			   else
	      				   sql = sql + "'" + lager.strLiquiDate + "');";
	      
	      stmt.executeUpdate(sql);
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
	private void aufteilungsdaten_einfuegen(int transaktions_id, String[][] datenAuft) {
		//fuegt die aufteilungsdaten in die entsprechende Tabelle anhand der im
		//array hinterlegten werte ein
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
	      for(String[] arg : datenAuft) {
		      String sql = "INSERT INTO aufteilung " +
		      			   "( transaktions_id, betrag, ereigniss_id, liqui) " +
		      			   "VALUES " +
		      			   "(" + transaktions_id + ", " +
		      			   arg[1] + ", " +
		      			   arg[0] + ", " +
		      			   arg[2] + ");";
		      //System.out.println(sql);
		      stmt.executeUpdate(sql);
	      }
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
}
