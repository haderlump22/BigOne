package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureDistributionTableRow;

public class ExpenditureDistributionTableModel extends AbstractTableModel {
    private Connection cn = null;
    private String[] columnName = new String[] { "Name", "Betrag" };
    private List<ExpenditureDistributionTableRow> tableData = new ArrayList<>();

    public ExpenditureDistributionTableModel(Connection LoginCN) {
        cn = LoginCN;
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
        ExpenditureDistributionTableRow expenditureDistributionTableRow = tableData.get(row);
        Object returnValue = null;

        switch (col) {
            case -1: // //it only called by the listener "ExpenditureDistributionTableSelectionListener"
                returnValue = expenditureDistributionTableRow.expenditureDistributionComment();
                break;
            case 0:
                returnValue = expenditureDistributionTableRow.NameOfParty();
                break;
            case 1:
                returnValue = expenditureDistributionTableRow.Amount();
                break;
            default:
                break;
        }

        return returnValue;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    private void lese_werte(Integer ExpenditureId) {
        /*
         * get all known expenditure over the time, actual and old one
         */
        DBTools getter = new DBTools(cn);

        getter.select("""
                SELECT haaa."ausgabenAufteilungId", p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' AS party, betrag, bemerkung
                FROM ha_ausgaben_aufteilung haaa, personen p
                WHERE haaa."parteiId" = p.personen_id
                AND haaa."ausgabenId" = %s
                ORDER BY party
                """.formatted(ExpenditureId.toString()));

        try {
            getter.beforeFirst();

            while (getter.next()) {
                tableData.add(new ExpenditureDistributionTableRow(getter.getInt("ausgabenAufteilungId"),
                        getter.getString("party"), getter.getDouble("betrag"), getter.getString("bemerkung")));
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
        }
    }

    public void aktualisiere(Integer ExpenditureId) {
        // before adding new data, remove the old in the tableData List
        tableData.clear();
		lese_werte(ExpenditureId);
		fireTableDataChanged();
	}
}
