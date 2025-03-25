package de.rachel.bigone.Listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.rachel.bigone.dialogs.TransferAmountDetailTableCreateSuccessorDialog;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

// import de.rachel.bigone.Models.SalaryBasesIncomeDetailTableModel;

public class TransferAmountDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    // private SalaryBasesIncomeDetailTableModel model;

    public TransferAmountDetailTableMouseListener(JTable TransferAmountDetailTable, JFrame TransferAmountWindow) {
        popmen = new JPopupMenu();
        // Menüeintrag für das Löschen einer Zeile
        JMenuItem createSuccessor = new JMenuItem("nachfolger anlegen");
        createSuccessor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                TransferAmountDetailTableCreateSuccessorDialog dialog = new TransferAmountDetailTableCreateSuccessorDialog(TransferAmountWindow);
                JOptionPane.showMessageDialog(null, "ALERT MESSAGE", "TITLE", JOptionPane.WARNING_MESSAGE);
            }
        });
        popmen.add(createSuccessor);
    }

    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable TransferAmountDetailTable = (JTable) me.getSource();
            int RowAtMousePoint = TransferAmountDetailTable.rowAtPoint(me.getPoint());

            // vorherige Selection aufheben
            TransferAmountDetailTable.clearSelection();

            // diese eine Zeile selectieren
            TransferAmountDetailTable.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(me.getComponent(), me.getX(), me.getY());
        }
    }
}
