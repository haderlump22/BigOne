package de.rachel.bigone.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.SalaryBasesSumOfIncomePerPartyTableRow;

public class SalaryBasesSumOfIncomePerPartyTableModel extends AbstractTableModel{
	private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Summe", "Anteil in Prozent" };
	private	Double SumOfAllIncome = 0.0;
	private List<SalaryBasesSumOfIncomePerPartyTableRow> TableData = new ArrayList<>();

	public SalaryBasesSumOfIncomePerPartyTableModel(Connection LoginCN) {
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
		SalaryBasesSumOfIncomePerPartyTableRow Zeile = TableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = Zeile.partyId();
				break;
			case 0:
				ReturnValue = Zeile.Name();
				break;
			case 1:
				ReturnValue = Zeile.Sum();
				break;
			case 2:
				// for Column 3 (Percentvalue) the Value must be calculated separatly
				ReturnValue = (Zeile.Sum() * 100) / SumOfAllIncome;
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

		getter.select("""
				SELECT p.personen_id, p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party, sum(gg.betrag) as betrag
				FROM personen p, ha_gehaltsgrundlagen gg
				WHERE gilt_bis IS NULL
				AND p.personen_id = gg.partei_id
				GROUP BY p.personen_id, p.name, p.vorname
				ORDER BY p.name;
				""",
				3);

		rs = getter.getResultSet();
		try {
			rs.beforeFirst();

			while (rs.next()) {
				TableData
						.add(new SalaryBasesSumOfIncomePerPartyTableRow(rs.getInt("personen_id"), rs.getString("party"), rs.getDouble("betrag")));
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}

		// first get the sum of all Values in Column 2
		for (SalaryBasesSumOfIncomePerPartyTableRow Zeile : TableData) {
			SumOfAllIncome = SumOfAllIncome + Zeile.Sum();
		}
	}
}
