package de.rachel.bigone.Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.BigOneTools;

public class RACTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -2431676313753205738L;
	private String strDateiName;
	private String[] columnName = new String[]{"Wertstellung","s/h","Betrag","Buchungshinweist","Empfänger","LiquiMon","Ereigniss"};
	private String[][] daten;
	private String[][] strLager;
	
	public RACTableModel(String strWert, int iZeilen){
		strDateiName = strWert;
		daten = lese_werte(strDateiName, iZeilen);
	}
	public int getColumnCount() {
		return columnName.length;
	}
	public int getRowCount() {
		return daten.length;
	}
	public String getColumnName(int col) {
		return columnName[col];
	}
	public Object getValueAt(int row, int col) {
		return daten[row][col];
	}
	public boolean isCellEditable(int row, int col){
		if ( col == 3 || col == 5 || col == 6 || col == 4)
			return true;
		else
			return false;
	}
	public void setValueAt(Object value, int row, int col) {
		if(value != null)
			daten[row][col] = value.toString();
		else
			daten[row][col] = null;
        fireTableCellUpdated(row, col);
    }
	private String[][] lese_werte(String strDateiName, int iZeilen) {
		/*
		 * liest bestimmte felder einer Text datei ein ein Array
		 * ein. Dabei wird ab zeile 9 begonnen(erste Zeile ist die nullte!!!)
		 * , weil bei der Postbank ab hier die datensaetze anfangen
		 * - ab ende 2011 hat die postbank ihr onlinebanking umgestellt
		 *   dabei ist jetzt ein csv format im einsatz in dem ab zeile 10 die 
		 *   datensaetze beginnen
		 * - ausserdem werden betraege jetzt mit euro symbol dargestellt 
		 *   zeichensatz ist hier windows-1252
		 * - ab JUN 2016 wurde das EUR Symbol vor den Betrag gestellt
		 * - ab NOV 2016 wurde das EUR Symbol wieder hinter den Betrag gestellt ARSCHLOECHER!!!
		 */
		
   		int iAktZeile = 0;
		String strLager[][] = null;
		strLager = new String[iZeilen - 9][7];
		String[] strZeilenTeile;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(strDateiName));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				iAktZeile++; //zeilenzaehler
				
				if(iAktZeile >= 10) {
					strZeilenTeile = zeile.split(";");
					for (int iFeld = 0; iFeld < strZeilenTeile.length; iFeld++) {
						switch (iFeld) {
						case 1://Wertstellung
							strLager[iAktZeile - 10][0] = BigOneTools.datum_wandeln(strZeilenTeile[iFeld].replace("\"",""),0);
							strLager[iAktZeile - 10][5] = BigOneTools.datum_wandeln("01" + strZeilenTeile[iFeld].replace("\"","").substring(2),0);
							break;
						case 3://Buchungshinweis
							strLager[iAktZeile - 10][3] = strZeilenTeile[iFeld].replace("\"","");
							break;
						case 5://Empfaenger
							if(strZeilenTeile[iFeld].isEmpty())
								strLager[iAktZeile - 10][4] = "LEER";
							else
								strLager[iAktZeile - 10][4] = strZeilenTeile[iFeld].replace("\"","");
							break;
						case 6://Betrag
							if(strZeilenTeile[iFeld].replace("\"","").substring(0,1).charAt(0) == '-') {
								//eurosymbol mit leerzeichen vor betrag
								//strLager[iAktZeile - 10][2] = strZeilenTeile[iFeld].replace("\"","").substring(3).replace(".","").replace(',', '.'); //wert vom syntax schon fuer mysql db vorbereiten
								
								//eurosymbol mit leerzeichen hinter dem betrag
								//das letzte substring ist dafuer das das eurosymbol und das leerzeichen am ende des betrages verschwinden
								strLager[iAktZeile - 10][2] = strZeilenTeile[iFeld].replace("\"","").replace(".","").replace(',', '.').substring(1, strZeilenTeile[iFeld].length() - 4); //wert vom syntax schon fuer mysql db vorbereiten
								strLager[iAktZeile - 10][1] = "s";
							}
							else {
								//eurosymbol mit leerzeichen hinter dem betrag
								strLager[iAktZeile - 10][2] = strZeilenTeile[iFeld].replace("\"","").replace(".","").replace(',', '.').substring(0, strZeilenTeile[iFeld].length() - 4);
								
								//eurosymbol mit leerzeichen vor betrag
								//strLager[iAktZeile - 10][2] = strZeilenTeile[iFeld].replace("\"","").substring(2).replace(".","").replace(',', '.');
								strLager[iAktZeile - 10][1] = "h";
							}
							break;	
						}
					}
					//am ende der verarbeitung einer Zeile wird das ereigniss fest auf
					//HaushGeld (46) gesetzt
					strLager[iAktZeile - 10][6] = "HaushGeld (46)";			    }
				
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strLager;
	}
	public void removeRow(int iZeile) {
		//mit dieser methode wird die  
		//gewaehlte Zeile aus dem Array entfernt
		if(iZeile >= 0 && iZeile <= daten.length) {
			//lagerarray mit der selben groesse wie das orginal erzeugen
			strLager = new String[daten.length][7];
			
			//alle daten aus orginalarray in das lager kopieren
			System.arraycopy(daten, 0, strLager, 0, daten.length);
			
			//daten array neu dimensionieren aber mit einer zeile weniger
			daten = new String[strLager.length - 1][7];
			
			//nun die ersten zeilen bis zu der zu loeschenden in das
			//redimensionierte orginalarray kopieren
			//da die zeilen zahlung bei 0 beginnt zeigt iZeile nicht nur
			//den index der zu loeschenden Zeile an sondern nuetzlicherweise auch
			//die menge der zu kopierenden datensaetze
			//bsp: wenn iZeile = 2, ist damit die 3 Zeile gemeint weil die Zeilezaehlung
			//bei 0 beginnt, das bedeutet das ich beim ersten kopieren alle Zeilen bis zu
			//der mit dem Index 2 in das neue array kopieren muss und das sind dann genau
			//2 Stueck. Und 2 ist ja auch der wert von iZeile.
			System.arraycopy(strLager, 0, daten, 0, iZeile);
			
			//fuer den restlichen kopiervorgang muss man jetzt ab der Zeile nach iZeile
			//alle restlichen datensaetze in das neue array kopieren, dort aber mit der
			//Zielposition an der vorher die zu loeschende Zeile stand, also iZeile!!!
			//Die menge der zu kopierenden Datensaetze laesst sich auch wieder
			//mithilfe von iZiele ermitteln. einfach von der gesammtlaenge des neuen Arrays 
			//der Wert von iZeile abziehen.
			//bsp: iZeile ist 2. dann muessen beim zweiten kopiervorgang, der die restlichen
			//daten in das neue array(welches jetzt z.B 6 stat 7  Datensaetze lang ist)
			//kopiert, noch 4 element kopiert werden. 6 - 2 ist 4 !! passt also  
			System.arraycopy(strLager, iZeile + 1, daten, iZeile, daten.length - iZeile);
			
			//tabellendarstellung aktualisieren
			fireTableDataChanged();
		}
	}
	public void aktualisiere(String strValue, int iZeilen) {
		daten = lese_werte(strValue, iZeilen);
		fireTableDataChanged();
	}
}
