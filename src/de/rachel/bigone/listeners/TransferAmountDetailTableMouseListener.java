package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.rachel.bigone.dialogs.TransferAmountSuccessorDialog;
import de.rachel.bigone.models.TransferAmountDetailTableModel;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

// import de.rachel.bigone.Models.SalaryBasesIncomeDetailTableModel;

public class TransferAmountDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private Connection cn = null;
    // private SalaryBasesIncomeDetailTableModel model;

    public TransferAmountDetailTableMouseListener(JTable transferAmountDetailTable, JFrame transferAmountWindow, Connection loginCN) {
        cn = loginCN;

        popmen = new JPopupMenu();
        // Menüeintrag für das Löschen einer Zeile
        JMenuItem createSuccessor = new JMenuItem("nachfolger anlegen");
        createSuccessor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                final TransferAmountDetailTableModel modelOfSourceTable;

                // show Popup only on Rows where Datefield is not filled
                modelOfSourceTable = (TransferAmountDetailTableModel) transferAmountDetailTable.getModel();

                if (modelOfSourceTable.isDateFilled(transferAmountDetailTable.getSelectedRow())) {
                    JOptionPane.showMessageDialog(null,
                            "Nachfolger kann nicht angelegt werden.\nEnddatum schon enthalten",
                            "Nachfolger nicht möglich", JOptionPane.WARNING_MESSAGE);
                } else {
                    TransferAmountSuccessorDialog dialog = new TransferAmountSuccessorDialog(
                            modelOfSourceTable, transferAmountDetailTable.getSelectedRow(),
                            transferAmountWindow, cn);
                    if (dialog.areValuesCorrect()) {
                        dialog.createSuccessor(modelOfSourceTable, (int) transferAmountDetailTable.getValueAt(transferAmountDetailTable.getSelectedRow(), -1));
                    }
                }
            }
        });
        popmen.add(createSuccessor);
    }

    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable transferAmountDetailTable = (JTable) me.getSource();
            int RowAtMousePoint = transferAmountDetailTable.rowAtPoint(me.getPoint());

            // vorherige Selection aufheben
            transferAmountDetailTable.clearSelection();

            // diese eine Zeile selectieren
            transferAmountDetailTable.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(me.getComponent(), me.getX(), me.getY());
        }
    }
}
