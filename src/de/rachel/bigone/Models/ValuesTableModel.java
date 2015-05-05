package de.rachel.bigone.Models;

import static de.rachel.bigone.DatabaseConstants.DRIVER;
import static de.rachel.bigone.DatabaseConstants.PASS;
import static de.rachel.bigone.DatabaseConstants.URL;
import static de.rachel.bigone.DatabaseConstants.USER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.table.AbstractTableModel;

public class ValuesTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -2431676313753205738L;
	private Statement  st = null;
	private ResultSet  rs = null;
	private Connection cn = null;
	private String strBetrag, strLiquiDate;
	private String[] columnName = new String[]{"TransID","s/h","Datum","Betrag","Buchtext","LiquiMon","Ereigniss"};
	private String[][] daten;
	private String strTransID; 
	public ValuesTableModel(String strWert, String strLiquiDate){
		System.out.println(strLiquiDate);
		strBetrag = strWert;
		this.strLiquiDate = strLiquiDate;
		daten = lese_werte(strBetrag, this.strLiquiDate);
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
		if (col == 2 || col == 3 || col == 4 || col == 5)
			return true;
		else
			return false;
	}
	public void setValueAt(Object value, int row, int col) {
		strTransID = daten[row][0];
		if(value != null)
			daten[row][col] = value.toString();
		else
			daten[row][col] = null;
        fireTableCellUpdated(row, col);
        if(value != null)
        	schreibe_neue_daten(row,col, value.toString(),strTransID);
        else
        	schreibe_neue_daten(row,col,"NULL",strTransID);
    }
	private String[][] lese_werte(String strValue, String strLiquiDate) {
		/*
		 * liest in abhaengigkeit der werte aus den 
		 * comboboxen und des optionsfeldes die werte aus der 
		 * DB und traegt sie in das String Array ein
		 */
   		
		String sql = "";
		String strLager[][] = null;

		try {
				//Select fitting database driver and connect:
				Class.forName( DRIVER );
				cn = DriverManager.getConnection( URL, USER, PASS );
				st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				sql = "select t.transaktions_id, t.soll_haben, t.datum, t.betrag, " +
						"t.buchtext, t.liqui_monat, k.ereigniss_krzbez " +
						"from transaktionen t, kontenereignisse k " +
						"where t.betrag = "+ strValue +
						" and k.ereigniss_id = t.ereigniss_id " +
						" and t.liqui_monat = '" + strLiquiDate + "' order by t.datum;";
				//System.out.println(sql);
				rs = st.executeQuery( sql );
				
				/*
				 *anzahl der zeilen herausfinden und das Lager Array
				 *entsprechend dimensionieren strLager[zeilen][spalten]
				 *die anzahl der Spalten steht schon fest und wird als 
				 *feste zahl eingetragen 
				 */
				rs.last();
				strLager = new String[rs.getRow()][7];
				rs.beforeFirst();
				
				//daten im str Array eintragen
				while(rs.next()) {
					for(int i=0; i<=6 ; i++)
						strLager[rs.getRow()-1][i] = rs.getString(i+1);
				}
				
				//recordset wieder leeren
				rs = null;
			}
			catch(Exception ex) {
				System.out.println(ex.toString());
			}
		
		return strLager;
	}
	private void schreibe_neue_daten(int row, int col, String strNewValue, String strTransID) {
		//hier werden anhand des alten Wertes und der neuen
		//werte in der Tabelle die sql stings erzeugt und
		//abgesetzt um die daten auch in der db zu aendern
		String strSqlUpdate ="";
		
		switch(col)
		{
		//fuer den betrag
		case 3:
			strSqlUpdate = "UPDATE transaktionen t SET t.betrag = " + strNewValue + 
							" WHERE t.transaktions_id = " + strTransID + " ;";
			break;
		//fuer das wertstellungsdatum
		case 2:
			strNewValue = "'"+strNewValue+"'";
			strSqlUpdate = "UPDATE transaktionen t SET t.datum = " + strNewValue + 
							" WHERE t.transaktions_id = " + strTransID + " ;";
			break;
		//fuer den Buchtext
		case 4:
			strNewValue = "'"+strNewValue+"'";
			strSqlUpdate = "UPDATE transaktionen t SET t.buchtext = " + strNewValue + 
							" WHERE t.transaktions_id = " + strTransID + " ;";
			break;
		//fuer das liquidatum
		case 5:
			if(strNewValue != "NULL")
				strNewValue = "'"+strNewValue+"'";
			strSqlUpdate = "UPDATE transaktionen t SET t.liqui_monat = " + strNewValue + 
							" WHERE t.transaktions_id = " + strTransID + " ;";
			break;
		}
		try {
			//Select fitting database driver and connect:
			Class.forName( DRIVER );
			cn = DriverManager.getConnection( URL, USER, PASS );
			st.executeUpdate(strSqlUpdate);
			}
			
		catch(Exception ex) {
			System.out.println(ex.toString());
		}
	}
	public void aktualisiere(String strValue, String strLiquiDate) {
		this.strLiquiDate = strLiquiDate;
		daten = lese_werte(strValue, this.strLiquiDate);
		fireTableDataChanged();
	}
}
