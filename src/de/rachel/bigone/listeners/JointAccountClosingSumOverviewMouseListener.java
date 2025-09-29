package de.rachel.bigone.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;

import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingSumOverviewMouseListener extends MouseAdapter {
    private JFormattedTextField billingMonth;
    private JTable jointAccountClosingDetailTable;

    public JointAccountClosingSumOverviewMouseListener(JFormattedTextField billingMonth, JTable jointAccountClosingDetailTable) {
        this.billingMonth = billingMonth;
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
    }
    public void mouseReleased(MouseEvent mouseEvent) {
        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", billingMonth.getText())) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                JFormattedTextField sourceTextField = (JFormattedTextField)mouseEvent.getSource();
                String[] ids = sourceTextField.getName().replaceAll("[{}]", "").split(",");

                ((JointAccountClosingDetailTableModel)jointAccountClosingDetailTable.getModel()).setMarkableRows(ids);
            }
        }
    }
}
