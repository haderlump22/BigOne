package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class Liqui {
	private JFrame liquiwindow;
	private JPanel pnlAbrMonat, pnlRestwert;
	private JFormattedTextField txtAbrMonat, txtNutzBetr;
	private JTextArea txtHinweis;
	private Font fontTxtFields;
	private Object[][] daten = null;
	private DatabaseConstants liquiDBC;
	private String strHinweis, strBetrag, strEreigniss;
	
	
	Liqui(DatabaseConstants DBC){
		liquiDBC = DBC;
		liquiwindow = new JFrame("Liquistatus");
		liquiwindow.setSize(400,280);
		liquiwindow.setLocation(200,200);
		liquiwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		liquiwindow.setLayout(null);
		liquiwindow.setResizable(false);
		
		//schriftenfestlegungen
		fontTxtFields = new Font("Arial",Font.PLAIN,16);
		
		pnlAbrMonat = new JPanel();
		pnlAbrMonat.setLayout(null);
		pnlAbrMonat.setBounds(30,30,150,60);
		pnlAbrMonat.setBorder(new TitledBorder("Abrechnungsmonat"));
			//inhalt fuer Panel AbrMonat erstellen
			try {
				txtAbrMonat = new JFormattedTextField(new MaskFormatter("01-##-20##"));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			txtAbrMonat.setBounds(20,25,110,25);
			txtAbrMonat.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtAbrMonat.setFont(fontTxtFields);
			txtAbrMonat.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent ke) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void keyReleased(KeyEvent ke) {
					// TODO Auto-generated method stub
					if( ke.getKeyCode() == KeyEvent.VK_ENTER) {
						txtHinweis.setText(""); //clear txtHinweis Field
						berechne();
					}
				}
				@Override
				public void keyPressed(KeyEvent ke) {
					// TODO Auto-generated method stub
					
				}
			});
		//inhalte fuer das Panel AbrMonat auf selbiges legen
		pnlAbrMonat.add(txtAbrMonat);
		
		pnlRestwert = new JPanel();
		pnlRestwert.setLayout(null);
		pnlRestwert.setBounds(30,115,150,60);
		pnlRestwert.setBorder(new TitledBorder("Monatsrestwert"));
			//inhalt fuer Panel Restwert erstellen
			txtNutzBetr = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
			txtNutzBetr.setBounds(20,25,110,25);		
			txtNutzBetr.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtNutzBetr.setFont(fontTxtFields);
			txtNutzBetr.setText("0,00");
			txtNutzBetr.addFocusListener( new FocusListener() {
				public void focusLost( FocusEvent fe ) {
		    	 
				}
				public void focusGained(FocusEvent fe) {
					txtNutzBetr.selectAll();
				}
			});
		//inhalte fuer das Panel AbrMonat auf selbiges legen
		pnlRestwert.add(txtNutzBetr);
		
		//Textfeld fuer Hinweise zum errechneten Betrag
		txtHinweis = new JTextArea();
		txtHinweis.setBounds(195, 30, 175, 205);
		txtHinweis.setBorder(BorderFactory.createEtchedBorder());
		txtHinweis.setEditable(false); //infos sollen nur vom Programm gesetzt werden
		
		liquiwindow.add(pnlAbrMonat);
		liquiwindow.add(pnlRestwert);
		liquiwindow.add(txtHinweis);
		
		liquiwindow.setVisible(true);

	}
	private void berechne() {
		double dblMtlJahrKosten, dblEin, dblAus, dblSummeFixkosten, dblNutzBetrag;
		
		//jahressparbetrag ermitteln
		dblMtlJahrKosten = monatliche_jahreskosten();
		//System.out.println("Jahrsparwert: " + dblMtlJahrKosten);
		txtHinweis.append("Jahrsparwert: " + dblMtlJahrKosten+"\n");
		
 		//die monatliche fixkosten in ein array schreiben
		monats_fixkosten();

		//einnahmen des abrechnungsmonats ermitteln
		dblEin = summiere_einnahmen();
		//System.out.println("Einnahmen: " + dblEin);
		txtHinweis.append("Einnahmen: " + dblEin+"\n");
		
		//ausgaben des abrechnungsmonats ermitteln (dabei sind die saetze mit
		//den ereignissid's der monatlichen fixausgaben ausgenommen, und auch die mit
		//der eieignissid 47 Jahresausgaben)
		dblAus = summiere_ausgaben();
		//System.out.println("Ausgaben: " + dblAus);
		txtHinweis.append("Ausgaben: " + dblAus+"\n");
		
		//die liquiditaetsfahigen teile von aufteilungsdatensaetzen zusammenaddieren
		//dabei werden einzeln haben und soll anteile zusammengerechnet
		//die ergebnisse werden dann jeweils den summen der einnahmen bzw der ausgaben
		//hinzugerechnet
		dblEin = dblEin + summiere_liqui_aus_aufteilung("h");
		dblAus = dblAus + summiere_liqui_aus_aufteilung("s");
		//System.out.println("Einnahmen(incl Aufteilung): " + dblEin);
		//System.out.println("Ausgaben(incl Aufteilung): " + dblAus);
		txtHinweis.append("Einnahmen(incl Aufteilung): " + dblEin+"\n");
		txtHinweis.append("Ausgaben(incl Aufteilung): " + dblAus+"\n");
		
		//nun wird von der differenz zwischen einnahmen und ausgaben
		//das jahressparen und alle gueltigen Monatlichen fixausgaben abgezogen
		dblSummeFixkosten = berechne_summe_fixkosten_aus_transaktionen();
		//System.out.println("Monatliche FixKost: " + dblSummeFixkosten);
		txtHinweis.append("Monatliche FixKost: " + roundScale2(dblSummeFixkosten)+"\n");
		
		//nun noch die endsumme berechnen und der textbox zuweisen
		dblNutzBetrag = roundScale2(dblEin - dblAus - dblSummeFixkosten - dblMtlJahrKosten);
		txtNutzBetr.setText(String.valueOf(dblNutzBetrag).replace(".", ","));
	}
	private double berechne_summe_fixkosten_aus_transaktionen() {
		//berechnet anhand der bisher zusammengesellten Daten
		//den nutzbaren betrag
		int iZaehler;
		double dblSumFixKosten=0;
		double dblErgSql;
		DatabaseConstants getter = new DatabaseConstants();
		
		for(iZaehler = 0; iZaehler < daten.length; iZaehler++)
		{
			getter.select("select sum(betrag) from transaktionen where ereigniss_id = " + 
		    		  daten[iZaehler][1].toString() + " and soll_haben = 's' and liqui_monat = '" + BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "';", 1);
			
			if(getter.getRowCount() > 0 && getter.getValueAt(0, 0) != null)
		    	  dblErgSql = roundScale2(Double.valueOf(getter.getValueAt(0, 0).toString()).doubleValue());
		      else
		    	  dblErgSql = 0;
			
		    //==========================================================================
		    //==========================================================================
		    
		    //wenn von dieser geplanten fixausgabe nicht der ganze geplante
		    //Betrag ausgegeben wurde wird trozdem der ganze geplante Betrag zur berechnung
		    //herrangezogen es sei denn der geplante Betrag ist nicht "hart" dann
		    //nur die bisherigen ausgaben
			//System.out.println(dblErgSql + " / " + Double.valueOf(daten[iZaehler][0].toString()).doubleValue()+":"+daten[iZaehler][2]);
			txtHinweis.append(dblErgSql + " / " + Double.valueOf(daten[iZaehler][0].toString()).doubleValue()+":"+daten[iZaehler][2]+"\n");
		    if(dblErgSql > 0 && dblErgSql <= Double.valueOf(daten[iZaehler][0].toString()).doubleValue())
		    {
		    	if(daten[iZaehler][2].toString().equals("true"))
		    		dblSumFixKosten = dblSumFixKosten + Double.valueOf(daten[iZaehler][0].toString()).doubleValue();
		    	else
		    		dblSumFixKosten = dblSumFixKosten + roundScale2(dblErgSql);
		    }
		    //sollte die summe der zu diesem ereigniss eingetragenen ausgaben groesser sein
		    //als der geplante Betrag dann dann wird dieser groessere wert zur berechnung 
		    //herangezogen
		    if(dblErgSql > Double.valueOf(daten[iZaehler][0].toString()).doubleValue())
		    {
		    	dblSumFixKosten = dblSumFixKosten + roundScale2(dblErgSql);
		    }
		    //falls fuer den eigentlich budgetierten Wert gilt "nur das was wirklich
		    //ausgegeben wurde" dann auch nur das zu der Summe der Fixkosten hinzurechen
		    //ansonsten, im fall "hart" den Budgetierten Wert
		    if(dblErgSql == 0)
		    {
		    	if(daten[iZaehler][2].toString().equals("1")) 
		    		dblSumFixKosten = dblSumFixKosten + Double.valueOf(daten[iZaehler][0].toString()).doubleValue();
		    	else
		    		dblSumFixKosten = dblSumFixKosten + 0;
		    }
		}
		//close db connection
		getter.connection_close();
		
		return dblSumFixKosten;
		
	}
	private double summiere_liqui_aus_aufteilung(String sh) {
		double dblWert;
		DatabaseConstants getter = new DatabaseConstants();
		
		getter.select("select sum(aufteilung.betrag) from transaktionen, " + 
    		  	"aufteilung where soll_haben = '" + sh + "' and liqui_monat = '" + 
    		  	BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "'" +
    		  	"and transaktionen.ereigniss_id in (52) " +
    		  	"and transaktionen.transaktions_id = aufteilung.transaktions_id " +
      			"and aufteilung.liqui = TRUE;", 1);
		
		if(getter.getRowCount() > 0 && getter.getValueAt(0, 0) != null)
			dblWert = roundScale2(Double.valueOf(""+getter.getValueAt(0, 0)).doubleValue());
	    else
	    	dblWert = 0;
		
		getter.connection_close();
		
		//System.out.println("Aufteilung("+sh+"): "+ dblWert);
		return dblWert;
		
	}
	private double summiere_ausgaben() {
		String sql = new String("select sum(betrag) from transaktionen where soll_haben " +
	    		  "= 's' and liqui_monat = '" + BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' " +
	    		  "and ereigniss_id not in (select ereigniss_id from mtlausgaben where gilt_ab <= '" + 
	    		  BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' and gilt_bis >= '" + 
	    		  BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "') and ereigniss_id not in(47,52);");
		//System.out.println(sql);
		liquiDBC.select(sql, 1);
		
		return roundScale2(Double.valueOf(liquiDBC.getValueAt(0, 0).toString()));
	}
	private double summiere_einnahmen() {
		String sql;
		
		sql = "select sum(betrag) from transaktionen " +
	    		"where soll_haben = 'h' and liqui_monat = '" + 
	    		BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' and ereigniss_id not in (52);";
	    //System.out.println(sql);
	    liquiDBC.select(sql, 1);
	    
	    return roundScale2(Double.valueOf(liquiDBC.getValueAt(0, 0).toString()));
	}
	private void monats_fixkosten() {
		String sql;
		
		sql = "select betrag, ereigniss_id, hart from mtlausgaben where gilt_ab <= '" + 
		  		BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' " + 
		  		"and gilt_bis >= '" + BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "';";
	    
	    liquiDBC.select(sql, 3);

		//datenarray der liquiDBC instanz in das lokale datenarray kopieren
	    daten = new Object[liquiDBC.getRowCount()][3];
	    
	    for(int i = 0; i < liquiDBC.getRowCount(); i++) {
	    	daten[i][0] = liquiDBC.getValueAt(i, 0);
	    	//System.out.print(daten[i][0]+"/");
	    	daten[i][1] = liquiDBC.getValueAt(i, 1);
	    	//System.out.print(daten[i][1]+"/");
	    	daten[i][2] = liquiDBC.getValueAt(i, 2);
	    	//System.out.println(daten[i][2]+"/");
	    }
	}
	private double monatliche_jahreskosten() {
		String sql;
		
		sql = "select sum(betrag) from jahresausgaben where gilt_ab <= '" + 
		  		BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' " + 
		  		"and gilt_bis >= '" + BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "';";
	    
	    liquiDBC.select(sql, 1);
	      
	    return roundScale2(Double.valueOf(liquiDBC.getValueAt(0, 0).toString()) / 12);
		
	}
	private double roundScale2( double d )
	  {
	    return Math.round( d * 100 ) / 100.;
	  }
}