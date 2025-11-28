package de.rachel.bigone.listeners;

import java.sql.Connection;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Font;

import de.rachel.bigone.DBTools;

public class JointAccountClosingDetailTableSelectionListener implements ListSelectionListener {

    private JTable jointAccountClosingDetailTable;
    private JTextArea eventExpenditureAmountPlanInfoArea, eventInfoAreaAccountClosing;
    private Integer closingDetailId;
    private Connection cn;
    private DBTools getter;
    private JFormattedTextField sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue,
            sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue;

    public JointAccountClosingDetailTableSelectionListener(JTable jointAccountClosingDetailTable,
            JTextArea eventExpenditureAmountPlanInfoArea, Connection LoginCN, JTextArea eventInfoAreaAccountClosing,
            JFormattedTextField sumOverviewNegativePlanedValue, JFormattedTextField sumOverviewNegativeUnplanedValue,
            JFormattedTextField sumOverviewPositivePlanedValue,
            JFormattedTextField sumOverviewPositiveUnplanedValue) {
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.eventExpenditureAmountPlanInfoArea = eventExpenditureAmountPlanInfoArea;
        this.eventInfoAreaAccountClosing = eventInfoAreaAccountClosing;
        this.sumOverviewNegativePlanedValue = sumOverviewNegativePlanedValue;
        this.sumOverviewNegativeUnplanedValue = sumOverviewNegativeUnplanedValue;
        this.sumOverviewPositivePlanedValue = sumOverviewPositivePlanedValue;
        this.sumOverviewPositiveUnplanedValue = sumOverviewPositiveUnplanedValue;
        this.cn = LoginCN;
        getter = new DBTools(cn);
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        ListSelectionModel lsm = (ListSelectionModel) lse.getSource();

        // wenn auswahl fertig UND auch etwas selectiert ist
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            // get the EventId from the selected event form the
            // JointAccountClosingDetailTable
            closingDetailId = (Integer) this.jointAccountClosingDetailTable
                   .getValueAt(this.jointAccountClosingDetailTable.getSelectedRow(), -1);

            fillEventExpenditureAmountPlanInfoArea();
            fillEventInfoAreaAccountClosing();
            eventInfoAreaAccountClosing.setEnabled(true);
            markSumOverviewType();
        } else {
            eventInfoAreaAccountClosing.setEnabled(false);
        }
    }

    private void fillEventExpenditureAmountPlanInfoArea() {
        // we clear first the Content from a previous Call
        eventExpenditureAmountPlanInfoArea.setText("");

        // get Info from the expenditure table for the selected Event
        getter.select("""
                SELECT ha_ausgaben.bemerkung, ha_ausgaben.gilt_ab, ha_ausgaben.gilt_bis
                FROM ha_ausgaben, ha_abschlussdetails
                WHERE ha_abschlussdetails."abschlussDetailId" = %d
                AND ha_ausgaben.bezeichnung = ha_abschlussdetails."kategorieBezeichnung"
                AND ha_ausgaben.gilt_bis >= ha_abschlussdetails."abschlussMonat"
                AND ha_ausgaben.gilt_ab <= ha_abschlussdetails."abschlussMonat"
                """.formatted(closingDetailId),1);

        // define the text depending on the data obtained
        try {
            if (getter.getRowCount() > 1) {
                eventExpenditureAmountPlanInfoArea.setToolTipText("--Fehler - mehr als Einen Datensatz für das Ereignis im Ausgabenplan gefunden--");
            } else if (getter.getRowCount() == 0) {
                eventExpenditureAmountPlanInfoArea.setToolTipText("--kein Datensatz im AusgabenPlan für dieses Ereignis gefunden---");
            } else {
                getter.beforeFirst();
                getter.next();

                if (getter.getString("bemerkung").length() == 0) {
                    eventExpenditureAmountPlanInfoArea.setToolTipText("--kein Infos im AusgabenPlan für dieses Ereignis festgelegt---");
                } else {
                    eventExpenditureAmountPlanInfoArea.setText(getter.getString("bemerkung"));
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
    }

    private void fillEventInfoAreaAccountClosing() {
        // we clear first the Content from a previous Call
        eventInfoAreaAccountClosing.setText("");

        // get even saved Info for the selected Event
        getter.select("""
                SELECT bemerkung
                FROM ha_abschlussdetails
                WHERE "abschlussDetailId" = %d
                """.formatted(closingDetailId),1);

        try {
            if (getter.getRowCount() > 1) {
                eventInfoAreaAccountClosing.setToolTipText("--Fehler - mehr als Einen Datensatz für Infos dieses Ereignisses im Abschlussmonat gefunden--");
            } else if (getter.getRowCount() == 0) {
                eventInfoAreaAccountClosing.setToolTipText("--keine Infos für dieses Ereignis im Abschlussmonat gefunden---");
            } else {
                getter.beforeFirst();
                getter.next();

                if (getter.getString("bemerkung") == null) {
                    eventInfoAreaAccountClosing.setToolTipText("--Info für dieses Ereignis im Abschlussmonat ist leer---");
                } else {
                    eventInfoAreaAccountClosing.setText(getter.getString("bemerkung"));
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
    }

    private void markSumOverviewType() {
        // if the id from the selected Row is present in one of the 4
        // sumoverview types, set its Font to Bold
        DBTools dbTools = new DBTools(cn);
        String sumOverviewType = "";

        dbTools.select("""
                SELECT "summenArt"
                FROM ha_abschlusssummen
                WHERE "abschlussDetailId" = %d
                """.formatted(closingDetailId), 0);

        try {
            if (dbTools.getRowCount() > 0) {
                // it can be only one SumOverviewtye exist for an ID
                dbTools.first();

                sumOverviewType = dbTools.getString("summenArt");

                switch (sumOverviewType) {
                    case "planned+":
						sumOverviewPositivePlanedValue.setFont(new Font(null, Font.BOLD, 14));
                        sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
						break;
					case "planned-":
						sumOverviewNegativePlanedValue.setFont(new Font(null, Font.BOLD, 14));
                        sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
						break;
					case "unplanned+":
						sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.BOLD, 14));
                        sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
						break;
					case "unplanned-":
						sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.BOLD, 14));
                        sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                        sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
						break;
					default:
						break;
				}
            } else {
                sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
                sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
                sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
    }
}
