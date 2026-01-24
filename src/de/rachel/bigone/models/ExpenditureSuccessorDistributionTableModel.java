package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureSuccessorDistributionTableRow;

public class ExpenditureSuccessorDistributionTableModel  extends AbstractTableModel {
private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Betrag", "Bemerkung" };
	private List<ExpenditureSuccessorDistributionTableRow> tableData = new ArrayList<>();
	private Integer[] detailIdsForMarkingDifferenceValue = new Integer[0];
    private JTable expenditureDetailTable;

	public ExpenditureSuccessorDistributionTableModel(Connection LoginCN, JTable expenditureDetailTable) {
		cn = LoginCN;
        this.expenditureDetailTable = expenditureDetailTable;
        Integer expenditureId = (Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -1);
		lese_werte(expenditureId);
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
		ExpenditureSuccessorDistributionTableRow accountClosingDetailTableModelRow = tableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = accountClosingDetailTableModelRow.partyId();
				break;
			case 0:
				ReturnValue = accountClosingDetailTableModelRow.nameOfParty();
				break;
			case 1:
				ReturnValue = accountClosingDetailTableModelRow.amount();
				break;
			case 2:
				ReturnValue = accountClosingDetailTableModelRow.comment();
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

	private void lese_werte(Integer expenditureId) {
		/*
		 * Determine the current distribution of the expenditure that's to be replaced
		 */

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT haaa."parteiId", p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' AS party, betrag, bemerkung
                FROM ha_ausgaben_aufteilung haaa, personen p
                WHERE haaa."parteiId" = p.personen_id
                AND haaa."ausgabenId" = %d
                ORDER BY party
				""".formatted(expenditureId));

        try {
            getter.beforeFirst();

            while (getter.next()) {
                tableData.add(new ExpenditureSuccessorDistributionTableRow(
                        getter.getInt("parteiId"),
                        getter.getString("party"),
                        getter.getDouble("betrag"),
                        getter.getString("bemerkung")));
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
        }
	}

	public void aktualisiere(String billingMonth) {
		if (!billingMonth.equals("")) {
			this.billingMonth = billingMonth;
			// lese_werte();
		} else {
			tableData = new ArrayList<>();
		}
		fireTableDataChanged();
	}

	public void setDetailIdsForMarkingDifferenceValue(Integer[] detailIdsForMarkingDifferenceValue) {
		this.detailIdsForMarkingDifferenceValue = detailIdsForMarkingDifferenceValue;
		fireTableDataChanged();
	}

	public boolean rowHasToMark(int row) {
		for (Integer detailId : detailIdsForMarkingDifferenceValue) {
			if (((Integer)getValueAt(row, -1)).equals(detailId)) {
				return true;
			}
		}
		return false;
	}
}
