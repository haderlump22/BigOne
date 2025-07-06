package de.rachel.bigone.listeners;

import java.sql.Connection;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.DBTools;

public class JointAccountClosingDetailTableSelectionListener implements ListSelectionListener {

    private JTable JointAccountClosingDetailTable;
    private JTextArea EventExpenditureAmountPlanInfoArea, EventInfoAreaAccountClosing;
    private Integer ExpenditureEventId;
    private Connection cn;
    private DBTools getter;
    private JFormattedTextField BillingMonth;

    public JointAccountClosingDetailTableSelectionListener(JTable JointAccountClosingDetailTable,
            JTextArea EventExpenditureAmountPlanInfoArea, Connection LoginCN, JFormattedTextField BillingMonth, JTextArea EventInfoAreaAccountClosing) {
        this.JointAccountClosingDetailTable = JointAccountClosingDetailTable;
        this.EventExpenditureAmountPlanInfoArea = EventExpenditureAmountPlanInfoArea;
        this.EventInfoAreaAccountClosing = EventInfoAreaAccountClosing;
        this.cn = LoginCN;
        this.BillingMonth = BillingMonth;
        getter = new DBTools(cn);
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {

        ListSelectionModel lsm = (ListSelectionModel) lse.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            this.fillEventExpenditureAmountPlanInfoArea();
            this.fillEventInfoAreaAccountClosing();
            this.EventInfoAreaAccountClosing.setEnabled(true);
        } else {
            this.EventInfoAreaAccountClosing.setEnabled(false);
        }
    }

    private void fillEventExpenditureAmountPlanInfoArea() {
        // get the EventId from the selected event form the JointAccountClosingDetailTable
        ExpenditureEventId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // get Info from the expenditure table for the selected Event
        getter.select("""
                select ha_ausgaben.bemerkung from ha_ausgaben, ha_kategorie
                where ha_kategorie.kategoriebezeichnung = ha_ausgaben.bezeichnung
                and ha_ausgaben.gilt_bis is null
                and ha_kategorie.ha_kategorie_id = %d
                """.formatted(ExpenditureEventId),1);

        // define the text depending on the data obtained
        try {
            if (getter.getRowCount() > 1) {
                EventExpenditureAmountPlanInfoArea.setToolTipText("--Fehler - mehr als Einen Datensatz für das Ereignis im Ausgabenplan gefunden--");
            } else if (getter.getRowCount() == 0) {
                EventExpenditureAmountPlanInfoArea.setToolTipText("--kein Datensatz im AusgabenPlan für dieses Ereignis gefunden---");
            } else {
                getter.beforeFirst();
                getter.next();

                if (getter.getString("bemerkung").length() == 0) {
                    EventExpenditureAmountPlanInfoArea.setToolTipText("--kein Infos im AusgabenPlan für dieses Ereignis festgelegt---");
                } else {
                    EventExpenditureAmountPlanInfoArea.setText(getter.getString("bemerkung"));
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
        }
    }

    private void fillEventInfoAreaAccountClosing() {
        // get the EventId from the selected event form the JointAccountClosingDetailTable
        ExpenditureEventId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // get even saved Info for the selected Event
        getter.select("""
                SELECT bemerkung from ha_kategorie_infos_abschluss
                WHERE abschluss_monat = '%s'
                AND ha_kategorie_id = %d
                """.formatted(BillingMonth.getText(), ExpenditureEventId),1);

        try {
            if (getter.getRowCount() > 1) {
                EventInfoAreaAccountClosing.setToolTipText("--Fehler - mehr als Einen Datensatz für Infos dieses Ereignisses im Abschlussmonat gefunden--");
            } else if (getter.getRowCount() == 0) {
                EventInfoAreaAccountClosing.setToolTipText("--keine Infos für dieses Ereignis im Abschlussmonat gefunden---");
            } else {
                getter.beforeFirst();
                getter.next();

                if (getter.getString("bemerkung").length() == 0) {
                    EventInfoAreaAccountClosing.setToolTipText("--Info für dieses Ereignis im Abschlussmonat ist leer---");
                } else {
                    EventInfoAreaAccountClosing.setText(getter.getString("bemerkung"));
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
        }
    }
}
