package de.rachel.bigone;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.rachel.bigone.listeners.TransferAmountDetailTableMouseListener;
import de.rachel.bigone.models.TransferAmountDetailTableModel;
import de.rachel.bigone.renderer.TransferAmountDetailTableCellRenderer;

import javax.swing.JTable;

public class TransferAmount {
    private Connection cn = null;

    private JFrame TransferAmountWindow;
    private JPanel TransferAmountDetailTablePanel;
    private JScrollPane TransferAmountDetailTableScrollPane;
    private JTable TransferAmountDetailTable;

    TransferAmount(Connection LoginCN) {
        cn = LoginCN;

        TransferAmountWindow = new JFrame("Überweisungsbeträge");
        TransferAmountWindow.setSize(785, 530);
        TransferAmountWindow.setLocation(200, 200);
        TransferAmountWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TransferAmountWindow.setLayout(null);
        TransferAmountWindow.setResizable(false);

        TransferAmountDetailTablePanel = new JPanel();
        TransferAmountDetailTablePanel.setLayout(null);
        TransferAmountDetailTablePanel.setBounds(10, 120, 300, 370);
        TransferAmountDetailTablePanel.setBorder(new TitledBorder("Überweisungsbeträge"));

        // create Table for the Details of Income of Partys
        createTransferAmountDetailTable();

        // scrollpane for the tabel with the sums of values
        TransferAmountDetailTableScrollPane = new JScrollPane(TransferAmountDetailTable);
        TransferAmountDetailTableScrollPane.setBounds(15, 20, 270, 340);
        TransferAmountDetailTablePanel.add(TransferAmountDetailTableScrollPane);

        // put all to the Frame
        TransferAmountWindow.add(TransferAmountDetailTablePanel);
        TransferAmountWindow.validate();
        TransferAmountWindow.repaint();

        TransferAmountWindow.setVisible(true);
    }

    private void createTransferAmountDetailTable() {
        TransferAmountDetailTable = new JTable(new TransferAmountDetailTableModel(cn));
        TransferAmountDetailTable.setDefaultRenderer(Object.class, new TransferAmountDetailTableCellRenderer());

        // define the width for some columns
        TransferAmountDetailTable.getColumnModel().getColumn(0).setMinWidth(100);
        TransferAmountDetailTable.getColumnModel().getColumn(0).setMaxWidth(100);
        TransferAmountDetailTable.getColumnModel().getColumn(1).setMinWidth(73);
        TransferAmountDetailTable.getColumnModel().getColumn(1).setMaxWidth(73);
        TransferAmountDetailTable.getColumnModel().getColumn(2).setMinWidth(80);
        TransferAmountDetailTable.getColumnModel().getColumn(2).setMaxWidth(80);

        // selbst definierten Mouselistener hinzufügen
		TransferAmountDetailTable.addMouseListener(new TransferAmountDetailTableMouseListener(TransferAmountDetailTable, TransferAmountWindow));
    }
}
