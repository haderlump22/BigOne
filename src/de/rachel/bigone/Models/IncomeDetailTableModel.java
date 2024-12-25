package de.rachel.bigone.Models;

import java.sql.Connection;
import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;

public class IncomeDetailTableModel extends AbstractTableModel{
	private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Betrag", "gilt bis", "Art" };
	private Object[][] daten;

	public IncomeDetailTableModel(Connection LoginCN) {
		cn = LoginCN;
		daten = lese_werte();
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
		return false;
	}

	private Object[][] lese_werte() {
		/*
		 * get the current Income of everey Party
		 */
		DBTools getter = new DBTools(cn);
		Object[][] daten;

		getter.select(
				"SELECT p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party, betrag, gilt_bis, art from personen p, ha_gehaltsgrundlagen gg where p.personen_id = gg.partei_id order by gilt_bis DESC, party, betrag DESC;",
				4);

		daten = getter.getData();

		return daten;
	}
}
