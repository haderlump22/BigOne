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
    private JFormattedTextField billingMonth;
    private Connection LoginCN;
    private DBTools query;
    private List<ClosingSumValueRecord> closingSumValueRecord = new ArrayList<>();
    private JMenuItem addToPositiveSumPlaned, addToNegativeSumPlaned, addToPositiveSumUnplaned,
            addToNegativeSumUnplaned, removeDifferenceValueFromSum;

    public JointAccountClosingDetailTableMouseListener(JTable jointAccountClosingDetailTable,
            JFormattedTextField billingMonth, Connection LoginCN) {
        popmen = new JPopupMenu();
        this.LoginCN = LoginCN;
        query = new DBTools(LoginCN);
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.jointAccountClosingDetailTableModel = (JointAccountClosingDetailTableModel) this.jointAccountClosingDetailTable
                .getModel();
        this.billingMonth = billingMonth;

        addToPositiveSumPlaned = new JMenuItem("zu SUM+ geplant hinzufügen");
        addToPositiveSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned+");
            }
        });

        addToNegativeSumPlaned = new JMenuItem("zu SUM- geplant hinzufügen");
        addToNegativeSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned-");
            }
        });

        addToPositiveSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToPositiveSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned+");
            }
        });

        addToNegativeSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToNegativeSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned-");
            }
        });

        removeDifferenceValueFromSum = new JMenuItem("Betrag aus Summe entfernen");
        removeDifferenceValueFromSum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // model = (RACTableModel) table.getModel();
                // model.setAllLiquiToNull();
            }
        });

        popmen.add(addToPositiveSumPlaned);
        popmen.add(addToNegativeSumPlaned);
        popmen.add(addToPositiveSumUnplaned);
        popmen.add(addToNegativeSumUnplaned);
        popmen.addSeparator();
        popmen.add(removeDifferenceValueFromSum);
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

            // get the detailId of the, now, selected row of the
            // jointAccountClosingDetailTable
            detailId = (int) jointAccountClosingDetailTableModel
                    .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), -1);

            // disable addTo... entries if the ID from the row where the mouse was clicked
            // is already added to a sum type
            if (isRowDifferenceValueAlreadyAddedToAnySumType()) {
                setAddMenuEntrysDisabled();
            } else {
                if (isRowDifferenceValueZero) {
                    setAddMenuEntrysDisabled();
                } else {
                    setAddMenuEntrysEnabled();
                }
            }

            popmen.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private void setAddMenuEntrysDisabled() {
        addToNegativeSumPlaned.setEnabled(false);
        addToNegativeSumUnplaned.setEnabled(false);
        addToPositiveSumPlaned.setEnabled(false);
        addToPositiveSumUnplaned.setEnabled(false);
        removeDifferenceValueFromSum.setEnabled(true);
    }

    private void setAddMenuEntrysEnabled() {
        addToNegativeSumPlaned.setEnabled(true);
        addToNegativeSumUnplaned.setEnabled(true);
        addToPositiveSumPlaned.setEnabled(true);
        addToPositiveSumUnplaned.setEnabled(true);
        removeDifferenceValueFromSum.setEnabled(false);
    }

    private void addDetailIdToSumType(String sumType) {
        query.insert("""
                INSERT INTO ha_abschlusssummen
                ("summenArt", "abschlussDetailId")
                VALUES
                ('%s', %d)
                """.formatted(sumType, detailId));
    }

    private boolean isRowDifferenceValueAlreadyAddedToAnySumType() {
        int number = 0;

        query.select("""
                SELECT count(*)
                FROM ha_abschlusssummen
                WHERE "abschlussDetailId" = %d
                """.formatted(detailId), 1);

        try {
            query.first();
            number = query.getInt("count");
        } catch (SQLException e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
        }

        if (number != 0) {
            return true;
        } else {
            return false;
        }
    }

    // private ClosingSumValueRecord getClosingSumValueRow(String type, double
    // difference, String billingMonth) {
    // DBTools getter = new DBTools(LoginCN);
    // double typeClosingSumValue = 0;

    // getter.select("""
    // SELECT "abschlussSummenId", "abschlussMonat", "summenArt", betrag,
    // "detailQuelle"
    // FROM ha_abschlusssummen
    // WHERE "abschlussMonat" = '%s'
    // AND "summenArt" = '%s'
    // AND betrag %s
    // """.formatted(billingMonth, type, difference < 0 ? "< 0" : "> 0"), 1);

    // try {
    // getter.next();

    // typeClosingSumValue = getter.getDouble("betrag");

    // } catch (SQLException e) {
    // System.out.println(
    // this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": "
    // + e.toString());
    // System.exit(1);
    // }

    // return typeClosingSumValue;
    // }

    private void updateExistTypeOfClosingSumValue(String type, double newValueOfThisType, String billingMonth) {
        // DBTools setter = new DBTools(LoginCN);

        // setter.update("""
        // UPDATE ha_abschlusssummen
        // SET betrag = %d
        // WHERE "abschlussMonat" = '%s'
        // """.formatted(newValueOfThisType, billingMonth))
    }
}
