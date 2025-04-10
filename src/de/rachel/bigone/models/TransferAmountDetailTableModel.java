package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.TransferAmountDetailTableRow;

public class TransferAmountDetailTableModel extends AbstractTableModel {
	private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Betrag", "gilt bis" };
	private List<TransferAmountDetailTableRow> TableData = new ArrayList<>();

	public TransferAmountDetailTableModel(Connection LoginCN) {
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
		TransferAmountDetailTableRow Zeile = TableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = Zeile.TransferAmountId();
				break;
			case 0:
				ReturnValue = Zeile.NameOfParty();
				break;
			case 1:
				ReturnValue = Zeile.Amount();
				break;
			case 2:
				ReturnValue = Zeile.ValidUntil();
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

		getter.select(
				"SELECT ueberweisungsbetrag_id, p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party,\n" +
				"betrag, gilt_bis from personen p, ha_ueberweisungsbetraege ueb where p.personen_id = ueb.partei_id\n" +
				"order by gilt_bis DESC, party, betrag DESC;",
				4);

		try {
			getter.beforeFirst();

			while (getter.next()) {
				TableData.add(new TransferAmountDetailTableRow(getter.getInt("ueberweisungsbetrag_id"), getter.getString("party"),
						getter.getDouble("betrag"), getter.getDate("gilt_bis")));
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
		}
	}

	public boolean isDateFilled(int RowNumber) {
		TransferAmountDetailTableRow Zeile = TableData.get(RowNumber);

		if (Zeile.ValidUntil() == null) {
			return false;
		} else {
			return true;
		}
	}
}
