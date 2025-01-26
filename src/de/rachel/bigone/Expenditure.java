package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.rachel.bigone.Listeners.ExpenditureDetailTableSelectionListener;
import de.rachel.bigone.Listeners.RACMouseListener;
import de.rachel.bigone.Models.ExpenditureDetailTableModel;
import de.rachel.bigone.Models.ExpenditureDistributionTableModel;
import de.rachel.bigone.Models.ExpenditureSumPerPartyTableModel;
import de.rachel.bigone.Renderer.ExpenditureDetailTableCellRenderer;
import de.rachel.bigone.Renderer.ExpenditureSumPerPartyTableCellRenderer;

public class Expenditure {
    private Connection cn = null;

    private JFrame ExpenditureWindow;
    private JPanel ExpenditureDetailPanel, ExpenditureSumPerPartyPanel, ExpenditureDistributionPanel;
    private JScrollPane ExpenditureSumPerPartyScrollPane, ExpenditureDetailScrollPane, ExpenditureDistributionScrollPane;
    private JTable ExpenditureSumPerPartyTable, ExpenditureDetailTable, ExpenditureDistributionTable;

    Expenditure(Connection LoginCN) {
        cn = LoginCN;

        ExpenditureWindow = new JFrame("Ausgaben");
        ExpenditureWindow.setSize(785, 530);
        ExpenditureWindow.setLocation(200, 200);
        ExpenditureWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ExpenditureWindow.setLayout(null);
        ExpenditureWindow.setResizable(false);

        ExpenditureSumPerPartyPanel = new JPanel();
        ExpenditureSumPerPartyPanel.setLayout(null);
        ExpenditureSumPerPartyPanel.setBounds(10, 10, 230, 100);
        ExpenditureSumPerPartyPanel.setBorder(new TitledBorder("Ausgaben Gesamtanteil"));

        ExpenditureDetailPanel = new JPanel();
        ExpenditureDetailPanel.setLayout(null);
        ExpenditureDetailPanel.setBounds(10, 120, 380, 370);
        ExpenditureDetailPanel.setBorder(new TitledBorder("Ausgaben Details"));

        ExpenditureDistributionPanel = new JPanel();
        ExpenditureDistributionPanel.setLayout(null);
        ExpenditureDistributionPanel.setBounds(410, 120, 230, 100);
        ExpenditureDistributionPanel.setBorder(new TitledBorder("Ausgaben Aufteilung"));

        // create table for expenditure sum per party
        createExpenditureSumPerPartyTable();

        // create table for expenditures
        createExpenditureDetailTable();

        // create table for expenditure distribution
        // should only appear later when something is selected in the detail table
        createExpenditureDistributionTable();

        ExpenditureSumPerPartyScrollPane = new JScrollPane(ExpenditureSumPerPartyTable);
        ExpenditureSumPerPartyScrollPane.setBounds(15, 20, 200, 70);
        ExpenditureSumPerPartyPanel.add(ExpenditureSumPerPartyScrollPane);

        ExpenditureDetailScrollPane = new JScrollPane(ExpenditureDetailTable);
        ExpenditureDetailScrollPane.setBounds(15, 20, 350, 340);
        ExpenditureDetailPanel.add(ExpenditureDetailScrollPane);

        ExpenditureDistributionScrollPane = new JScrollPane(new JTextArea("...bitte Ausgabe auswäheln um\n die Aufteilung hier anzuzeigen..."));
        // ExpenditureDistributionScrollPane = new JScrollPane(ExpenditureDistributionTable);
        ExpenditureDistributionScrollPane.setBounds(15, 20, 200, 70);
        ExpenditureDistributionPanel.add(ExpenditureDistributionScrollPane);

        // put all to the Frame
        ExpenditureWindow.add(ExpenditureSumPerPartyPanel);
        ExpenditureWindow.add(ExpenditureDetailPanel);
        ExpenditureWindow.add(ExpenditureDistributionPanel);
        ExpenditureWindow.validate();
        ExpenditureWindow.repaint();

        ExpenditureWindow.setVisible(true);

        // add self defined SelectionListener for the detail table
        // that listener will show the distributioninfos of the selected expenditure for each party
        // in a separate table (ExpenditureDistributionTable)
        ExpenditureDetailTable.getSelectionModel().addListSelectionListener(new ExpenditureDetailTableSelectionListener(ExpenditureDetailTable, ExpenditureDistributionScrollPane, ExpenditureDistributionTable));

        // test to get the thicknes of one of the vertical scrollbars
        // System.out.println(ExpenditureSumPerPartyScrollPane.getVerticalScrollBar().getWidth());
        // System.out.println(ExpenditureSumPerPartyTable.getColumnModel().getColumn(1).getWidth());

    }

    private void createExpenditureSumPerPartyTable() {
        ExpenditureSumPerPartyTable = new JTable(new ExpenditureSumPerPartyTableModel(cn));
        ExpenditureSumPerPartyTable.setDefaultRenderer(Object.class, new ExpenditureSumPerPartyTableCellRenderer());

        // define the width for some columns
        ExpenditureSumPerPartyTable.getColumnModel().getColumn(0).setMinWidth(120);
        ExpenditureSumPerPartyTable.getColumnModel().getColumn(0).setMaxWidth(120);
    }

    private void createExpenditureDetailTable() {
        ExpenditureDetailTable = new JTable(new ExpenditureDetailTableModel(cn));
        ExpenditureDetailTable.setDefaultRenderer(Object.class, new ExpenditureDetailTableCellRenderer());

        // define the width for some columns
        ExpenditureDetailTable.getColumnModel().getColumn(0).setMinWidth(110);
        ExpenditureDetailTable.getColumnModel().getColumn(0).setMaxWidth(110);
    }

    private void createExpenditureDistributionTable() {
        ExpenditureDistributionTable = new JTable(new ExpenditureDistributionTableModel(cn));
        ExpenditureDistributionTable.setDefaultRenderer(Object.class, new ExpenditureDetailTableCellRenderer());

        // define the width for some columns
        ExpenditureDistributionTable.getColumnModel().getColumn(0).setMinWidth(120);
        ExpenditureDistributionTable.getColumnModel().getColumn(0).setMaxWidth(120);

    }
}
