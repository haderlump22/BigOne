package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.rachel.bigone.Models.SumOfIncomePerPartyTableModel;
import de.rachel.bigone.Renderer.SumOfIncomePerPartyTableCellRenderer;

import javax.swing.JTable;

public class SalaryBases {
    private Connection cn = null;

    private JFrame SalaryBasesWindow;
    private JPanel pnlCurrentSumOfIncomePerParty;
    private JScrollPane spCurrentSumOfIncomePerParty;
    private JTable SumOfIncomePerPartyTable;

    SalaryBases(Connection LoginCN) {
        cn = LoginCN;

		SalaryBasesWindow = new JFrame("Gehaltsgrundlagen");
		SalaryBasesWindow.setSize(785, 530);
		SalaryBasesWindow.setLocation(200, 200);
		SalaryBasesWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SalaryBasesWindow.setLayout(null);
		SalaryBasesWindow.setResizable(false);

        pnlCurrentSumOfIncomePerParty = new JPanel();
		pnlCurrentSumOfIncomePerParty.setLayout(null);
		pnlCurrentSumOfIncomePerParty.setBounds(10, 10, 500, 100);
		pnlCurrentSumOfIncomePerParty.setBorder(new TitledBorder("mtl. Einkünfte"));

        // create Table for Current Income Sums of each Party
        createSumOfIncomePerPartyTable();

        // scrollpane for the tabel with the values
        spCurrentSumOfIncomePerParty = new JScrollPane(SumOfIncomePerPartyTable);
        spCurrentSumOfIncomePerParty.setBounds(15,20,460,70);
        pnlCurrentSumOfIncomePerParty.add(spCurrentSumOfIncomePerParty);
  
        // put all to the Frame
        SalaryBasesWindow.add(pnlCurrentSumOfIncomePerParty);
        SalaryBasesWindow.validate();
        SalaryBasesWindow.repaint();
        
        SalaryBasesWindow.setVisible(true);
    }
    private void createSumOfIncomePerPartyTable () {
        SumOfIncomePerPartyTable = new JTable(new SumOfIncomePerPartyTableModel(cn));
		SumOfIncomePerPartyTable.setDefaultRenderer( Object.class, new SumOfIncomePerPartyTableCellRenderer() );
    }
}
