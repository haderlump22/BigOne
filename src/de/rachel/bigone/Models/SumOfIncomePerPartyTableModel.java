package de.rachel.bigone.Models;

import java.sql.Connection;
import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;

public class SumOfIncomePerPartyTableModel extends AbstractTableModel{
	private Connection cn = null;
	private String[] columnName = new String[]{"Name","Summe","Anteil in Prozent"};
	private Object[][] daten;
	public SumOfIncomePerPartyTableModel(Connection LoginCN){
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
	public boolean isCellEditable(int row, int col){
		return false;
	}
	private Object[][] lese_werte() {
		/*
		 * get the current Income of everey Party
		 */
   		DBTools getter = new DBTools(cn);
		Object[][] daten;
		Double SummeOfAllIncome = 0.0;

 		getter.select(
				"SELECT p.name || ', ' || SUBSTRING(p.vorname, 1, 1) as party, sum(gg.betrag), 0 AS anteil from personen p, ha_gehaltsgrundlagen gg where gilt_bis is NULL and p.personen_id = gg.partei_id group by p.name, p.vorname order by p.name;",
				3);
		
				daten = getter.getData();

		// calculate the percent Value for Column 3
		
		// first get the sum of all Values in Column 2
		for (int i = 0; i < daten.length; i++) {
			SummeOfAllIncome = SummeOfAllIncome + Double.valueOf(daten[i][1].toString());
		}

		// Determine for every Incom the percentage share
		for (int i = 0; i < daten.length; i++) {
			Double Proportion = (Double.valueOf(daten[i][1].toString()) * 100) / SummeOfAllIncome;
			daten[i][2] = roundScale2(Proportion);
		}
		return daten;
	}
	private double roundScale2(double d) {
		return Math.round(d * 100) / 100.;
	}
}
