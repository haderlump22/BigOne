package de.rachel.bigone.Listeners;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.Models.ExpenditureDistributionTableModel;

public class ExpenditureDetailTableSelectionListener implements ListSelectionListener {

    private JTable ExpenditureDetailTable, ExpenditureDistributionTable;
    private JScrollPane ExpenditureDistributionScrollPane;
    private Integer ExpenditureId;
    private Integer zaehler = 0;

    public ExpenditureDetailTableSelectionListener(JTable ExpenditureDetailTable,
            JScrollPane ExpenditureDistributionScrollPane, JTable ExpenditureDistributionTable) {
        this.ExpenditureDetailTable = ExpenditureDetailTable;
        this.ExpenditureDistributionScrollPane = ExpenditureDistributionScrollPane;
        this.ExpenditureDistributionTable = ExpenditureDistributionTable;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        // wenn auswahl fertig
        if (!lsm.getValueIsAdjusting()) {
            ExpenditureId = (Integer) ExpenditureDetailTable.getValueAt(ExpenditureDetailTable.getSelectedRow(), -1);
            ((ExpenditureDistributionTableModel) ExpenditureDistributionTable.getModel()).aktualisiere(ExpenditureId);

            // if it's the first call of the listener then
            // change the component in the scrollpane to the table
            if (zaehler == 0) {
                ExpenditureDistributionScrollPane.setViewportView(ExpenditureDistributionTable);
            }

            zaehler++;
        }
    }
}
