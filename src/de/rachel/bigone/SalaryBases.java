package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class SalaryBases {
    private Connection cn = null;

    private JFrame SalaryBasesWindow;
    private JPanel pnlSumOfIncomePerParty;
    private JScrollPane spSumOfIncomePerParty;

    SalaryBases(Connection LoginCN) {
        cn = LoginCN;

		SalaryBasesWindow = new JFrame("Gehaltsgrundlagen");
		SalaryBasesWindow.setSize(785, 530);
		SalaryBasesWindow.setLocation(200, 200);
		SalaryBasesWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SalaryBasesWindow.setLayout(null);
		SalaryBasesWindow.setResizable(false);

        pnlSumOfIncomePerParty = new JPanel();
		pnlSumOfIncomePerParty.setLayout(null);
		pnlSumOfIncomePerParty.setBounds(10, 10, 500, 80);
		pnlSumOfIncomePerParty.setBorder(new TitledBorder("mtl. Einkünfte"));

        // scrollpane for the tabel with the values
        spSumOfIncomePerParty = new JScrollPane();
        pnlSumOfIncomePerParty.add(spSumOfIncomePerParty);
  
        // put all to the Frame
        SalaryBasesWindow.add(pnlSumOfIncomePerParty);
        SalaryBasesWindow.validate();
        SalaryBasesWindow.repaint();

        SalaryBasesWindow.setVisible(true);
    }
}
