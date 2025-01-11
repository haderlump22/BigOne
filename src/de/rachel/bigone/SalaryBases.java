package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.rachel.bigone.Models.IncomeDetailTableModel;
import de.rachel.bigone.Models.SumOfIncomePerPartyTableModel;
import de.rachel.bigone.Renderer.IncomeDetailTableCellRenderer;
import de.rachel.bigone.Renderer.SumOfIncomePerPartyTableCellRenderer;

import javax.swing.JTable;

public class SalaryBases {
    private Connection cn = null;

    private JFrame SalaryBasesWindow;
    private JPanel CurrentSumOfIncomePerPartyPanel, IncomeDetailTablePanel;
    private JScrollPane CurrentSumOfIncomePerPartyScrollPane, IncomeDetailTableScrollPane;
    private JTable SumOfIncomePerPartyTable, IncomeDetailTable;

    SalaryBases(Connection LoginCN) {
        cn = LoginCN;

        SalaryBasesWindow = new JFrame("Gehaltsgrundlagen");
        SalaryBasesWindow.setSize(785, 530);
        SalaryBasesWindow.setLocation(200, 200);
        SalaryBasesWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SalaryBasesWindow.setLayout(null);
        SalaryBasesWindow.setResizable(false);

        CurrentSumOfIncomePerPartyPanel = new JPanel();
        CurrentSumOfIncomePerPartyPanel.setLayout(null);
        CurrentSumOfIncomePerPartyPanel.setBounds(10, 10, 305, 100);
        CurrentSumOfIncomePerPartyPanel.setBorder(new TitledBorder("mtl. Einkünfte"));

        IncomeDetailTablePanel = new JPanel();
        IncomeDetailTablePanel.setLayout(null);
        IncomeDetailTablePanel.setBounds(10, 120, 690, 370);
        IncomeDetailTablePanel.setBorder(new TitledBorder("Details der Einkünfte"));

        // create Table for Current Income Sums of each Party
        createSumOfIncomePerPartyTable();

        // create Table for the Details of Income of Partys
        createIncomeDetailTable();

        // scrollpane for the tabel with the sums of values
        CurrentSumOfIncomePerPartyScrollPane = new JScrollPane(SumOfIncomePerPartyTable);
        CurrentSumOfIncomePerPartyScrollPane.setBounds(15, 20, 275, 70);
        CurrentSumOfIncomePerPartyPanel.add(CurrentSumOfIncomePerPartyScrollPane);

        // scrollpane for the Detail Table
        IncomeDetailTableScrollPane = new JScrollPane(IncomeDetailTable);
        IncomeDetailTableScrollPane.setBounds(15, 20, 660, 340);
        IncomeDetailTablePanel.add(IncomeDetailTableScrollPane);

        // put all to the Frame
        SalaryBasesWindow.add(CurrentSumOfIncomePerPartyPanel);
        SalaryBasesWindow.add(IncomeDetailTablePanel);
        SalaryBasesWindow.validate();
        SalaryBasesWindow.repaint();

        SalaryBasesWindow.setVisible(true);
    }

    private void createSumOfIncomePerPartyTable() {
        SumOfIncomePerPartyTable = new JTable(new SumOfIncomePerPartyTableModel(cn));
        SumOfIncomePerPartyTable.setDefaultRenderer(Object.class, new SumOfIncomePerPartyTableCellRenderer());

        // define the width for some columns
        SumOfIncomePerPartyTable.getColumnModel().getColumn(0).setMinWidth(100);
        SumOfIncomePerPartyTable.getColumnModel().getColumn(0).setMaxWidth(100);
        SumOfIncomePerPartyTable.getColumnModel().getColumn(1).setMinWidth(70);
        SumOfIncomePerPartyTable.getColumnModel().getColumn(1).setMaxWidth(70);
    }

    private void createIncomeDetailTable() {
        IncomeDetailTable = new JTable(new IncomeDetailTableModel(cn));
        IncomeDetailTable.setDefaultRenderer(Object.class, new IncomeDetailTableCellRenderer());

        // define the width for some columns
        IncomeDetailTable.getColumnModel().getColumn(0).setMinWidth(100);
        IncomeDetailTable.getColumnModel().getColumn(0).setMaxWidth(100);
        IncomeDetailTable.getColumnModel().getColumn(1).setMinWidth(70);
        IncomeDetailTable.getColumnModel().getColumn(1).setMaxWidth(70);
        IncomeDetailTable.getColumnModel().getColumn(2).setMinWidth(78);
        IncomeDetailTable.getColumnModel().getColumn(2).setMaxWidth(78);
    }
}
