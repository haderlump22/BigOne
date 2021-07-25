package de.rachel.bigone.Listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JMenuItem;

import de.rachel.bigone.Models.RACTableModel;

public class RACMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private RACTableModel model;

    public RACMouseListener(JTable table) {
        popmen = new JPopupMenu();
		JMenuItem delrow = new JMenuItem("Zeile löschen");
		delrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model = (RACTableModel) table.getModel();
				model.removeRow(table.getSelectedRow(), true);
			}
		});
		popmen.add(delrow);
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
