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
    private DBTools theDB = null;
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
        theDB = new DBTools(this.cn);
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
        // get the EventId from the selected event form the JointAccountClosingDetailTable
        Integer ExpenditureEventId = (Integer) JointAccountClosingDetailTable.getValueAt(JointAccountClosingDetailTable.getSelectedRow(), -1);

        // check if Record for the selected AccountClosingEvent exist
        theDB.select("""
                    SELECT count(*) FROM ha_kategorie_infos_abschluss
                    WHERE abschluss_monat = '%s'
                    AND ha_kategorie_id = %d
                    """.formatted(BillingMonth.getText(), ExpenditureEventId),1);

        try {
            if (theDB.getInt("count") == 1) {
                theDB.update("""
                            UPDATE ha_kategorie_infos_abschluss
                            SET bemerkung = '%s'
                            WHERE abschluss_monat = '%s'
                            AND ha_kategorie_id = %d
                            """.formatted(((JTextArea)(ke.getSource())).getText(), BillingMonth.getText(), ExpenditureEventId));
            } else {
                theDB.insert("""
                            INSERT INTO ha_kategorie_infos_abschluss
                            (ha_kategorie_id, bemerkung, abschluss_monat)
                            VALUES
                            (%d, '%s', '%s')
                            """.formatted(ExpenditureEventId, ((JTextArea)(ke.getSource())).getText(), BillingMonth.getText()));
            }
        } catch (SQLException e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
        }



    }
}
