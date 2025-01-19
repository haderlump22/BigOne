package de.rachel.bigone.Models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.Records.ExpenditureDetailTableRow;

public class ExpenditureDetailTableModel extends AbstractTableModel {
    private Connection cn = null;
    private String[] columnName = new String[] { "Bezeichung", "Betrag", "Aufteilungsart", "gilt bis", "Person", "Betrag", "Person", "Betrag" };
    private List<ExpenditureDetailTableRow> TableData = new ArrayList<>();

    public ExpenditureDetailTableModel(Connection LoginCN) {
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
        ExpenditureDetailTableRow Zeile = TableData.get(row);
        Object ReturnValue = null;

        switch (col) {
            case 0:
                ReturnValue = Zeile.Description();
                break;
            case 1:
                ReturnValue = Zeile.Amount();
                break;
            case 2:
                ReturnValue = Zeile.ValidUntil();
                break;
            case 3:
                ReturnValue = Zeile.DivideType();
                break;
            case 4:
                ReturnValue = Zeile.PartyName1();
                break;
            case 5:
                ReturnValue = Zeile.AmountParty1();
                break;
            case 6:
                ReturnValue = Zeile.PartyName2();
                break;
            case 7:
                ReturnValue = Zeile.AmountParty2();
                break;
            case 8:
                ReturnValue = Zeile.Hint();
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

        getter.select(
                "SELECT haa.bezeichnung, haa.betrag, haa.aufteilungsart, haa.gilt_bis,\n" +
                        "p1.name || ', ' || SUBSTRING(p1.vorname, 1, 1) || '.' as party1, haaa1.betrag betragP1,\n" +
                        "p2.name || ', ' || SUBSTRING(p2.vorname, 1, 1) || '.' as party2, haaa2.betrag betragP2,\n" +
                        "haa.bemerkung\\n" + //
                        "from ha_ausgaben haa, ha_ausgaben_aufteilung haaa1, ha_ausgaben_aufteilung haaa2, personen p1, personen p2\n"
                        +
                        "where haaa1.\"ausgabenId\" = haa.\"ausgabenId\"\n" +
                        "and haaa1.\"parteiId\" = 2\n" +
                        "and haaa1.\"parteiId\" = p1.personen_id\n" +
                        "and haaa2.\"ausgabenId\" = haa.\"ausgabenId\"\n" +
                        "and haaa2.\"parteiId\" = 6\n" +
                        "and haaa2.\"parteiId\" = p2.personen_id\n" +
                        "order by gilt_bis DESC, haa.betrag DESC",
                2);

        rs = getter.getResultSet();
        try {
            rs.beforeFirst();

            while (rs.next()) {
                TableData.add(new ExpenditureDetailTableRow(rs.getString("bezeichung"), rs.getDouble("betrag"),
                        rs.getString("aufteilungsart"), rs.getDate("gilt_bis"), rs.getString("party1"), rs.getDouble("betragP1"),
                        rs.getString("party2"), rs.getDouble("betragP2"), rs.getString("bemerkung")));
            }
        } catch (Exception e) {
            System.out.println("ExpenditureDetailTableModel - lese_werte(): " + e.toString());
        }
    }
}
