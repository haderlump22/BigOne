package de.rachel.bigone.listeners;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.event.*;
import java.sql.Connection;

import de.rachel.bigone.DBTools;

public class JointAccountClosingEventInfoAreaKeyListener extends KeyAdapter {
    private Connection cn = null;
    private DBTools theDB = null;
    private Boolean statusTimer = false;
    private JTable jointAccountClosingDetailTable = null;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // nothing to do at this time,
        }
    };

    public JointAccountClosingEventInfoAreaKeyListener(Connection LoginCN, JTable jointAccountClosingDetailTable) {
        cn = LoginCN;
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
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
        Integer closingDetailId = (Integer) jointAccountClosingDetailTable.getValueAt(jointAccountClosingDetailTable.getSelectedRow(), -1);
        String closingDetailNote = "";

        if (((JTextArea)(ke.getSource())).getText().length() == 0) {
            closingDetailNote = "NULL";
        } else {
            closingDetailNote = "'" + ((JTextArea)(ke.getSource())).getText() + "'";
        }

        theDB.update("""
                    UPDATE ha_abschlussdetails
                    SET bemerkung = %s
                    WHERE "abschlussDetailId" = %d
                    """.formatted(closingDetailNote, closingDetailId));
    }
}
