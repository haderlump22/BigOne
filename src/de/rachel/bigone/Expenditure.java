package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
// import javax.swing.JScrollPane;
// import javax.swing.JTable;
import javax.swing.border.TitledBorder;

public class Expenditure {
    private Connection cn = null;

    private JFrame ExpenditureWindow;
    private JPanel ExpenditureDetailPanel, ExpenditureSumPerPartyPanel;
    private JScrollPane ExpenditureSumPerPartyScrollPane, ExpenditureDetailScrollPane;
    // private JTable SumOfIncomePerPartyTable, IncomeDetailTable;

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
        ExpenditureSumPerPartyPanel.setBounds(10, 10, 305, 100);
        ExpenditureSumPerPartyPanel.setBorder(new TitledBorder("Anteil Ausgaben pro Person"));

        ExpenditureDetailPanel = new JPanel();
        ExpenditureDetailPanel.setLayout(null);
        ExpenditureDetailPanel.setBounds(10, 120, 690, 370);
        ExpenditureDetailPanel.setBorder(new TitledBorder("Details der Ausgaben"));

        // scrollpane for the table with the sums Expenditures per Party
        ExpenditureSumPerPartyScrollPane = new JScrollPane();
        ExpenditureSumPerPartyScrollPane.setBounds(15, 20, 275, 70);
        ExpenditureSumPerPartyPanel.add(ExpenditureSumPerPartyScrollPane);

        // scrollpane for the Expenditures Detail Table
        ExpenditureDetailScrollPane = new JScrollPane();
        ExpenditureDetailScrollPane.setBounds(15, 20, 660, 340);
        ExpenditureDetailPanel.add(ExpenditureDetailScrollPane);

        // put all to the Frame
        ExpenditureWindow.add(ExpenditureSumPerPartyPanel);
        ExpenditureWindow.add(ExpenditureDetailPanel);
        ExpenditureWindow.validate();
        ExpenditureWindow.repaint();

        ExpenditureWindow.setVisible(true);
    }
}
