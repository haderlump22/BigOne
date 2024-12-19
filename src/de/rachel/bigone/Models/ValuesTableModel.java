package de.rachel.bigone.Models;

import java.sql.Connection;
import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;

public class ValuesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2431676313753205738L;
	private Connection cn = null;
	private String[] columnName = new String[] { "TransID", "s/h", "Datum", "Betrag", "Buchtext", "LiquiMon",
			"Ereigniss" };
	private Object[][] daten;
	private String strTransID;

	public ValuesTableModel(String strBetrag, String strLiquiDate, String sIban, Connection LoginCN) {
		// System.out.println(strLiquiDate);
		cn = LoginCN;
		daten = lese_werte(strBetrag, strLiquiDate, sIban);
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

	public boolean isCellEditable(int row, int col) {
		if (col == 2 || col == 3 || col == 4 || col == 5)
			return true;
		else
			return false;
	}

	public void setValueAt(Object value, int row, int col) {
		strTransID = daten[row][0].toString();
		if (value != null)
			daten[row][col] = value.toString();
		else
			daten[row][col] = null;

		fireTableCellUpdated(row, col);
		
		if (value != null)
			schreibe_neue_daten(row, col, value.toString(), strTransID);
		else
			schreibe_neue_daten(row, col, "NULL", strTransID);
	}

	private Object[][] lese_werte(String strValue, String strLiquiDate, String sIban) {
		/*
		 * ermittelt die dem Betrag entsprechenden Datensätze
		 * des ausgewählten Kontos und des gesetzten Liquizeitraumes
		 * sortiert nach Datum in absteigender Form
		 * zur Darstellung in der Tabelle
		 * Wird das Liquidatum als leerer String übergeben
		 * wird es nicht berücksichtigt
		 */
		DBTools getter = new DBTools(cn);

		getter.select("select t.transaktions_id, t.soll_haben, t.datum, t.betrag, " +
				"t.buchtext, t.liqui_monat, k.ereigniss_krzbez " +
				"from transaktionen t, kontenereignisse k, konten kto " +
				"where t.betrag = " + strValue +
				" and k.ereigniss_id = t.ereigniss_id " +
				(strLiquiDate.isEmpty() ? "" : " and t.liqui_monat = '" + strLiquiDate + "'") +
				" and t.konten_id = kto.konten_id " +
				" and kto.iban = '" + sIban + "'" +
				" order by t.datum DESC;", 7);

		return getter.getData();
	}

	private void schreibe_neue_daten(int row, int col, String strNewValue, String strTransID) {
		// hier werden anhand des alten Wertes und der neuen
		// werte in der Tabelle die sql stings erzeugt und
		// abgesetzt um die daten auch in der db zu aendern

		String strSqlUpdate = null;
		DBTools updater = new DBTools(cn);

		switch (col) {
			// fuer den betrag
			case 3:
				strSqlUpdate = "UPDATE transaktionen SET betrag = " + strNewValue +
						" WHERE transaktions_id = " + strTransID + " ;";
				break;
			// fuer das wertstellungsdatum
			case 2:
				strNewValue = "'" + strNewValue + "'";
				strSqlUpdate = "UPDATE transaktionen SET datum = " + strNewValue +
						" WHERE transaktions_id = " + strTransID + " ;";
				break;
			// fuer den Buchtext
			case 4:
				strNewValue = "'" + strNewValue + "'";
				strSqlUpdate = "UPDATE transaktionen SET buchtext = " + strNewValue +
						" WHERE transaktions_id = " + strTransID + " ;";
				break;
			// fuer das liquidatum
			case 5:
				if (strNewValue != "NULL")
					strNewValue = "'" + strNewValue + "'";
				strSqlUpdate = "UPDATE transaktionen SET liqui_monat = " + strNewValue +
						" WHERE transaktions_id = " + strTransID + " ;";
				break;
		}

		updater.update(strSqlUpdate);
	}

	public void aktualisiere(String strValue, String strLiquiDate, String sIban) {
		daten = lese_werte(strValue, strLiquiDate, sIban);
		fireTableDataChanged();
	}
}
