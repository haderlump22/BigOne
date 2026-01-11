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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.BigOneTools;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.TransferAmountDetailTableModel;

public class TransferAmountSuccessorDialog {
    private JLabel lblPersonOfTransferAmount, lblTransferAmountValue, lblHeadline, lblValidFrom;
    private JComboBox<String> cmbPersonOfTransferAmount;
    private DefaultComboBoxModel<String> cmbPersonOfTransferAmountModel;
    private JFormattedTextField transferAmountValue, validFrom;
    private JButton btnSaveNewTransferAmount;
    private JDialog newTransferAmountDialog;
    private Connection cn = null;

    public TransferAmountSuccessorDialog(TransferAmountDetailTableModel transfertAmountDetailTableModel, int selectedRow, JFrame dialogOwner, Connection loginCN) {
        cn = loginCN;

        newTransferAmountDialog = new JDialog(dialogOwner, "Ausgabennachfolger", true);
        newTransferAmountDialog.setSize(390, 280);
        newTransferAmountDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        newTransferAmountDialog.setResizable(true);

        lblHeadline = new JLabel("Infos für neuen Überweisungsbetrag");

        lblPersonOfTransferAmount = new JLabel("Person für Ausgabe");
        lblPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));

        lblTransferAmountValue = new JLabel("Ausgaben Betrag");
        lblTransferAmountValue.setPreferredSize(new Dimension(150, 25));

        lblValidFrom = new JLabel("gültig ab");
        lblValidFrom.setPreferredSize(new Dimension(150, 25));

        cmbPersonOfTransferAmountModel = new DefaultComboBoxModel<String>();
        this.fillcmbPersonOfTransferAmount(transfertAmountDetailTableModel, selectedRow);
        cmbPersonOfTransferAmount = new JComboBox<String>(cmbPersonOfTransferAmountModel);
        cmbPersonOfTransferAmount.setFont(new Font("Arial", Font.PLAIN,12));
        cmbPersonOfTransferAmount.setPreferredSize(new Dimension(150, 25));



        transferAmountValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        transferAmountValue.setFont(new Font("Arial", Font.PLAIN,12));
        transferAmountValue.setText("0,00");
        transferAmountValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
        transferAmountValue.setPreferredSize(new Dimension(150, 25));
        transferAmountValue.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent fe) {
            }

            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        transferAmountValue.selectAll();
                    }
                });
            }
        });

        try {
            validFrom = new JFormattedTextField(new MaskFormatter("01-##-20##"));
            validFrom.setFont(new Font("Arial", Font.PLAIN,12));
            validFrom.setPreferredSize(new Dimension(150, 25));
            validFrom.setHorizontalAlignment(JTextField.RIGHT);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        btnSaveNewTransferAmount = new JButton("Speichern");
        btnSaveNewTransferAmount.setPreferredSize(new Dimension(100, 30));
        btnSaveNewTransferAmount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (areValuesCorrect()) {
                    newTransferAmountDialog.setVisible(false);
                    newTransferAmountDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "alle Werte müssen korrekt gefüllt sein", "Achtung", JOptionPane.INFORMATION_MESSAGE);
                }
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

        gbc.gridx = 1;
        gbc.gridy = 3;
        newTransferAmountDialog.add(lblValidFrom, gbc);


        // place Textfield for AmountValue
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        newTransferAmountDialog.add(transferAmountValue, gbc);

        // Place Save Button
        gbc.gridx = 2;
        gbc.gridy = 3;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        newTransferAmountDialog.add(validFrom, gbc);

        // Place Save Button
        gbc.gridx = 2;
        gbc.gridy = 4;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 0, 0);
        newTransferAmountDialog.add(btnSaveNewTransferAmount, gbc);
    }

    public String getPersonOfTransferAmount() {
        return cmbPersonOfTransferAmount.getSelectedItem().toString();
    }

    private void fillcmbPersonOfTransferAmount(TransferAmountDetailTableModel transfertAmountDetailTableModel, int selectedRow) {
        int personIdFromSelectedRow;
        String comboboxElementToAdd;

        //put all persons to the cmbmodel cmbModelPerson
        DBTools getter = new DBTools(cn);

        // if given the selectedRow, then get the Person that stands behind them
        personIdFromSelectedRow = this.getPersonIdFromSelectedRow(transfertAmountDetailTableModel, selectedRow);

        //set First Value (select invitation) to cmbPerson
        cmbPersonOfTransferAmountModel.addElement("---bitte wählen---");

        getter.select("""
                SELECT name, vorname, personen_id
                FROM personen
                WHERE gueltig = TRUE
                """);

        try {
            getter.beforeFirst();

            while (getter.next()) {
                comboboxElementToAdd = getter.getString("name") + " " + getter.getString("vorname") + " (" + getter.getString("personen_id") + ")";
                cmbPersonOfTransferAmountModel.addElement(comboboxElementToAdd);

                // decide if this Element has to be preselected
                if (personIdFromSelectedRow == getter.getInt("personen_id")) {
                    cmbPersonOfTransferAmountModel.setSelectedItem(comboboxElementToAdd);
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
        }
    }

    public void createSuccessor(TransferAmountDetailTableModel transfertAmountDetailTableModel, int oldTransferAmountId) {
        DBTools sqlTool = new DBTools(cn);
        LocalDate validFromDate = LocalDate.parse(validFrom.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // first set the Until Value of the old Record to the new valid from Date minus
        // one Month
        if (!sqlTool.update("""
                UPDATE ha_ueberweisungsbetraege
                SET gilt_bis = '%s'
                WHERE ueberweisungsbetrag_id = %d
                """.formatted(validFromDate.minusMonths(1).toString(), oldTransferAmountId))) {
            System.err.println("Fehler bei der Akutalisierung des Überweisungsbetragsdatensatztes mit der ID: "
                    + oldTransferAmountId);
            System.exit(1);
        }

        // next we insert a New Record
        if (!sqlTool.insert("""
                INSERT INTO ha_ueberweisungsbetraege
                (partei_id, betrag, gilt_ab)
                VALUES
                (%d, %s, '%s')
                """.formatted(BigOneTools.extractEreigId(cmbPersonOfTransferAmount.getSelectedItem().toString()),
                Double.valueOf(transferAmountValue.getText().replace(".", "").replace(',', '.')),
                validFromDate.toString()))) {
            System.err.println("Fehler beim Einfügen des neuen Überweisungsbetragsdatensatztes für: "
                    + cmbPersonOfTransferAmount.getSelectedItem().toString());
            System.exit(1);
        }

        // an at last update the Table
        transfertAmountDetailTableModel.aktualisiere();
    }

    private int getPersonIdFromSelectedRow(TransferAmountDetailTableModel transfertAmountDetailTableModel, int selectedRow) {
        // put all persons to the cmbmodel cmbModelPerson
        DBTools getter = new DBTools(cn);

        getter.select("""
                SELECT partei_id
                FROM ha_ueberweisungsbetraege
                WHERE ueberweisungsbetrag_id = %d
                """.formatted(transfertAmountDetailTableModel.getValueAt(selectedRow, -1)));

        try {
            getter.first();

            return getter.getInt("partei_id");
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
            return -1;
        }
    }

    public boolean areValuesCorrect() {
        boolean allIsCorrect = false;

        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", validFrom.getText())) {
            allIsCorrect = true;
        } else {
            return false;
        }

        if (Double.valueOf(transferAmountValue.getText().replace(".", "").replace(',', '.')) > 0) {
            allIsCorrect = true;
        } else {
            return false;
        }

        return allIsCorrect;
    }
}
