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
	private List<TransferAmountDetailTableRow> tableData = new ArrayList<>();

	public TransferAmountDetailTableModel(Connection loginCN) {
		cn = loginCN;
		lese_werte();
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return tableData.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		TransferAmountDetailTableRow transferAmountDetailTableRow = tableData.get(row);
		Object returnValue = null;

		switch (col) {
			case -1:
				returnValue = transferAmountDetailTableRow.TransferAmountId();
				break;
			case 0:
				returnValue = transferAmountDetailTableRow.NameOfParty();
				break;
			case 1:
				returnValue = transferAmountDetailTableRow.Amount();
				break;
			case 2:
				returnValue = transferAmountDetailTableRow.ValidUntil();
				break;
			default:
				break;
		}

		return returnValue;
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	private void lese_werte() {
		/*
		 * get the current Income of everey Party
		 */
		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT ueberweisungsbetrag_id, p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' AS party, betrag, gilt_bis
				FROM personen p, ha_ueberweisungsbetraege ueb
				WHERE p.personen_id = ueb.partei_id
				ORDER BY gilt_bis DESC, party, betrag DESC;
				""",4);

		try {
			getter.beforeFirst();

			while (getter.next()) {
				tableData.add(new TransferAmountDetailTableRow(getter.getInt("ueberweisungsbetrag_id"), getter.getString("party"),
						getter.getDouble("betrag"), getter.getDate("gilt_bis")));
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}
	}

	public boolean isDateFilled(int RowNumber) {
		TransferAmountDetailTableRow transferAmountDetailTableRow = tableData.get(RowNumber);

		if (transferAmountDetailTableRow.ValidUntil() == null) {
			return false;
		} else {
			return true;
		}
	}

	public void aktualisiere() {
        tableData.clear();
        lese_werte();
		fireTableDataChanged();
	}
}
