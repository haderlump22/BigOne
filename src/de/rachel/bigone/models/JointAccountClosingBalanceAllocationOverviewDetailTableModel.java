package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.JointAccountClosingBalanceAllocationOverviewDetailTableRow;

public class JointAccountClosingBalanceAllocationOverviewDetailTableModel extends AbstractTableModel {
	private Connection LoginCn = null;
	private String[] columnName = new String[] { "Name", "Anteil in %", "Betrag" };
	private List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> tableData = new ArrayList<>();

	public JointAccountClosingBalanceAllocationOverviewDetailTableModel(Connection LoginCN) {
		this.LoginCn = LoginCn;
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
		JointAccountClosingBalanceAllocationOverviewDetailTableRow jointAccountClosingBalanceAllocationOverviewDetailTableModelRow = tableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case 0:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.nameOfParty();
				break;
			case 1:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.shareInPercent();
				break;
			case 2:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.amount();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public boolean isCellEditable(int row, int col) {
		// this has to change, because for AccountClosing we must edit this Values sometimes
		return false;
	}

	private void lese_werte() {
		/*
		 * must get the Values from the SumOverviewStuff
		 */
	}

	public void aktualisiere() {
		lese_werte();
		fireTableDataChanged();
	}
}
