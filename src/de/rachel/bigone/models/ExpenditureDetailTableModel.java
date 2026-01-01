package de.rachel.bigone.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureDetailTableRow;

public class ExpenditureDetailTableModel extends AbstractTableModel {
    private Connection cn = null;
    private String[] columnName = new String[] { "Bezeichung", "Betrag", "Aufteilungsart", "gilt bis" };
    private List<ExpenditureDetailTableRow> tableData = new ArrayList<>();

    public ExpenditureDetailTableModel(Connection LoginCN) {
        cn = LoginCN;
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
        ExpenditureDetailTableRow expenditureDetailTableRow = tableData.get(row);
        Object returnValue = null;

        switch (col) {
            case -1: //it only called by the listener "ExpenditureDetailTableSelectionListener"
                returnValue = expenditureDetailTableRow.ExpenditureId();
                break;
            case -2: // //it only called by the listener "ExpenditureDetailTableSelectionListener"
                returnValue = expenditureDetailTableRow.ExpenditureHint();
                break;
            case 0:
                returnValue = expenditureDetailTableRow.Description();
                break;
            case 1:
                returnValue = expenditureDetailTableRow.Amount();
                break;
            case 2:
                returnValue = expenditureDetailTableRow.DivideType();
                break;
            case 3:
                returnValue = expenditureDetailTableRow.ValidUntil();
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
         * get all known expenditure over the time, actual and old one
         */
        DBTools getter = new DBTools(cn);
        ResultSet rs;

        getter.select("""
                SELECT "ausgabenId", bezeichnung, betrag, aufteilungsart, gilt_bis, bemerkung
                FROM ha_ausgaben
                ORDER BY gilt_bis DESC, betrag DESC
                """,2);

        rs = getter.getResultSet();
        try {
            rs.beforeFirst();

            while (rs.next()) {
                tableData.add(new ExpenditureDetailTableRow(rs.getInt("ausgabenId"), rs.getString("bezeichnung"), rs.getDouble("betrag"),
                        rs.getString("aufteilungsart"), rs.getDate("gilt_bis"), rs.getString("bemerkung")));
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
    }

    public void aktualisiere() {
        tableData.clear();
        lese_werte();
		fireTableDataChanged();
	}
}
