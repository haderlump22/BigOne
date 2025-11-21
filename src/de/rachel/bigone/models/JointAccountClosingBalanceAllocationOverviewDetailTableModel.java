package de.rachel.bigone.models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.records.JointAccountClosingBalanceAllocationOverviewDetailTableRow;

public class JointAccountClosingBalanceAllocationOverviewDetailTableModel extends AbstractTableModel {
	private String[] columnName = new String[] { "Name", "Anteil in %", "Betrag" };
	private List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> tableData = new ArrayList<>();

	public JointAccountClosingBalanceAllocationOverviewDetailTableModel() {

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
		JointAccountClosingBalanceAllocationOverviewDetailTableRow jointAccountClosingBalanceAllocationOverviewDetailTableModelRow = tableData
				.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.partyId();
				break;
			case 0:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.nameOfParty();
				break;
			case 1:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.shareInPercent();
				break;
			case 2:
				ReturnValue = jointAccountClosingBalanceAllocationOverviewDetailTableModelRow.finalShare();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public void aktualisiere(
			List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> jointAccountClosingBalanceAllocationOverviewDetailTableData) {
		this.tableData = jointAccountClosingBalanceAllocationOverviewDetailTableData;
		fireTableDataChanged();
	}

	public List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> getTableData () {
		return tableData;
	}
}
