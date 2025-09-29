package de.rachel.bigone.listeners;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;

import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingSumOverviewMouseListener extends MouseAdapter {
    private JFormattedTextField billingMonth, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue,
            sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue;
    private JTable jointAccountClosingDetailTable;

    public JointAccountClosingSumOverviewMouseListener(JFormattedTextField billingMonth,
            JTable jointAccountClosingDetailTable, JFormattedTextField sumOverviewNegativePlanedValue,
            JFormattedTextField sumOverviewNegativeUnplanedValue, JFormattedTextField sumOverviewPositivePlanedValue,
            JFormattedTextField sumOverviewPositiveUnplanedValue) {
        this.billingMonth = billingMonth;
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.sumOverviewNegativePlanedValue = sumOverviewNegativePlanedValue;
        this.sumOverviewNegativeUnplanedValue = sumOverviewNegativeUnplanedValue;
        this.sumOverviewPositivePlanedValue = sumOverviewPositivePlanedValue;
        this.sumOverviewPositiveUnplanedValue = sumOverviewPositiveUnplanedValue;
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        // first we set the font of all four Textfields to plain
        this.sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        this.sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
        this.sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        this.sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));

        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", billingMonth.getText())) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                JFormattedTextField sourceTextField = (JFormattedTextField) mouseEvent.getSource();
                sourceTextField.setFont(new Font(null, Font.BOLD, 14));
                String[] ids = sourceTextField.getName().replaceAll("[{}]", "").split(",");

                ((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
                        .setDetailIdsForMarkingDifferenceValue(ids);
            }
        }
    }
}
