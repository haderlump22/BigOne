package de.rachel.bigone.listeners;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ExpenditureDistributionTableSelectionListener implements ListSelectionListener {

    private JTable expenditureDistributionTable;
    private JTextArea distributionHintArea;

    public ExpenditureDistributionTableSelectionListener(JTable expenditureDistributionTable, JTextArea distributionHintArea) {
        this.expenditureDistributionTable = expenditureDistributionTable;
        this.distributionHintArea = distributionHintArea;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            // refresh the content of the jtextarea with the hint from the choosen distributionTable row
            // the hint is only in the tablemodel and is not show in the detailtable
            distributionHintArea.setText((String) expenditureDistributionTable.getValueAt(expenditureDistributionTable.getSelectedRow(), -1));
        }
    }
}
