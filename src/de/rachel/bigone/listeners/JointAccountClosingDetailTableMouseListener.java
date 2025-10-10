package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;
import de.rachel.bigone.records.ClosingSumValueRecord;
import de.rachel.bigone.records.JointAccountClosingDetailTableRow;

public class JointAccountClosingDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JointAccountClosingDetailTableModel jointAccountClosingDetailTableModel;
    private JTable jointAccountClosingDetailTable;
    private int detailId;
    private double differenceValue;
    private JFormattedTextField billingMonth;
    private Connection sqlConnection;
    private List<ClosingSumValueRecord> closingSumValueRecord = new ArrayList<>();

    public JointAccountClosingDetailTableMouseListener(JTable jointAccountClosingDetailTable,
            JFormattedTextField billingMonth, Connection sqlConnection) {
        popmen = new JPopupMenu();
        this.sqlConnection = sqlConnection;
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.jointAccountClosingDetailTableModel = (JointAccountClosingDetailTableModel) this.jointAccountClosingDetailTable
                .getModel();
        this.billingMonth = billingMonth;

        JMenuItem addToPositiveSumPlaned = new JMenuItem("zu SUM+ geplant hinzufügen");
        addToPositiveSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                differenceValue = (double) jointAccountClosingDetailTableModel
                        .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), 3);
                ;
                detailId = (int) jointAccountClosingDetailTableModel
                        .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), -1);

                modifyClosingSumValue("planned", differenceValue, billingMonth.getText(), detailId);
            }
        });

        JMenuItem addToNegativeSumPlaned = new JMenuItem("zu SUM- geplant hinzufügen");
        addToNegativeSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // model = (RACTableModel) table.getModel();
                // model.setLiquiToNull(table.getSelectedRow());
            }
        });

        JMenuItem addToPositiveSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToPositiveSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // model = (RACTableModel) table.getModel();
                // model.setAllLiquiToNull();
            }
        });

        JMenuItem addToNegativeSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToNegativeSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // model = (RACTableModel) table.getModel();
                // model.setAllLiquiToNull();
            }
        });
        popmen.add(addToPositiveSumPlaned);
        popmen.add(addToNegativeSumPlaned);
        popmen.add(addToPositiveSumUnplaned);
        popmen.add(addToNegativeSumUnplaned);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable jointAccountClosingDetailTable = (JTable) mouseEvent.getSource();
            int rowAtMousePoint = jointAccountClosingDetailTable.rowAtPoint(mouseEvent.getPoint());

            // vorherige Selection aufheben
            jointAccountClosingDetailTable.clearSelection();

            // diese eine Zeile selectieren
            jointAccountClosingDetailTable.addRowSelectionInterval(rowAtMousePoint, rowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private void modifyClosingSumValue(String type, double difference, String billingMonth, int detailId) {
        if (existTypeOfClosingSumValue(type, difference, billingMonth)) {
            closingSumValueRecord.add(getClosingSumValueRow(type, difference, billingMonth));

            double newValueOfThisType = oldValueOfThisType + difference;

            updateExistTypeOfClosingSumValue(type, newValueOfThisType, billingMonth, detailId);
        } else {
            createNewTypeOfClosingSumValue(type, difference, billingMonth);
        }
    }

    private boolean existTypeOfClosingSumValue(String type, double difference, String billingMonth) {
        // the record that we looking for may only occur once or not at all
        DBTools getter = new DBTools(sqlConnection);
        int anzahl = 0;

        getter.select("""
                SELECT count(*)
                FROM ha_abschlusssummen
                WHERE "abschlussMonat" = '%s'
                AND "summenArt" = '%s'
                AND betrag %s
                """.formatted(billingMonth, type, difference < 0 ? "< 0" : (difference > 0 ? "> 0" : "= 0")), 1);

        try {
            getter.next();

            anzahl = getter.getInt("count");

        } catch (SQLException e) {
            System.out.println(
                    this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
            System.exit(1);
        }

        return anzahl == 1 ? true : false;
    }

    private ClosingSumValueRecord getClosingSumValueRow(String type, double difference, String billingMonth) {
        DBTools getter = new DBTools(sqlConnection);
        double typeClosingSumValue = 0;

        getter.select("""
                SELECT "abschlussSummenId", "abschlussMonat", "summenArt", betrag, "detailQuelle"
                FROM ha_abschlusssummen
                WHERE "abschlussMonat" = '%s'
                AND "summenArt" = '%s'
                AND betrag %s
                """.formatted(billingMonth, type, difference < 0 ? "< 0" : "> 0"), 1);

        try {
            getter.next();

            typeClosingSumValue = getter.getDouble("betrag");

        } catch (SQLException e) {
            System.out.println(
                    this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
            System.exit(1);
        }

        return typeClosingSumValue;
    }

    private void updateExistTypeOfClosingSumValue(String type, double newValueOfThisType, String billingMonth) {
        DBTools setter = new DBTools(sqlConnection);

        setter.update("""
                UPDATE ha_abschlusssummen
                SET betrag = %d
                WHERE "abschlussMonat" = '%s'
                """.formatted(newValueOfThisType, billingMonth))
    }
}
