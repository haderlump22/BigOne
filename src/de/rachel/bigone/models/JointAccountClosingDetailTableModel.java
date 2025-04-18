package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.JointAccountClosingDetailTableRow;

public class JointAccountClosingDetailTableModel extends AbstractTableModel {
	private Connection cn = null;
	private String[] columnName = new String[] { "Ausgabenart", "Betrag IST", "Betrag PLAN", "Differenz" };
	private List<JointAccountClosingDetailTableRow> TableData = new ArrayList<>();
	private String billingMonth = null;

	public JointAccountClosingDetailTableModel(Connection LoginCN) {
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
		JointAccountClosingDetailTableRow Zeile = TableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = Zeile.EventId();
				break;
			case 0:
				ReturnValue = Zeile.NameOfExpenditure();
				break;
			case 1:
				ReturnValue = Zeile.ActualAmount();
				break;
			case 2:
				ReturnValue = Zeile.PlanAmount();
				break;
			case 3:
				ReturnValue = Zeile.Difference();
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
		 * get the current Amount Sum of each type of money
		 */
		if (billingMonth != null && Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}",billingMonth)) {
			// if TableData contain Values => flush them before fill it with new data
			if (TableData.size() > 0) {
				TableData.clear();
			}

			DBTools getter = new DBTools(cn);

			getter.select(
					"select ha_kategorie.ha_kategorie_id, ha_kategorie.kategoriebezeichnung, \"get_actualAmount\"(ereigniss_id, 13, '" + billingMonth + "') betragist\n" +
					"from transaktionen, ha_kategorie\n" +
					"where konten_id = 13\n" +
					"and liqui_monat = '" + billingMonth + "'\n" +
					"and ha_kategorie.ha_kategorie_id = transaktionen.ereigniss_id\n" +
					"group by ha_kategorie.ha_kategorie_id, ha_kategorie.kategoriebezeichnung, ereigniss_id\n" +
					"order by ha_kategorie.kategoriebezeichnung;",
					2);

			try {
				getter.beforeFirst();

				while (getter.next()) {
					TableData.add(new JointAccountClosingDetailTableRow(getter.getInt("ha_kategorie_id"), getter.getString("kategoriebezeichnung"),
							getter.getDouble("betragist"), 0.0, 0.0));
				}
			} catch (Exception e) {
				System.out.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
			}
		}
	}

	public void aktualisiere(String billingMonth) {
		this.billingMonth = billingMonth;
		this.lese_werte();
		fireTableDataChanged();
	}
}
