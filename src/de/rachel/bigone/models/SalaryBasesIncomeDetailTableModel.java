package de.rachel.bigone.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.SalaryBasesIncomeDetailTableRow;

public class SalaryBasesIncomeDetailTableModel extends AbstractTableModel{
	private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Betrag", "gilt bis", "Art" };
	private List<SalaryBasesIncomeDetailTableRow> TableData = new ArrayList<>();

	public SalaryBasesIncomeDetailTableModel(Connection LoginCN) {
		cn = LoginCN;
		lese_werte();
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return TableData.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		SalaryBasesIncomeDetailTableRow Zeile = TableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case 0:
				ReturnValue = Zeile.NameOfParty();
				break;
			case 1:
				ReturnValue = Zeile.Amount();
				break;
			case 2:
				ReturnValue = Zeile.ValidUntil();
				break;
			case 3:
				ReturnValue = Zeile.Type();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	private void lese_werte() {
		/*
		 * get the current Income of everey Party
		 */
		DBTools getter = new DBTools(cn);
		ResultSet rs;

		getter.select(
				"SELECT p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party, betrag, gilt_bis, art from personen p, ha_gehaltsgrundlagen gg where p.personen_id = gg.partei_id order by gilt_bis DESC, party, betrag DESC;",
				4);

		rs = getter.getResultSet();
		try {
			rs.beforeFirst();

			while (rs.next()) {
				TableData.add(new SalaryBasesIncomeDetailTableRow(rs.getString("party"), rs.getDouble("betrag"), rs.getDate("gilt_bis"), rs.getString("art")));
			}
		} catch (Exception e) {
			System.out.println("SalaryBasesIncomeDetailTableModel - lese_werte(): " + e.toString());
		}
	}
}
