package de.rachel.bigone.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.ReadCamt;

public class RACTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -2431676313753205738L;
	private String[] columnName = new String[]{"Wertstellung","s/h","Betrag","Buchungshinweis","Empfänger","LiquiMon","Ereignis"};
	private String[][] daten;
	private String[][] strLager;
	private static int ValueDate = 0;
	private static int CreditDebitIndicator = 1;
	private static int Amount = 2;
	private static int Unstructured = 3;	//Unstrukturierter Verwendungszweck 140zeichen max
	private static int Creditor = 4;
	private static int LiquiMonth = 5;
	private static int AccountBookingEvent = 6;
	SimpleDateFormat SQLDATE = new SimpleDateFormat("yyyy-MM-dd");
	
	public RACTableModel(ReadCamt Auszug){
		daten = lese_werte(Auszug);
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
	private String[][] lese_werte(ReadCamt Auszug) {
		/*
		 * Daten des Auszugs aus einer XML CAMT Datei werden hier
		 * in das notwendige Array der Tabelle Models ein
		 */
		String strLager[][] = new String[Auszug.getBuchungsanzahl()][7];
		
		for(int iAktuelleBuchung = 0; iAktuelleBuchung < Auszug.getBuchungsanzahl(); iAktuelleBuchung++){		
			strLager[iAktuelleBuchung][ValueDate] = Auszug.getValDt(iAktuelleBuchung);
			
			strLager[iAktuelleBuchung][LiquiMonth] = Auszug.getValDt(iAktuelleBuchung).substring(0, 8) + "01";
			
			strLager[iAktuelleBuchung][Unstructured] = Auszug.getUstrd(iAktuelleBuchung);
			
			strLager[iAktuelleBuchung][Creditor] = Auszug.getCdtr(iAktuelleBuchung);
			
			strLager[iAktuelleBuchung][Amount] = Auszug.getAmt(iAktuelleBuchung); 
			
			//Das Soll Haben wird aus dem Feld CreditDebitIndicator gebildet
			//CRDT ist haben Buchung
			//DBIT ist soll Buchung
			if(Auszug.getCdtDbtInd(iAktuelleBuchung).equals("CRDT"))
				strLager[iAktuelleBuchung][CreditDebitIndicator] = "h";
			if(Auszug.getCdtDbtInd(iAktuelleBuchung).equals("DBIT"))
				strLager[iAktuelleBuchung][CreditDebitIndicator] = "s";

			//am ende der verarbeitung einer Zeile wird das ereigniss fest auf
			//HaushGeld (46) gesetzt
			strLager[iAktuelleBuchung][AccountBookingEvent] = "HaushGeld (46)";
		}
		return strLager;
	}
	public void removeRow(int iZeile, boolean refreshImmediately) {
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
			
			//tabellendarstellung aktualisieren wenn es gewollt ist
			if (refreshImmediately)
				fireTableDataChanged();
		}
	}
	public void aktualisiere(ReadCamt Auszug) {
		daten = lese_werte(Auszug);
		fireTableDataChanged();
	}

	public void removeUnusedRows(Date von, Date bis) {
		// remove all Rows that are not in the Timerange
		
		// remove the hour, minutes and seconds to compare only the yyyy-mm-dd
		String sVon = SQLDATE.format(von);
		String sBis = SQLDATE.format(bis);
		
		// check rows
		for (int iZeile = 0; iZeile < daten.length; iZeile++) {
			// if is not in the timerange, remove it
			try {
				if (SQLDATE.parse(daten[iZeile][ValueDate]).before(SQLDATE.parse(sVon)) || SQLDATE.parse(daten[iZeile][ValueDate]).after(SQLDATE.parse(sBis))) {
					this.removeRow(iZeile, false);
					
					// because we delete one Row in the array and the row after move to the actual index, wie must go on on the same index,
					iZeile--;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		//tabellendarstellung aktualisieren 
		fireTableDataChanged();
	}
}
