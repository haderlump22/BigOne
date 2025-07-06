package de.rachel.bigone.listeners;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

import java.awt.event.*;
import java.sql.Connection;

public class EventInfoAreaAccountClosingKeyListener extends KeyAdapter {
    Connection cn = null;
    Boolean statusTimer = false;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // do nothing for this time,
        }
    };

    public EventInfoAreaAccountClosingKeyListener(Connection LoginCN) {
        cn = LoginCN;
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

        // check if Record for the selected AccountClosingEvent exist

        // if it so, update it

        // if not insert a new one for this Event and BillingMonth Comination
    }
}
