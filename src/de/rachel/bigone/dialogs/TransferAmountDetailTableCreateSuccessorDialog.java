package de.rachel.bigone.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

public class TransferAmountDetailTableCreateSuccessorDialog extends JFrame {
    private JLabel lblPersonOfTransferAmount, lblTransferAmountValue, lblHeadline;
    private JTextField txtPersonOfTransferAmount;
    private JFormattedTextField txtTransferAmountValue;
    private JButton btnSaveNewTransferAmount;
    private final JDialog newTransferAmountDialog;

    public TransferAmountDetailTableCreateSuccessorDialog(JFrame dialogOwner) {
        newTransferAmountDialog = new JDialog(dialogOwner, "Ausgabennachfolger", true);
        newTransferAmountDialog.setSize(320, 180);
        newTransferAmountDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        newTransferAmountDialog.setResizable(false);

        lblHeadline = new JLabel("Infos für neuen Überweisungsbetrag");

        lblPersonOfTransferAmount = new JLabel("Person für Ausgabe");
        lblPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));

        txtPersonOfTransferAmount = new JTextField();
        txtPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));

        lblTransferAmountValue = new JLabel("Ausgaben Betrag");
        lblTransferAmountValue.setPreferredSize(new Dimension(150, 25));

        txtTransferAmountValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        txtTransferAmountValue.setText("0,00");
        txtTransferAmountValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTransferAmountValue.setPreferredSize(new Dimension(75, 25));
        txtTransferAmountValue.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent fe) {
            }

            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        txtTransferAmountValue.selectAll();
                    }
                });
            }
        });

        btnSaveNewTransferAmount = new JButton("Speichern");
        btnSaveNewTransferAmount.setPreferredSize(new Dimension(100, 30));
        btnSaveNewTransferAmount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                newTransferAmountDialog.setVisible(false);
                newTransferAmountDialog.dispose();

            }

        });

        // Layouting
        this.createLayout();

        newTransferAmountDialog.setVisible(true);
    }

    private void createLayout() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        newTransferAmountDialog.setLayout(gbl);

        // place Headline
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        newTransferAmountDialog.add(lblHeadline, gbc);

        // place Label of PersonTextfield
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // wieder zurücksezten da insets summiert werden und weiter unten eine
                                             // weitere Verwendung statt findet
        // gbc.fill = GridBagConstraints.NONE;
        newTransferAmountDialog.add(lblPersonOfTransferAmount, gbc);

        // place Textfield for Person
        gbc.gridx = 2;
        gbc.gridy = 1;
        newTransferAmountDialog.add(txtPersonOfTransferAmount, gbc);

        // place Label of AmountTextfield
        gbc.gridx = 1;
        gbc.gridy = 2;
        newTransferAmountDialog.add(lblTransferAmountValue, gbc);

        // place Textfield for AmountValue
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        newTransferAmountDialog.add(txtTransferAmountValue, gbc);

        // Place Save Button
        gbc.gridx = 2;
        gbc.gridy = 3;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 0, 0);
        newTransferAmountDialog.add(btnSaveNewTransferAmount, gbc);
    }

    public String getPersonOfTransferAmount() {
        return txtPersonOfTransferAmount.getText();
    }

}
