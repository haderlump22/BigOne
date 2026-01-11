
package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.editors.DateTableCellEditor;
import de.rachel.bigone.editors.DecimalTableCellEditor;
import de.rachel.bigone.models.ValuesTableModel;
import de.rachel.bigone.renderer.ValuesTableCellRenderer;

/**
 * @author Normen Rachel
 */

public class Values {
    private Connection cn = null;
    private JFrame valuewindow;
    private JFormattedTextField txtValue, txtLiquiDate;
    private Font fontTxtFields, fontCmbBoxes;
    private JTable table;
    private ValuesTableModel model;
    private JComboBox<String> cmbKto;
    private JCheckBox withLiquiDate;

    Values(Connection LoginCN) {
        cn = LoginCN;
        valuewindow = new JFrame("Beträge finden");
        valuewindow.setSize(785, 480);
        valuewindow.setLocation(200, 200);
        valuewindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        valuewindow.setLayout(null);
        valuewindow.setResizable(false);

        // schriftenfestlegungen
        fontTxtFields = new Font("Arial", Font.PLAIN, 16);
        fontCmbBoxes = new Font("Arial", Font.PLAIN, 14);

        // textfeld fuer den zu suchenden Betrag definieren
        txtValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        txtValue.setBounds(30, 25, 100, 25);
        txtValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtValue.setFont(fontTxtFields);
        txtValue.setText("0,00");
        txtValue.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent fe) {

            }

            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (table.isEditing()) {
                            table.getCellEditor().stopCellEditing();
                            table.clearSelection();
                        }

                        txtValue.selectAll();
                    }
                });
            }
        });

        txtValue.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    reloadData();
                }
            }

            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtValue.requestFocus();
                }
            }

            public void keyTyped(KeyEvent arg0) {

            }
        });

        String now = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        // String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        try {
            txtLiquiDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        txtLiquiDate.setBounds(140, 25, 110, 25);
        txtLiquiDate.setHorizontalAlignment(JTextField.RIGHT);
        txtLiquiDate.setFont(fontTxtFields);
        // aktuellen Monat als Liquimonat setzten
        txtLiquiDate.setText("01-" + now.substring(3, 5) + "-20" + now.substring(8));

        // add Account choose and fill it with valid Account IDs (IBAN)
        cmbKto = new JComboBox<String>();
        cmbKto.setBounds(260, 25, 250, 25);
        cmbKto.setFont(fontCmbBoxes);

        // and fill it with Values
        fill_cmbKto();

        // setting the Tooltip for the shown Value after first filling
        cmbKto.setToolTipText(getAccountDescription(cmbKto.getSelectedItem().toString().replace(" ", "")));

        // add an ActionListener to set the Tooltip with the Accountdescription of the selected IBAN
        cmbKto.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // set Tooltip of the Account Combobox with the Hint of the Account
                    cmbKto.setToolTipText(getAccountDescription(e.getItem().toString().replace(" ", "")));
                    reloadData();
                }
            }
        });

        // Create the check box to specify whether liquidatum is in Query or not
        withLiquiDate = new JCheckBox("mit Liquidatum suchen", true);
        withLiquiDate.setBounds(520, 25, 180, 25);

        // ad an Valuechange Listener for aktivate/deaktivate the LiquiDate Textfield
        withLiquiDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadData();
            }
        });

        // put all Elements on the Window
        valuewindow.add(txtValue);
        valuewindow.add(txtLiquiDate);
        valuewindow.add(cmbKto);
        valuewindow.add(withLiquiDate);
        zeichne_tabelle();

        valuewindow.setVisible(true);

    }

    private void zeichne_tabelle() {

        table = new JTable(new ValuesTableModel(cn));
        ValuesTableCellRenderer ren = new ValuesTableCellRenderer();
        table.setDefaultRenderer(Object.class, ren);
        table.getColumnModel().getColumn(2).setCellEditor(new DateTableCellEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new DecimalTableCellEditor());
        table.getColumnModel().getColumn(5).setCellEditor(new DateTableCellEditor());
        // table.setDefaultEditor(Object.class, new ValuesTableCellEditor());

        // fuer einige spalten feste breiten einrichten
        table.getColumnModel().getColumn(0).setMinWidth(55);
        table.getColumnModel().getColumn(0).setMaxWidth(55);
        table.getColumnModel().getColumn(1).setMinWidth(25);
        table.getColumnModel().getColumn(1).setMaxWidth(25);
        table.getColumnModel().getColumn(2).setMinWidth(78);
        table.getColumnModel().getColumn(2).setMaxWidth(78);
        table.getColumnModel().getColumn(3).setMinWidth(65);
        table.getColumnModel().getColumn(3).setMaxWidth(65);
        table.getColumnModel().getColumn(5).setMinWidth(78);
        table.getColumnModel().getColumn(5).setMaxWidth(78);
        table.getColumnModel().getColumn(6).setMinWidth(120);
        table.getColumnModel().getColumn(6).setMaxWidth(120);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 70, 725, 355);

        valuewindow.add(sp);
        valuewindow.validate();
        valuewindow.repaint();
    }

    private void fill_cmbKto() {
        DBTools getter = new DBTools(cn);

        getter.select("""
                SELECT konten.iban
                FROM konten
                WHERE konten.gueltig = TRUE
                """);

        try {
            getter.beforeFirst();

            while (getter.next()) {
                cmbKto.addItem(BigOneTools.getIbanFormatted(getter.getString("iban")));
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
        }
    }

    private String getAccountDescription(String IBAN) {
        // get the transaction wording to the transaktions_id
        DBTools getter = new DBTools(cn);
        String returnValue = "not found";

        getter.select("""
                SELECT bemerkung
                FROM konten
                WHERE iban = '%s'
                """.formatted(IBAN));

        try {
            getter.beforeFirst();

            while (getter.next()) {
                returnValue = getter.getString("bemerkung");
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
            System.err.println("etwas hat beim summieren der SollSumme nicht geklappt!");
            System.exit(1);
        }

        return returnValue;
    }

    private void reloadData() {
        String liquiDate;

        if (withLiquiDate.isSelected()) {
            liquiDate = BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0);
            txtLiquiDate.setEnabled(true);
            withLiquiDate.setText("mit Liquidatum suchen");
        } else {
            liquiDate = "";
            txtLiquiDate.setEnabled(false);
            withLiquiDate.setText("ohne Liquidatum suchen");
        }

        // and restart the Search if an Amount is given
        if (Double.valueOf(txtValue.getText().replace(".", "").replace(',', '.')) > 0) {
            model = (ValuesTableModel) table.getModel();
            model.aktualisiere(txtValue.getText().replace(".", "").replace(',', '.'), liquiDate,
                    cmbKto.getSelectedItem().toString().replace(" ", ""), cmbKto.getToolTipText());
        }
    }
}
