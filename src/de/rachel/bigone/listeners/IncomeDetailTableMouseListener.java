package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

// import de.rachel.bigone.Models.SalaryBasesIncomeDetailTableModel;

public class IncomeDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    // private SalaryBasesIncomeDetailTableModel model;

    public IncomeDetailTableMouseListener(JTable IncomeDetailTable) {
        popmen = new JPopupMenu();
        // Menüeintrag für das Löschen einer Zeile
        JMenuItem createSuccessor = new JMenuItem("nachfolger anlegen");
        createSuccessor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "ALERT MESSAGE", "TITLE", JOptionPane.WARNING_MESSAGE);
            }
        });
        popmen.add(createSuccessor);
    }

    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable IncomeDetailTable = (JTable) me.getSource();
            int RowAtMousePoint = IncomeDetailTable.rowAtPoint(me.getPoint());

            // vorherige Selection aufheben
            IncomeDetailTable.clearSelection();

            // diese eine Zeile selectieren
            IncomeDetailTable.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(me.getComponent(), me.getX(), me.getY());
        }
    }
}
