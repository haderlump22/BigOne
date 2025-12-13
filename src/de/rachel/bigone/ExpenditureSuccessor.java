package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ExpenditureSuccessor extends JFrame{
    private Connection cn = null;

    ExpenditureSuccessor(JFrame dialogOwner, double flGesBetrag, String strBuchText, Connection LoginCN) {
        cn = LoginCN;
		final JDialog dialog = new JDialog(dialogOwner, "Nachfolger erstellen", true);
		dialog.setSize(300, 250);
		dialog.setLayout(null);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

}
