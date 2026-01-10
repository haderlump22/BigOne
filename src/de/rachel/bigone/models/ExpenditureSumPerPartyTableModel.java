package de.rachel.bigone.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureSumPerPartyTableRow;

public class ExpenditureSumPerPartyTableModel extends AbstractTableModel {
    private Connection cn = null;
    private String[] columnName = new String[] { "Name", "Summe" };
    private List<ExpenditureSumPerPartyTableRow> tableData = new ArrayList<>();

    public ExpenditureSumPerPartyTableModel(Connection loginCN) {
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
        ExpenditureSumPerPartyTableRow expenditureSumPerPartyTableRow = tableData.get(row);
        Object returnValue = null;

        switch (col) {
            case 0:
                returnValue = expenditureSumPerPartyTableRow.NameOfParty();
                break;
            case 1:
                returnValue = expenditureSumPerPartyTableRow.ExpenditureSum();
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
                SELECT p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' AS party, SUM(haaa.betrag) betrag
                FROM ha_ausgaben haa, ha_ausgaben_aufteilung haaa, personen p
                WHERE haa.gilt_bis IS NULL
                AND haaa."ausgabenId" = haa."ausgabenId"
                AND haaa."parteiId" = p.personen_id
                GROUP BY party
                """, 2);

        try {
            getter.beforeFirst();

            while (getter.next()) {
                tableData.add(new ExpenditureSumPerPartyTableRow(getter.getString("party"), getter.getDouble("betrag")));
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
