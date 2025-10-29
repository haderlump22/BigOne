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
import de.rachel.bigone.JointAccountClosing;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JointAccountClosingDetailTableModel jointAccountClosingDetailTableModel;
    private JointAccountClosing jointAccountClosingUi;
    private JTable jointAccountClosingDetailTable;
    private int detailId;
    private DBTools query;
    private JMenuItem addToPositiveSumPlaned, addToNegativeSumPlaned, addToPositiveSumUnplaned,
            addToNegativeSumUnplaned, removeDifferenceValueFromSum;

    public JointAccountClosingDetailTableMouseListener(JTable jointAccountClosingDetailTable,
            JFormattedTextField billingMonth, Connection LoginCN, JointAccountClosing jointAccountClosingUi) {
        popmen = new JPopupMenu();
        query = new DBTools(LoginCN);
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.jointAccountClosingDetailTableModel = (JointAccountClosingDetailTableModel) this.jointAccountClosingDetailTable
                .getModel();

        addToPositiveSumPlaned = new JMenuItem("zu SUM+ geplant hinzufügen");
        addToPositiveSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned+");
                jointAccountClosingUi.fillSumOverview();
            }
        });

        addToNegativeSumPlaned = new JMenuItem("zu SUM- geplant hinzufügen");
        addToNegativeSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned-");
                jointAccountClosingUi.fillSumOverview();
            }
        });

        addToPositiveSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToPositiveSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned+");
                jointAccountClosingUi.fillSumOverview();
            }
        });

        addToNegativeSumUnplaned = new JMenuItem("zu SUM- ungeplant hinzufügen");
        addToNegativeSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned-");
                jointAccountClosingUi.fillSumOverview();
            }
        });

        removeDifferenceValueFromSum = new JMenuItem("Betrag aus Summe entfernen");
        removeDifferenceValueFromSum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                removeDetailIdFromSumType();
                jointAccountClosingUi.fillSumOverview();
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
                setAddMenuEntrysDisabled(false);
            } else {
                if (isRowDifferenceValueZero()) {
                    setAddMenuEntrysDisabled(true);
                } else {
                    setAddMenuEntrysEnabled();
                }
            }

            popmen.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private void setAddMenuEntrysDisabled(boolean allEntrys) {
        addToNegativeSumPlaned.setEnabled(false);
        addToNegativeSumUnplaned.setEnabled(false);
        addToPositiveSumPlaned.setEnabled(false);
        addToPositiveSumUnplaned.setEnabled(false);
        if (allEntrys) {
            removeDifferenceValueFromSum.setEnabled(false);
        } else {
            removeDifferenceValueFromSum.setEnabled(true);
        }
    }

    private void setAddMenuEntrysEnabled() {
        if (isRowDifferenceValuePositive()) {
            addToNegativeSumPlaned.setEnabled(false);
            addToNegativeSumUnplaned.setEnabled(false);
            addToPositiveSumPlaned.setEnabled(true);
            addToPositiveSumUnplaned.setEnabled(true);
        } else {
            addToNegativeSumPlaned.setEnabled(true);
            addToNegativeSumUnplaned.setEnabled(true);
            addToPositiveSumPlaned.setEnabled(false);
            addToPositiveSumUnplaned.setEnabled(false);
        }
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
                SELECT COUNT(*)
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

    private boolean isRowDifferenceValueZero() {
        double differenceValueOfSelectedRow = (double) jointAccountClosingDetailTableModel
                .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), 3);
        if (differenceValueOfSelectedRow == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRowDifferenceValuePositive() {
        double differenceValueOfSelectedRow = (double) jointAccountClosingDetailTableModel
                .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), 3);
        if (differenceValueOfSelectedRow > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void removeDetailIdFromSumType() {
        query.insert("""
                DELETE FROM ha_abschlusssummen
                WHERE "abschlussDetailId" = %d
                """.formatted(detailId));
    }
}
