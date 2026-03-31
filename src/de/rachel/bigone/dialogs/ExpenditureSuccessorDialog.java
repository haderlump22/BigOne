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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ExpenditureSuccessorDialog {
    private Connection cn = null;
    private JFrame dialogOwner;
    private JDialog expenditureSuccessorDialog;
    private JLabel expenditureSuccessorDescriptionLabel, expenditureSuccessorAmountLabel,
            expenditureSuccessorDivideTypeLabel, expenditureSuccessorValidFromLabel, expenditureSuccessorCommentLabel,
            expenditureSuccessorFrequencyLabel;
    private JFormattedTextField expenditureSuccessorAmount, expenditureSuccessorValidFrom;
    private JTextField expenditureSuccessorDescription, expenditureSuccessorDivideType, expenditureSuccessorFrequency;
    private JTextArea expenditureSuccessorCommentArea;
    private JButton saveExpenditureSuccessorButton;
    private Font fontTxtFields;
    private JPanel successorDivideTablePanel;
    private JScrollPane successorDivideTableScrollPane, expenditureSuccessorCommentAreaScrollPane;
    private JTable successorDivideTable, expenditureDetailTable;
    private Integer successorToId, frequency;
    private String valueCheckComment = "";


    public ExpenditureSuccessorDialog(JFrame dialogOwner, Connection LoginCN, JTable expenditureDetailTable) {
        cn = LoginCN;
        this.dialogOwner = dialogOwner;
        this.expenditureDetailTable = expenditureDetailTable;
        this.successorToId = (Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -1);
        this.frequency = (Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -3);

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

        fontTxtFields = new Font("Arial", Font.PLAIN, 14);

        expenditureSuccessorDescriptionLabel = new JLabel("Bezeichnung");

        expenditureSuccessorAmountLabel = new JLabel("Betrag");

        expenditureSuccessorDivideTypeLabel = new JLabel("Aufteilungsart");

        expenditureSuccessorValidFromLabel = new JLabel("gilt ab");

        expenditureSuccessorCommentLabel = new JLabel("Bemerkung");

        expenditureSuccessorFrequencyLabel = new JLabel("Häufigkeit");

        // fix Me
        // add an Commentsection, has forgot this

        expenditureSuccessorDescription = new JTextField();
        expenditureSuccessorDescription.setFont(fontTxtFields);
        expenditureSuccessorDescription.setPreferredSize(new Dimension(90, 25));
        expenditureSuccessorDescription.setText((String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 0));

        expenditureSuccessorAmount = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
        expenditureSuccessorAmount.setFont(fontTxtFields);
        expenditureSuccessorAmount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        expenditureSuccessorAmount.setPreferredSize(new Dimension(90, 25));
        expenditureSuccessorAmount.setText("%.02f".formatted((Double)(expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 1))));


        expenditureSuccessorDivideType = new JTextField();
        expenditureSuccessorDivideType.setPreferredSize(new Dimension(90, 25));
        expenditureSuccessorDivideType.setHorizontalAlignment(JFormattedTextField.CENTER);
        expenditureSuccessorDivideType.setText((String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 2));

        try {
            expenditureSuccessorValidFrom = new JFormattedTextField(new MaskFormatter("01.##.20##"));
            expenditureSuccessorValidFrom.setFont(fontTxtFields);
            expenditureSuccessorValidFrom.setPreferredSize(new Dimension(90, 25));
            expenditureSuccessorValidFrom.setHorizontalAlignment(JTextField.RIGHT);

            // the new valid from Date is ever the first of the next month
            LocalDate actualDayInNextMonth = LocalDate.now().plusMonths(1);
            LocalDate firstOfNextMonth = actualDayInNextMonth.minusDays(actualDayInNextMonth.getDayOfMonth() - 1);
            expenditureSuccessorValidFrom.setText(firstOfNextMonth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        expenditureSuccessorCommentArea = new JTextArea((String)(expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -2)));
        expenditureSuccessorCommentArea.setLineWrap(true);
        expenditureSuccessorCommentArea.setWrapStyleWord(true);
        expenditureSuccessorCommentAreaScrollPane = new JScrollPane(expenditureSuccessorCommentArea);
        expenditureSuccessorCommentAreaScrollPane.setPreferredSize(new Dimension(200, 70));

        expenditureSuccessorFrequency = new JTextField();
        expenditureSuccessorFrequency.setFont(fontTxtFields);
        expenditureSuccessorFrequency.setPreferredSize(new Dimension(90, 25));
        expenditureSuccessorFrequency.setText(((Integer) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), -3)).toString());

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
        throw new UnsupportedOperationException("Unimplemented method 'registerExistingListeners'");
    }

    private void createListeners() {
        saveExpenditureSuccessorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (successorDivideTable.isEditing()) {
                    successorDivideTable.getCellEditor().stopCellEditing();
                    successorDivideTable.clearSelection();
                }

                if (areValuesCorrect()) {
                    expenditureSuccessorDialog.setVisible(false);
                    expenditureSuccessorDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "alle Werte müssen korrekt gefüllt sein\n(" + valueCheckComment + ")", "Achtung",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        expenditureSuccessorAmount.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent fe) {
                // fixme
                // maybe split the new amount automaticly
                // over the rows of the divideTable
            }

            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        expenditureSuccessorAmount.selectAll();
                    }
                });
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
		expenditureSuccessorDialog.add(expenditureSuccessorDescriptionLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorAmountLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorDivideTypeLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorValidFromLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 4;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorCommentLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 0;
		expenditureLayoutConstraints.gridy = 5;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorFrequencyLabel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 0;
        expenditureLayoutConstraints.insets = new Insets(0, 20, 5, 0);
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorDescription, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 1;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorAmount, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 2;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorDivideType, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 3;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorValidFrom, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 4;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorCommentAreaScrollPane, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
		expenditureLayoutConstraints.gridy = 5;
		expenditureLayoutConstraints.anchor = GridBagConstraints.WEST;
		expenditureSuccessorDialog.add(expenditureSuccessorFrequency, expenditureLayoutConstraints);

        expenditureLayoutConstraints.insets = new Insets(0, 0, 0, 0);

        expenditureLayoutConstraints.gridx = 0;
        expenditureLayoutConstraints.gridy = 6;
        expenditureLayoutConstraints.gridwidth = GridBagConstraints.REMAINDER;
        expenditureSuccessorDialog.add(successorDivideTablePanel, expenditureLayoutConstraints);

        expenditureLayoutConstraints.gridx = 1;
        expenditureLayoutConstraints.gridy = 7;
        // expenditureLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        expenditureLayoutConstraints.anchor = GridBagConstraints.EAST;
        expenditureLayoutConstraints.insets = new Insets(10, 0, 0, 0);
        expenditureSuccessorDialog.add(saveExpenditureSuccessorButton, expenditureLayoutConstraints);

    }

    public boolean areValuesCorrect() {
        boolean allIsCorrect = false;

        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", expenditureSuccessorValidFrom.getText())) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "Datumswert nicht korrekt";
            return false;
        }

        if (Double.valueOf(expenditureSuccessorAmount.getText().replace(".", "").replace(',', '.')) > 0) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "neuer Betrag nicht größer 0";
            return false;
        }

        // The sum of the individual values ​​in the distribution table must correspond
        // to the total value of the successor amount.
        Integer compareResult = Double.compare(
                ((ExpenditureSuccessorDistributionTableModel) successorDivideTable.getModel()).getSumOfAmounts(),
                Double.valueOf(expenditureSuccessorAmount.getText().replace(".", "").replace(',', '.')));

        if (compareResult == 0) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "Nachfolgebetrag nicht korrekt aufgeteilt";
            return false;
        }

        // Some database fields only hold a certain number of characters.
        if (expenditureSuccessorDescription.getText().length() <= 100) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "Ausgabenbezeichung enthält mehr als 100 Zeichen";
            return false;
        }

        if (expenditureSuccessorDivideType.getText().length() <= 20) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "Aufteilungsart enthält mehr als 20 Zeichen";
            return false;
        }

        if (expenditureSuccessorCommentArea.getText().length() <= 250) {
            allIsCorrect = true;
        } else {
            valueCheckComment = "Hinweis enthält mehr als 250 Zeichen";
            return false;
        }

        return allIsCorrect;
    }

    public String getNewDescription() {
        return expenditureSuccessorDescription.getText();
    }

    public Double getNewAmount() {
        return Double.valueOf(expenditureSuccessorAmount.getText().replace(".", "").replace(',', '.'));
    }

    public String getNewDivideType() {
        return expenditureSuccessorDivideType.getText();
    }

    public LocalDate getNewValidFrom() {
        return LocalDate.parse(expenditureSuccessorValidFrom.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public ArrayList<ExpenditureSuccessorDistributionTableRow> getNewSuccessorDivideTableData() {
        return ((ExpenditureSuccessorDistributionTableModel)(successorDivideTable.getModel())).getTableData();
    }

    public Integer getSuccessorToId() {
        return successorToId;
    }

    public String getNewComment() {
        return expenditureSuccessorCommentArea.getText();
    }

    public Integer getFrequency() {
        return frequency;
    }
}
