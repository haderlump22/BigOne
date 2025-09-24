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
    private Integer closingDetailId;
    private Connection cn;
    private DBTools getter;

    public JointAccountClosingDetailTableSelectionListener(JTable JointAccountClosingDetailTable,
            JTextArea EventExpenditureAmountPlanInfoArea, Connection LoginCN, JTextArea EventInfoAreaAccountClosing) {
        this.JointAccountClosingDetailTable = JointAccountClosingDetailTable;
        this.EventExpenditureAmountPlanInfoArea = EventExpenditureAmountPlanInfoArea;
        this.EventInfoAreaAccountClosing = EventInfoAreaAccountClosing;
        this.cn = LoginCN;
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
        // we clear first the Content from a previous Call
        EventExpenditureAmountPlanInfoArea.setText("");

        // get the EventId from the selected event form the JointAccountClosingDetailTable
        closingDetailId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // get Info from the expenditure table for the selected Event
        getter.select("""
                SELECT ha_ausgaben.bemerkung, ha_ausgaben.gilt_ab, ha_ausgaben.gilt_bis FROM ha_ausgaben, ha_abschlussdetails
                WHERE ha_abschlussdetails."abschlussDetailId" = %d
                AND ha_ausgaben.bezeichnung = ha_abschlussdetails."kategorieBezeichnung"
                AND ha_ausgaben.gilt_bis >= ha_abschlussdetails."abschlussMonat"
                AND ha_ausgaben.gilt_ab <= ha_abschlussdetails."abschlussMonat"
                """.formatted(closingDetailId),1);

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
        // we clear first the Content from a previous Call
        EventInfoAreaAccountClosing.setText("");

        // get the EventId from the selected event form the JointAccountClosingDetailTable
        closingDetailId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // get even saved Info for the selected Event
        getter.select("""
                SELECT bemerkung from ha_abschlussdetails
                WHERE "abschlussDetailId" = %d
                """.formatted(closingDetailId),1);

        try {
            if (getter.getRowCount() > 1) {
                EventInfoAreaAccountClosing.setToolTipText("--Fehler - mehr als Einen Datensatz für Infos dieses Ereignisses im Abschlussmonat gefunden--");
            } else if (getter.getRowCount() == 0) {
                EventInfoAreaAccountClosing.setToolTipText("--keine Infos für dieses Ereignis im Abschlussmonat gefunden---");
            } else {
                getter.beforeFirst();
                getter.next();

                if (getter.getString("bemerkung") == null) {
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
