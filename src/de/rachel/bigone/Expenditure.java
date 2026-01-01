package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import de.rachel.bigone.listeners.ExpenditureDetailTableMouseListener;
import de.rachel.bigone.listeners.ExpenditureDetailTableSelectionListener;
import de.rachel.bigone.models.ExpenditureDetailTableModel;
import de.rachel.bigone.models.ExpenditureDistributionTableModel;
import de.rachel.bigone.models.ExpenditureSumPerPartyTableModel;
import de.rachel.bigone.renderer.ExpenditureDetailTableCellRenderer;
import de.rachel.bigone.renderer.ExpenditureSumPerPartyTableCellRenderer;

public class Expenditure {
    private Connection cn = null;

    private JFrame expenditureWindow;
    private JPanel expenditureDetailPanel, expenditureSumPerPartyPanel, expenditureDistributionPanel,
            expenditureHintPanel;
    private JScrollPane expenditureSumPerPartyScrollPane, expenditureDetailScrollPane,
            expenditureDistributionScrollPane, expenditureHintScrollPane;
    private JTable expenditureSumPerPartyTable, expenditureDetailTable, expenditureDistributionTable;
    private JTextArea hintArea = new JTextArea();
    private ExpenditureDetailTableSelectionListener detailTableSelectionListener;

    Expenditure(Connection LoginCN) {
        cn = LoginCN;

        expenditureWindow = new JFrame("Ausgaben");
        expenditureWindow.setSize(650, 530);
        expenditureWindow.setLocation(200, 200);
        expenditureWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        expenditureWindow.setLayout(null);
        expenditureWindow.setResizable(false);

        expenditureSumPerPartyPanel = new JPanel();
        expenditureSumPerPartyPanel.setLayout(null);
        expenditureSumPerPartyPanel.setBounds(10, 10, 230, 100);
        expenditureSumPerPartyPanel.setBorder(new TitledBorder("Ausgaben Gesamtanteil"));

        expenditureDetailPanel = new JPanel();
        expenditureDetailPanel.setLayout(null);
        expenditureDetailPanel.setBounds(10, 120, 380, 370);
        expenditureDetailPanel.setBorder(new TitledBorder("Ausgaben Details"));

        expenditureDistributionPanel = new JPanel();
        expenditureDistributionPanel.setLayout(null);
        expenditureDistributionPanel.setBounds(410, 120, 230, 100);
        expenditureDistributionPanel.setBorder(new TitledBorder("Ausgaben Aufteilung"));

        expenditureHintPanel = new JPanel();
        expenditureHintPanel.setLayout(null);
        expenditureHintPanel.setBounds(410, 230, 230, 100);
        expenditureHintPanel.setBorder(new TitledBorder("Ausgaben Hinweise"));

        // create table for expenditure sum per party
        ereateExpenditureSumPerPartyTable();

        // create table for expenditures
        createExpenditureDetailTable();

        // create table for expenditure distribution
        // should only appear later when something is selected in the detail table
        createExpenditureDistributionTable();

        expenditureSumPerPartyScrollPane = new JScrollPane(expenditureSumPerPartyTable);
        expenditureSumPerPartyScrollPane.setBounds(15, 20, 200, 70);
        expenditureSumPerPartyPanel.add(expenditureSumPerPartyScrollPane);

        expenditureDetailScrollPane = new JScrollPane(expenditureDetailTable);
        expenditureDetailScrollPane.setBounds(15, 20, 350, 340);
        expenditureDetailPanel.add(expenditureDetailScrollPane);

        expenditureDistributionScrollPane = new JScrollPane(
                new JTextArea("...bitte Ausgabe auswäheln um\n die Aufteilung hier anzuzeigen..."));
        // expenditureDistributionScrollPane = new
        // JScrollPane(expenditureDistributionTable);
        expenditureDistributionScrollPane.setBounds(15, 20, 200, 70);
        expenditureDistributionPanel.add(expenditureDistributionScrollPane);

        // some settings for the jtextarea before putting to JScrollPane
        hintArea.setLineWrap(true);
        hintArea.setWrapStyleWord(true);
        hintArea.setEditable(false);

        expenditureHintScrollPane = new JScrollPane(hintArea);
        expenditureHintScrollPane.setBounds(15, 20, 200, 70);
        expenditureHintPanel.add(expenditureHintScrollPane);

        // put all to the Frame
        expenditureWindow.add(expenditureSumPerPartyPanel);
        expenditureWindow.add(expenditureDetailPanel);
        expenditureWindow.add(expenditureDistributionPanel);
        expenditureWindow.add(expenditureHintPanel);
        expenditureWindow.validate();
        expenditureWindow.repaint();

        expenditureWindow.setVisible(true);

        // add self defined SelectionListener for the detail table
        // that listener will show the distributioninfos of the selected expenditure for
        // each party
        // in a separate table (expenditureDistributionTable)
        detailTableSelectionListener = new ExpenditureDetailTableSelectionListener(
                expenditureDetailTable, expenditureDistributionScrollPane, expenditureDistributionTable, hintArea);
        expenditureDetailTable.getSelectionModel().addListSelectionListener(detailTableSelectionListener);

        // test to get the thicknes of one of the vertical scrollbars
        // System.out.println(expenditureSumPerPartyScrollPane.getVerticalScrollBar().getWidth());
        // System.out.println(expenditureSumPerPartyTable.getColumnModel().getColumn(1).getWidth());

    }

    private void ereateExpenditureSumPerPartyTable() {
        expenditureSumPerPartyTable = new JTable(new ExpenditureSumPerPartyTableModel(cn));
        expenditureSumPerPartyTable.setDefaultRenderer(Object.class, new ExpenditureSumPerPartyTableCellRenderer());

        // define the width for some columns
        expenditureSumPerPartyTable.getColumnModel().getColumn(0).setMinWidth(120);
        expenditureSumPerPartyTable.getColumnModel().getColumn(0).setMaxWidth(120);
    }

    private void createExpenditureDetailTable() {
        expenditureDetailTable = new JTable(new ExpenditureDetailTableModel(cn));
        expenditureDetailTable.setDefaultRenderer(Object.class, new ExpenditureDetailTableCellRenderer());

        // define the width for some columns
        expenditureDetailTable.getColumnModel().getColumn(0).setMinWidth(110);
        expenditureDetailTable.getColumnModel().getColumn(0).setMaxWidth(110);

        // selbst definierten Mouselistener hinzufügen
        expenditureDetailTable.addMouseListener(new ExpenditureDetailTableMouseListener(expenditureDetailTable, this, cn));
    }

    private void createExpenditureDistributionTable() {
        expenditureDistributionTable = new JTable(new ExpenditureDistributionTableModel(cn));
        expenditureDistributionTable.setDefaultRenderer(Object.class, new ExpenditureDetailTableCellRenderer());

        // define the width for some columns
        expenditureDistributionTable.getColumnModel().getColumn(0).setMinWidth(120);
        expenditureDistributionTable.getColumnModel().getColumn(0).setMaxWidth(120);
    }

    public void refreshContent() {
        expenditureDistributionScrollPane.setViewportView(new JTextArea("...bitte Ausgabe auswäheln um\n die Aufteilung hier anzuzeigen..."));
        hintArea.setText(null);

        // set call Couter to 0 so that viewport of a Scrollpane can update
        detailTableSelectionListener.resetCallCounter();

        ((ExpenditureDetailTableModel)expenditureDetailTable.getModel()).aktualisiere();
        ((ExpenditureSumPerPartyTableModel)expenditureSumPerPartyTable.getModel()).aktualisiere();
    }

    public JFrame getExpenditureJFrame() {
        return expenditureWindow;
    }
}
