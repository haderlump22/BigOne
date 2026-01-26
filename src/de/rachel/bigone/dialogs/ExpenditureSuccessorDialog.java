package de.rachel.bigone.dialogs;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.editors.DecimalTableCellEditor;
import de.rachel.bigone.models.ExpenditureSuccessorDistributionTableModel;
import de.rachel.bigone.records.ExpenditureSuccessorDistributionTableRow;
import de.rachel.bigone.renderer.ExpenditureSuccessorDistributionTableCellRenderer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExpenditureSuccessorDialog {
    private Connection cn = null;
    private JFrame dialogOwner;
    private JDialog expenditureSuccessorDialog;
    private JLabel expenditureDescriptionLabel, expenditureAmountLabel, expenditureDivideTypeLabel, expenditureValidFromLabel;
    private JFormattedTextField expenditureAmount, expenditureValidFrom;
    private JTextField expenditureDescription, expenditureDivideType;
    private JButton saveExpenditureSuccessorButton;
    private Font fontTxtFields;
    private JPanel successorDivideTablePanel;
    private JScrollPane successorDivideTableScrollPane;
    private JTable successorDivideTable, expenditureDetailTable;


    public ExpenditureSuccessorDialog(JFrame dialogOwner, Connection LoginCN, JTable expenditureDetailTable) {
        cn = LoginCN;
        this.dialogOwner = dialogOwner;
        this.expenditureDetailTable = expenditureDetailTable;

        createComponents();

		createListeners();

		// registerExistingListeners();

        createLayout();

        expenditureSuccessorDialog.setVisible(true);

    }

    private void createComponents() {
        expenditureSuccessorDialog = new JDialog(dialogOwner, "Nachfolger erstellen", true);
		expenditureSuccessorDialog.setSize(350, 550);
		expenditureSuccessorDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        fontTxtFields = new Font("Arial", Font.PLAIN, 12);

        expenditureDescriptionLabel = new JLabel("Bezeichnung");

        expenditureAmountLabel = new JLabel("Betrag");

        expenditureDivideTypeLabel = new JLabel("Aufteilungsart");

        expenditureValidFromLabel = new JLabel("gilt ab");

        expenditureDescription = new JTextField();
        expenditureDescription.setFont(fontTxtFields);
        expenditureDescription.setPreferredSize(new Dimension(90, 25));
        expenditureDescription.setText((String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 0));

        expenditureAmount = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        expenditureAmount.setFont(fontTxtFields);
        expenditureAmount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        expenditureAmount.setPreferredSize(new Dimension(90, 25));
        expenditureAmount.setText("%.02f".formatted((Double)(expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 1))));


        expenditureDivideType = new JTextField();
        expenditureDivideType.setPreferredSize(new Dimension(90, 25));
        expenditureDivideType.setHorizontalAlignment(JFormattedTextField.CENTER);
        expenditureDivideType.setText((String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 2));

        try {
            expenditureValidFrom = new JFormattedTextField(new MaskFormatter("01.##.20##"));
            expenditureValidFrom.setFont(fontTxtFields);
            expenditureValidFrom.setPreferredSize(new Dimension(90, 25));
            expenditureValidFrom.setHorizontalAlignment(JTextField.RIGHT);
            // we set the new valid from Date to the first of the next month
            LocalDate actualDayInNextMonth = LocalDate.now().plusMonths(1);
            LocalDate firstOfNextMonth = actualDayInNextMonth.minusDays(actualDayInNextMonth.getDayOfMonth() - 1);
            expenditureValidFrom.setText(firstOfNextMonth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        successorDivideTable = new JTable(new ExpenditureSuccessorDistributionTableModel(cn, expenditureDetailTable));
        successorDivideTable.setDefaultRenderer(Object.class, new ExpenditureSuccessorDistributionTableCellRenderer());
        successorDivideTable.getColumnModel().getColumn(1).setCellEditor(new DecimalTableCellEditor());

        successorDivideTableScrollPane = new JScrollPane(successorDivideTable);
        successorDivideTableScrollPane.setPreferredSize(new Dimension(300, 90));

        successorDivideTablePanel = new JPanel();
		successorDivideTablePanel.setBorder(new TitledBorder("Nachfolger Aufteilung"));

        saveExpenditureSuccessorButton = new JButton("Speichern");
        saveExpenditureSuccessorButton.setPreferredSize(new Dimension(100, 30));
    }

    private void registerExistingListeners() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerExistingListeners'");
    }

    private void createListeners() {
        saveExpenditureSuccessorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (areValuesCorrect()) {
                    expenditureSuccessorDialog.setVisible(false);
                    expenditureSuccessorDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "alle Werte müssen korrekt gefüllt sein", "Achtung", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void createLayout() {
        // ---
        GridBagLayout successorDividePanelLayout = new GridBagLayout();
        GridBagConstraints successorDividePanelLayoutConstraints = new GridBagConstraints();
        successorDivideTablePanel.setLayout(successorDividePanelLayout);

		successorDividePanelLayoutConstraints.gridx = 0;
		successorDividePanelLayoutConstraints.gridy = 0;
		successorDividePanelLayoutConstraints.fill = GridBagConstraints.BOTH;
		successorDivideTablePanel.add(successorDivideTableScrollPane, successorDividePanelLayoutConstraints);
		// ---

		// ---
		GridBagLayout expenditureLayout = new GridBagLayout();
		GridBagConstraints expenditureLayoutConstraints = new GridBagConstraints();
		expenditureSuccessorDialog.setLayout(expenditureLayout);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 0;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureDescriptionLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureAmountLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureDivideTypeLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureValidFromLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 0;
        expenditureLayoutConstraints.insets = new Insets(0, 20, 5, 0);
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureDescription, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureAmount, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureDivideType, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureValidFrom, expenditureLayoutConstraints);

        expenditureLayoutConstraints.insets = new Insets(0, 0, 0, 0);

        expenditureLayoutConstraints.gridx = 0;
        expenditureLayoutConstraints.gridy = 4;
        expenditureLayoutConstraints.gridwidth = GridBagConstraints.REMAINDER;
        expenditureSuccessorDialog.add(successorDivideTablePanel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
        expenditureLayoutConstraints.gridy = 5;
        // expenditureLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        expenditureLayoutConstraints.anchor = GridBagConstraints.EAST;
        expenditureLayoutConstraints.insets = new Insets(10, 0, 0, 0);
        expenditureSuccessorDialog.add(saveExpenditureSuccessorButton, expenditureLayoutConstraints);

    }

    public boolean areValuesCorrect() {
        boolean allIsCorrect = false;

        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", expenditureValidFrom.getText())) {
            allIsCorrect = true;
        } else {
            return false;
        }

        if (Double.valueOf(expenditureAmount.getText().replace(".", "").replace(',', '.')) > 0) {
            allIsCorrect = true;
        } else {
            return false;
        }

        return allIsCorrect;
    }

    public String getNewDescription() {
        return expenditureDescription.getText();
    }

    public String getNewAmount() {
        return expenditureAmount.getText();
    }

    public String getNewDivideType() {
        return expenditureDivideType.getText();
    }

    public LocalDate getNewValidFrom() {
        return LocalDate.parse(expenditureValidFrom.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public ArrayList<ExpenditureSuccessorDistributionTableRow> getNewSuccessorDivideTableData() {
        return ((ExpenditureSuccessorDistributionTableModel)(successorDivideTable.getModel())).getTableData();
    }
}
