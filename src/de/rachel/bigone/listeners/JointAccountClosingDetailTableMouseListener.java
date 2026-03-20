package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JMenuItem;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.JointAccountClosing;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JointAccountClosingDetailTableModel jointAccountClosingDetailTableModel;
    private JTable jointAccountClosingDetailTable;
    private int detailId;
    private DBTools query;
    private JMenuItem addToPositiveSumPlaned, addToNegativeSumPlaned, addToPositiveSumUnplaned,
            addToNegativeSumUnplaned, removeDifferenceValueFromSum, resetBillingMonthPreparedData;
    private JointAccountClosing jointAccountClosingUi;
    private String billingMonth = "";

    public JointAccountClosingDetailTableMouseListener(JTable jointAccountClosingDetailTable, Connection LoginCN, JointAccountClosing jointAccountClosingUi) {
        popmen = new JPopupMenu();
        query = new DBTools(LoginCN);
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.jointAccountClosingDetailTableModel = (JointAccountClosingDetailTableModel) this.jointAccountClosingDetailTable
                .getModel();
        this.jointAccountClosingUi = jointAccountClosingUi;

        addToPositiveSumPlaned = new JMenuItem("zu SUM+ geplant hinzufügen");
        addToPositiveSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned+");
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        addToNegativeSumPlaned = new JMenuItem("zu SUM- geplant hinzufügen");
        addToNegativeSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("planned-");
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        addToPositiveSumUnplaned = new JMenuItem("zu SUM+ ungeplant hinzufügen");
        addToPositiveSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned+");
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        addToNegativeSumUnplaned = new JMenuItem("zu SUM- ungeplant hinzufügen");
        addToNegativeSumUnplaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                addDetailIdToSumType("unplanned-");
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        removeDifferenceValueFromSum = new JMenuItem("Betrag aus Summe entfernen");
        removeDifferenceValueFromSum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                removeDetailIdFromSumType();
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        resetBillingMonthPreparedData = new JMenuItem("Abschlussvorbereitung zurücksetzen");
        resetBillingMonthPreparedData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                removeJointAccountClosingDetailData();
                jointAccountClosingUi.clearBillingMonth();
                jointAccountClosingDetailTableModel.setDetailIdsForMarkingDifferenceValue(new Integer[0]);
                // we give an empty BillingMonth because we whant to clear the JTable
                jointAccountClosingDetailTableModel.aktualisiere("");
                jointAccountClosingUi.fillSumOverview();
                jointAccountClosingUi.fillBallaceAllocationOverview();
            }
        });

        popmen.add(addToPositiveSumPlaned);
        popmen.add(addToNegativeSumPlaned);
        popmen.add(addToPositiveSumUnplaned);
        popmen.add(addToNegativeSumUnplaned);
        popmen.addSeparator();
        popmen.add(removeDifferenceValueFromSum);
        popmen.add(resetBillingMonthPreparedData);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        // first we renew the billingMonth Value from the UI
        billingMonth = jointAccountClosingUi.getBillingMonth();

        // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
        // wird diese Zeile erst selectiert
        JTable jointAccountClosingDetailTable = (JTable) mouseEvent.getSource();
        int rowAtMousePoint = jointAccountClosingDetailTable.rowAtPoint(mouseEvent.getPoint());

        // vorherige Selection aufheben
        jointAccountClosingDetailTable.clearSelection();

        // diese eine Zeile selectieren
        jointAccountClosingDetailTable.addRowSelectionInterval(rowAtMousePoint, rowAtMousePoint);

        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            // get the detailId of the, now, selected row of the
            // jointAccountClosingDetailTable
            detailId = (int) jointAccountClosingDetailTableModel
                    .getValueAt(jointAccountClosingDetailTable.getSelectedRow(), -1);

            // if billing month already closed disable the possibility
            // to manipulate the Value Counting
            if (jointAccountClosingUi.getBillingMonthAlreadyClosed()) {
                resetBillingMonthPreparedData.setEnabled(false);
                setAddMenuEntrysDisabled(true);
            } else {
                resetBillingMonthPreparedData.setEnabled(true);
                // disable addTo... entries if the ID from the row where the mouse was clicked
                // is already added to a sum type
                if (isRowDifferenceValueAlreadyAddedToAnySumType()) {
                    setAddMenuEntrysDisabled(false);
                } else {
                    // if the difference Value is Zero then they can't add to any SumType
                    if (isRowDifferenceValueZero()) {
                        setAddMenuEntrysDisabled(true);
                    } else {
                        setAddMenuEntrysEnabled();
                    }
                }
            }

            popmen.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }

        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            jointAccountClosingDetailTableModel.setDetailIdsForMarkingDifferenceValue(new Integer[0]);

            // diese eine Zeile selectieren
            jointAccountClosingDetailTable.addRowSelectionInterval(rowAtMousePoint, rowAtMousePoint);
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
                """.formatted(detailId));

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

    private void removeJointAccountClosingDetailData() {
        // the referenced Rows in ha_abschlusssummen are also
        // deletet, but it happend automaticly by an foreign Key in the db
        // between the parrent table ha_abschlussdetails and child table ha_abschlusssummen
        query.update("""
                DELETE FROM ha_abschlussdetails
                WHERE "abschlussMonat" = '%s'
                """.formatted(billingMonth));
    }
}
