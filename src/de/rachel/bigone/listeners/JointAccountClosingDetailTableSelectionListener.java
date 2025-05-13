package de.rachel.bigone.listeners;

import java.sql.Connection;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.DBTools;

public class JointAccountClosingDetailTableSelectionListener implements ListSelectionListener {

    private JTable JointAccountClosingDetailTable;
    private JTextArea EventExpenditureAmountPlanInfoArea;
    private Integer ExpenditureEventId;
    private Connection cn;
    private DBTools getter;
    private String strBillingMonth;

    public JointAccountClosingDetailTableSelectionListener(JTable JointAccountClosingDetailTable,
            JTextArea EventExpenditureAmountPlanInfoArea, Connection LoginCN, String strBillingMonth) {
        this.JointAccountClosingDetailTable = JointAccountClosingDetailTable;
        this.EventExpenditureAmountPlanInfoArea = EventExpenditureAmountPlanInfoArea;
        this.cn = LoginCN;
        this.strBillingMonth = strBillingMonth;
        getter = new DBTools(cn);
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {

        ListSelectionModel lsm = (ListSelectionModel) lse.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            this.fillEventExpenditureAmountPlanInfoArea();
            this.fillEventInfoAreaAccountClosing();
        }
    }

    private void fillEventExpenditureAmountPlanInfoArea() {
        String ContentForTextArea;
        // get the EventId from the selected event form the JointAccountClosingDetailTable
        ExpenditureEventId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // get Info from the expenditure table for the specified Event
        getter.select(
                "select ha_ausgaben.bemerkung from ha_ausgaben, ha_kategorie\n" +
                "where ha_kategorie.kategoriebezeichnung = ha_ausgaben.bezeichnung\n" +
                "and ha_ausgaben.gilt_bis is null\n" +
                "and ha_kategorie.ha_kategorie_id = " + ExpenditureEventId,
                1);

        // define the text depending on the data obtained
        try {
            if (getter.getRowCount() > 1) {
                ContentForTextArea = "--Fehler - mehr als Einen Datensatz für das Ereignis im Ausgabenplan gefunden--";
            } else if (getter.getRowCount() == 0) {
                ContentForTextArea = "--kein Datensatz im AusgabenPlan für dieses Ereignis gefunden---";
            } else {
                getter.beforeFirst();
                getter.next();
                ContentForTextArea = getter.getString("bemerkung");

                if (ContentForTextArea.length() == 0) {
                    ContentForTextArea = "--kein Infos im AusgabenPlan für dieses Ereignis festgelegt---";
                }
            }

            EventExpenditureAmountPlanInfoArea.setText(ContentForTextArea);
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
        }
    }

    private void fillEventInfoAreaAccountClosing() {
        // get even saved Info for the selected Evend and his Amount for AccountClosing of the Month in the Textfield BillingMonth
    }
}
