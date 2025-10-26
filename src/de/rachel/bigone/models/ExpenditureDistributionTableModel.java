package de.rachel.bigone.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ExpenditureDistributionTableRow;

public class ExpenditureDistributionTableModel extends AbstractTableModel {
    private Connection cn = null;
    private String[] columnName = new String[] { "Name", "Betrag" };
    private List<ExpenditureDistributionTableRow> TableData = new ArrayList<>();

    public ExpenditureDistributionTableModel(Connection LoginCN) {
        cn = LoginCN;
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
        ExpenditureDistributionTableRow Zeile = TableData.get(row);
        Object ReturnValue = null;

        switch (col) {
            case 0:
                ReturnValue = Zeile.NameOfParty();
                break;
            case 1:
                ReturnValue = Zeile.Amount();
                break;
            default:
                break;
        }

        return ReturnValue;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    private void lese_werte(Integer ExpenditureId) {
        /*
         * get all known expenditure over the time, actual and old one
         */
        DBTools getter = new DBTools(cn);
        ResultSet rs;

        getter.select("""
                SELECT haaa."ausgabenAufteilungId", p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' AS party, betrag
                FROM ha_ausgaben_aufteilung haaa, personen p
                WHERE haaa."parteiId" = p.personen_id
                AND haaa."ausgabenId" = %s
                ORDER BY party",
                """.formatted(ExpenditureId.toString()),2);

        rs = getter.getResultSet();
        try {
            rs.beforeFirst();

            while (rs.next()) {
                TableData.add(new ExpenditureDistributionTableRow(rs.getInt("ausgabenAufteilungId"), rs.getString("party"), rs.getDouble("betrag")));
            }
        } catch (Exception e) {
            System.out.println("ExpenditureDetailTableModel - lese_werte(): " + e.toString());
        }
    }

    public void aktualisiere(Integer ExpenditureId) {
        // before adding new data, remove the old in the TableData List
        TableData.clear();
		lese_werte(ExpenditureId);
		fireTableDataChanged();
	}
}
