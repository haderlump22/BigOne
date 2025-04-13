package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.JointAccountClosingDetailTableRow;

public class JointAccountClosingDetailTableModel extends AbstractTableModel {
	private Connection cn = null;
	private String[] columnName = new String[] { "Ausgabenart", "Betrag IST", "Plan", "Differenz" };
	private List<JointAccountClosingDetailTableRow> TableData = new ArrayList<>();

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
		 * get the current Income of everey Party
		 */
		DBTools getter = new DBTools(cn);

		// has to be corrected
		// getter.select(
		// 		"SELECT ueberweisungsbetrag_id, p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party,\n" +
		// 		"betrag, gilt_bis from personen p, ha_ueberweisungsbetraege ueb where p.personen_id = ueb.partei_id\n" +
		// 		"order by gilt_bis DESC, party, betrag DESC;",
		// 		4);

		// try {
		// 	getter.beforeFirst();

		// 	while (getter.next()) {
		// 		// has to be corrected
		// 		// TableData.add(new TransferAmountDetailTableRow(getter.getInt("ueberweisungsbetrag_id"), getter.getString("party"),
		// 		// 		getter.getDouble("betrag"), getter.getDate("gilt_bis")));
		// 	}
		// } catch (Exception e) {
		// 	System.out.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
		// }
	}
}
