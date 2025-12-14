package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ExpenditureSuccessor extends JFrame{
    private Connection cn = null;
    private JFrame dialogOwner;
    private JDialog dialog;
    private JLabel expenditureDescriptionLabel, expenditureAmountLabel, expenditureDivideTypeLabel, expenditureValidFromLabel;
    private JFormattedTextField expenditureAmount, expenditureValidFrom;
    private JTextField expenditureDescription, expenditureDivideType;
    private Font fontTxtFields;


    public ExpenditureSuccessor(JFrame dialogOwner, Connection LoginCN) {
        cn = LoginCN;
        this.dialogOwner = dialogOwner;

        createComponents();

		// createListeners();

		// registerExistingListeners();

        createLayout();

        dialog.setVisible(true);

    }

    private void createComponents() {
        dialog = new JDialog(dialogOwner, "Nachfolger erstellen", true);
		dialog.setSize(300, 250);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        fontTxtFields = new Font("Arial", Font.PLAIN, 14);

        expenditureDescriptionLabel = new JLabel("Bezeichnung");

        expenditureAmountLabel = new JLabel("Betrag");

        expenditureDivideTypeLabel = new JLabel("Aufteilungsart");

        expenditureValidFromLabel = new JLabel("gilt ab");

        expenditureDescription = new JTextField();
        expenditureDescription.setPreferredSize(new Dimension(70, 25));

        expenditureAmount = new JFormattedTextField();
        expenditureAmount.setPreferredSize(new Dimension(70, 25));

        expenditureDivideType = new JTextField();
        expenditureDivideType.setPreferredSize(new Dimension(70, 25));

        expenditureValidFrom = new JFormattedTextField();
        expenditureValidFrom.setPreferredSize(new Dimension(70, 25));
    }

    private void registerExistingListeners() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerExistingListeners'");
    }

    private void createListeners() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createListeners'");
    }

    private void createLayout() {
        // ---
		GridBagLayout expenditureLayout = new GridBagLayout();
		GridBagConstraints expenditureLayoutConstraints = new GridBagConstraints();
		dialog.setLayout(expenditureLayout);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 0;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureDescriptionLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureAmountLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureDivideTypeLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureValidFromLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 0;
        expenditureLayoutConstraints.insets = new Insets(0, 20, 5, 0);
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureDescription, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureAmount, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureDivideType, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		dialog.add(expenditureValidFrom, expenditureLayoutConstraints);

        // reset insets
		expenditureLayoutConstraints.insets = new Insets(0, 0, 0, 0);
    }
}
