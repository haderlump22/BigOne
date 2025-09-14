package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JMenuItem;

import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JointAccountClosingDetailTableModel model;

    public JointAccountClosingMouseListener(JTable table) {
        popmen = new JPopupMenu();

        JMenuItem addToPositiveSumPlaned = new JMenuItem("zu SUM+ geplant hinzufügen");
        addToPositiveSumPlaned.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // model = (RACTableModel) table.getModel();
                // model.removeRow(table.getSelectedRow(), true);
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
            JTable table = (JTable) mouseEvent.getSource();
            int RowAtMousePoint = table.rowAtPoint(mouseEvent.getPoint());

            // vorherige Selection aufheben
            table.clearSelection();

            // diese eine Zeile selectieren
            table.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        }
    }
}
