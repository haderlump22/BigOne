package de.rachel.bigone.Listeners;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.Models.ExpenditureDistributionTableModel;

public class ExpenditureDetailTableSelectionListener implements ListSelectionListener {

    private JTable ExpenditureDetailTable, ExpenditureDistributionTable;
    private JScrollPane ExpenditureDistributionScrollPane;
    private JTextArea HintArea;
    private Integer ExpenditureId;
    private Integer zaehler = 0;

    public ExpenditureDetailTableSelectionListener(JTable ExpenditureDetailTable,
            JScrollPane ExpenditureDistributionScrollPane, JTable ExpenditureDistributionTable,
            JTextArea HintArea) {
        this.ExpenditureDetailTable = ExpenditureDetailTable;
        this.ExpenditureDistributionScrollPane = ExpenditureDistributionScrollPane;
        this.ExpenditureDistributionTable = ExpenditureDistributionTable;
        this.HintArea = HintArea;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            // refresh Tablemodeldata of the dristributiontable with the choosen id of the
            // detailtable
            ExpenditureId = (Integer) ExpenditureDetailTable.getValueAt(ExpenditureDetailTable.getSelectedRow(), -1);
            ((ExpenditureDistributionTableModel) ExpenditureDistributionTable.getModel()).aktualisiere(ExpenditureId);

            // refresh the content of the jtextarea with the hint from the choosen detailtable row
            // the hint is only in the tablemodel and is not show in the detailtable
            HintArea.setText((String) ExpenditureDetailTable.getValueAt(ExpenditureDetailTable.getSelectedRow(), -2));

            // if it's the first call of the listener then
            // change the component in the scrollpane from the jtextarea to the table
            if (zaehler == 0) {
                ExpenditureDistributionScrollPane.setViewportView(ExpenditureDistributionTable);
            }

            zaehler++;
        }
    }
}
