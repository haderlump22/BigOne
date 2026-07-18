package de.rachel.bigone.listeners;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.models.ExpenditureDistributionTableModel;

public class ExpenditureDetailTableSelectionListener implements ListSelectionListener {

    private JTable expenditureDetailTable, expenditureDistributionTable;
    private JScrollPane expenditureDistributionScrollPane;
    private JTextArea hintArea;
    private Integer expenditureId;
    private Integer zaehler = 0;

    public ExpenditureDetailTableSelectionListener(JTable expenditureDetailTable,
            JScrollPane expenditureDistributionScrollPane, JTable expenditureDistributionTable,
            JTextArea hintArea) {
        this.expenditureDetailTable = expenditureDetailTable;
        this.expenditureDistributionScrollPane = expenditureDistributionScrollPane;
        this.expenditureDistributionTable = expenditureDistributionTable;
        this.hintArea = hintArea;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            // refresh Tablemodeldata of the dristributiontable with the choosen id of the
            // detailtable
            expenditureId = (Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -1);
            ((ExpenditureDistributionTableModel) expenditureDistributionTable.getModel()).aktualisiere(expenditureId);

            // refresh the content of the jtextarea with the hint from the choosen detailtable row
            // the hint is only in the tablemodel and is not show in the detailtable
            hintArea.setText((String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -2));

            // if it's the first call of the listener then
            // change the component in the scrollpane from the jtextarea to the table
            if (zaehler == 0) {
                expenditureDistributionScrollPane.setViewportView(expenditureDistributionTable);
            }

            zaehler++;
        }
    }

    public void resetCallCounter() {
        // we has to set the Counter to 0 if Data had refreshed
        // for example in the Case of update the ratioShare Data
        zaehler = 0;
    }
}
