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
    private List<ExpenditureSumPerPartyTableRow> TableData = new ArrayList<>();

    public ExpenditureSumPerPartyTableModel(Connection LoginCN) {
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
        ExpenditureSumPerPartyTableRow Zeile = TableData.get(row);
        Object ReturnValue = null;

        switch (col) {
            case 0:
                ReturnValue = Zeile.NameOfParty();
                break;
            case 1:
                ReturnValue = Zeile.ExpenditureSum();
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
        ResultSet rs;

        getter.select("SELECT p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party, sum(haaa.betrag) betrag\n" +
                "from ha_ausgaben haa, ha_ausgaben_aufteilung haaa, personen p\n" +
                "where haa.gilt_bis is NULL\n" +
                "and haaa.\"ausgabenId\" = haa.\"ausgabenId\"\n" +
                "and haaa.\"parteiId\" = p.personen_id\n" +
                "group by party", 2);

        rs = getter.getResultSet();
        try {
            rs.beforeFirst();

            while (rs.next()) {
                TableData.add(new ExpenditureSumPerPartyTableRow(rs.getString("party"), rs.getDouble("betrag")));
            }
        } catch (Exception e) {
            System.out.println("ExpenditureSumPerPartyTableModel - lese_werte(): " + e.toString());
        }
    }
}
