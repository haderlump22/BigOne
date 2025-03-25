package de.rachel.bigone.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class TransferAmountDetailTableCreateSuccessorDialog extends JFrame {
    private JLabel lblDatum, lblBezeichnung;
    private JTextField txtDatum, txtBezeichnung;

    public TransferAmountDetailTableCreateSuccessorDialog(JFrame dialogOwner) {
        final JDialog dialog = new JDialog(dialogOwner, "Ausgabennachfolger erstellen", true);
		dialog.setSize(220,250);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container pane = dialog.getContentPane();

        pane.add(lblDatum);
        // pane.add(txtDatum, BorderLayout.CENTER);

        // pane.add(lblBezeichnung, BorderLayout.LINE_START);
        // pane.add(txtBezeichnung, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

}
