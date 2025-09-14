package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.rachel.bigone.models.RacTableModel;

import javax.swing.JMenuItem;

public class RacMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private RacTableModel model;

    public RacMouseListener(JTable table) {
        popmen = new JPopupMenu();
		// Menüeintrag für das Löschen einer Zeile
        JMenuItem delrow = new JMenuItem("Zeile löschen");
		delrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model = (RacTableModel) table.getModel();
				model.removeRow(table.getSelectedRow(), true);
			}
		});
        // Menüeintrag für das NULLen des Liquidatums
        JMenuItem NullLiqui = new JMenuItem("aktuellen Liqui=>NULL");
		NullLiqui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model = (RacTableModel) table.getModel();
				model.setLiquiToNull(table.getSelectedRow());
			}
		});
        // Menüeintrag für das NULLen aller Liquidatumswerte
        JMenuItem NullAllLiqui = new JMenuItem("alle Liqui=>NULL");
		NullAllLiqui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model = (RacTableModel) table.getModel();
				model.setAllLiquiToNull();
			}
		});
		popmen.add(delrow);
        popmen.add(NullLiqui);
        popmen.add(NullAllLiqui);
    }
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable table = (JTable) me.getSource();
            int RowAtMousePoint = table.rowAtPoint(me.getPoint());

            // vorherige Selection aufheben
            table.clearSelection();

            // diese eine Zeile selectieren
            table.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(me.getComponent(), me.getX(), me.getY());
        }
    }
}
