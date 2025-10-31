package de.rachel.bigone.dialogs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.TransferAmountDetailTableModel;

public class TransferAmountDetailTableCreateSuccessorDialog extends JFrame {
    private JLabel lblPersonOfTransferAmount, lblTransferAmountValue, lblHeadline;
    private JComboBox<String> cmbPersonOfTransferAmount;
    private DefaultComboBoxModel<String> cmbPersonOfTransferAmountModel;
    private JFormattedTextField txtTransferAmountValue;
    private JButton btnSaveNewTransferAmount;
    private final JDialog newTransferAmountDialog;
    private Connection cn = null;

    public TransferAmountDetailTableCreateSuccessorDialog(TransferAmountDetailTableModel modelOfSourceTable, int selectedRow, JFrame dialogOwner, Connection LoginCN) {
        cn = LoginCN;

        newTransferAmountDialog = new JDialog(dialogOwner, "Ausgabennachfolger", true);
        newTransferAmountDialog.setSize(320, 180);
        newTransferAmountDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        newTransferAmountDialog.setResizable(false);

        lblHeadline = new JLabel("Infos für neuen Überweisungsbetrag");

        lblPersonOfTransferAmount = new JLabel("Person für Ausgabe");
        lblPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));

        cmbPersonOfTransferAmountModel = new DefaultComboBoxModel<String>();
        this.fillcmbPersonOfTransferAmount(modelOfSourceTable, selectedRow);
        cmbPersonOfTransferAmount = new JComboBox<String>(cmbPersonOfTransferAmountModel);
        cmbPersonOfTransferAmount.setFont(new Font("Arial", Font.PLAIN,16));
        cmbPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));

        lblTransferAmountValue = new JLabel("Ausgaben Betrag");
        lblTransferAmountValue.setPreferredSize(new Dimension(150, 25));

        txtTransferAmountValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        txtTransferAmountValue.setFont(new Font("Arial", Font.PLAIN,16));
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
        newTransferAmountDialog.add(cmbPersonOfTransferAmount, gbc);

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
        return cmbPersonOfTransferAmount.getSelectedItem().toString();
    }

    private void fillcmbPersonOfTransferAmount(TransferAmountDetailTableModel modelOfSourceTable, int selectedRow) {
        int PersonIdFromSelectedRow;
        String ComboboxElementToAdd;

		//put all persons to the cmbmodel cmbModelPerson
		DBTools getter = new DBTools(cn);
        ResultSet rs;

        // if given the selectedRow, then get the Person that stands behind them
        PersonIdFromSelectedRow = this.getPersonIdFromSelectedRow(modelOfSourceTable, selectedRow);

		//set First Value (select invitation) to cmbPerson
		cmbPersonOfTransferAmountModel.addElement("---bitte wählen---");

		getter.select("""
                SELECT name, vorname, personen_id
                FROM personen
				WHERE gueltig = TRUE
                """,3);

        rs = getter.getResultSet();

        try {
            rs.beforeFirst();

            while (rs.next()) {
                ComboboxElementToAdd = rs.getString("name") + " " + rs.getString("vorname") + "(" + rs.getString("personen_id") + ")";
                cmbPersonOfTransferAmountModel.addElement(ComboboxElementToAdd);

                // decide if this Element has to be preselected
                if (PersonIdFromSelectedRow == rs.getInt("personen_id")) {
                    cmbPersonOfTransferAmountModel.setSelectedItem(ComboboxElementToAdd);
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
	}

    public void createSuccessor(TransferAmountDetailTableModel modelOfSourceTable, int selectedRow) {

    }

    private int getPersonIdFromSelectedRow(TransferAmountDetailTableModel modelOfSourceTable, int selectedRow) {
        // put all persons to the cmbmodel cmbModelPerson
        DBTools getter = new DBTools(cn);
        ResultSet rs;

        getter.select("""
                SELECT partei_id
                FROM ha_ueberweisungsbetraege
                WHERE ueberweisungsbetrag_id = %d
                """.formatted(modelOfSourceTable.getValueAt(selectedRow, -1)), 1);

        rs = getter.getResultSet();

        try {
            rs.first();

            return rs.getInt("partei_id");
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
            return -1;
        }
    }
}
