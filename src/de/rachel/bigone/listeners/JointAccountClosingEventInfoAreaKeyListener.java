package de.rachel.bigone.listeners;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

import de.rachel.bigone.DBTools;

public class JointAccountClosingEventInfoAreaKeyListener extends KeyAdapter {
    private Connection cn = null;
    private DBTools getter = null;
    private Boolean statusTimer = false;
    private JFormattedTextField BillingMonth = null;
    private JTable JointAccountClosingDetailTable = null;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // do nothing for this time,
        }
    };

    public JointAccountClosingEventInfoAreaKeyListener(Connection LoginCN, JFormattedTextField BillingMonth, JTable JointAccountClosingDetailTable) {
        cn = LoginCN;
        this.BillingMonth = BillingMonth;
        this.JointAccountClosingDetailTable = JointAccountClosingDetailTable;
        getter = new DBTools(this.cn);
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyReleased(KeyEvent ke) {

        if (statusTimer.equals(Boolean.TRUE)) {
            task.cancel();
            task = new TimerTask() {
                @Override
                public void run() {
                    saveEventInfoAreaValue(ke);
                }
            };
        }

        timer.schedule(task, 3000);
        statusTimer = Boolean.TRUE;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    public void saveEventInfoAreaValue(KeyEvent ke) {
        System.out.println("\"" + ((JTextArea)(ke.getSource())).getText() +  "\" .. wurde gespeichert....");

        // get the EventId from the selected event form the JointAccountClosingDetailTable
        Integer ExpenditureEventId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // check if Record for the selected AccountClosingEvent exist
        getter.select("""
                    SELECT count(*) FROM ha_kategorie_infos_abschluss
                    WHERE abschluss_monat = '%s'
                    AND ha_kategorie_id = %d
                    """.formatted(BillingMonth.getText(), ExpenditureEventId),1);

        /*
        try {
            if (getter.getInt("count") == 1) {
                // if it so, update it
            } else {
                // if not insert a new one for this Event and BillingMonth Comination
            }
        } catch (SQLException e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
        }*/



    }
}
