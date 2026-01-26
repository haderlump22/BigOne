package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureSuccessorDistributionTableRow;

public class ExpenditureSuccessorDistributionTableModel  extends AbstractTableModel {
private Connection cn = null;
	private String[] columnName = new String[] { "Name", "Betrag", "Bemerkung" };
	private ArrayList<ExpenditureSuccessorDistributionTableRow> expenditureSuccessorDistributionTable = new ArrayList<>();

	public ExpenditureSuccessorDistributionTableModel(Connection LoginCN, JTable expenditureDetailTable) {
		cn = LoginCN;
        Integer expenditureId = (Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -1);
		lese_werte(expenditureId);
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return expenditureSuccessorDistributionTable.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		ExpenditureSuccessorDistributionTableRow expenditureSuccessorDistributionTableRow = expenditureSuccessorDistributionTable.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = expenditureSuccessorDistributionTableRow.partyId();
				break;
			case 0:
				ReturnValue = expenditureSuccessorDistributionTableRow.nameOfParty();
				break;
			case 1:
				ReturnValue = expenditureSuccessorDistributionTableRow.amount();
				break;
			case 2:
				ReturnValue = expenditureSuccessorDistributionTableRow.comment();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 1 || col == 2) {
            return true;
        } else {
            return false;
        }
	}

    	public void setValueAt(Object value, int row, int col) {
		ExpenditureSuccessorDistributionTableRow tmpRow;

		// first del row and save temporary the content
		tmpRow = expenditureSuccessorDistributionTable.remove(row);

        if (value != null) {
            switch (col) {
                case 1:
                    // and then put an new at the same position
                    expenditureSuccessorDistributionTable.add(row,
                            new ExpenditureSuccessorDistributionTableRow(
                                    tmpRow.partyId(),
                                    tmpRow.nameOfParty(),
                                    Double.valueOf(value.toString()),
                                    tmpRow.comment()));
                    break;
                case 2:
                    // and then put an new at the same position
                    expenditureSuccessorDistributionTable.add(row,
                            new ExpenditureSuccessorDistributionTableRow(
                                    tmpRow.partyId(),
                                    tmpRow.nameOfParty(),
                                    tmpRow.amount(),
                                    value.toString()));
                    break;
                default:
                    break;
            }
        }

		fireTableCellUpdated(row, col);
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
                expenditureSuccessorDistributionTable.add(new ExpenditureSuccessorDistributionTableRow(
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

    public ArrayList<ExpenditureSuccessorDistributionTableRow> getTableData() {
        return expenditureSuccessorDistributionTable;
    }
}
