package de.rachel.bigone.listeners;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;

public class JointAccountClosingSumOverviewMouseListener extends MouseAdapter {
    private JFormattedTextField billingMonth, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue,
            sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue;
    private JTable jointAccountClosingDetailTable;
    private Connection LoginCN;

    public JointAccountClosingSumOverviewMouseListener(JFormattedTextField billingMonth,
            JTable jointAccountClosingDetailTable, JFormattedTextField sumOverviewNegativePlanedValue,
            JFormattedTextField sumOverviewNegativeUnplanedValue, JFormattedTextField sumOverviewPositivePlanedValue,
            JFormattedTextField sumOverviewPositiveUnplanedValue, Connection LoginCN) {
        this.LoginCN = LoginCN;
        this.billingMonth = billingMonth;
        this.jointAccountClosingDetailTable = jointAccountClosingDetailTable;
        this.sumOverviewNegativePlanedValue = sumOverviewNegativePlanedValue;
        this.sumOverviewNegativeUnplanedValue = sumOverviewNegativeUnplanedValue;
        this.sumOverviewPositivePlanedValue = sumOverviewPositivePlanedValue;
        this.sumOverviewPositiveUnplanedValue = sumOverviewPositiveUnplanedValue;
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        // first we set the font of all four Textfields to plain
        sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));

        if (Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", billingMonth.getText())) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                JFormattedTextField sourceTextField = (JFormattedTextField) mouseEvent.getSource();
                sourceTextField.setFont(new Font(null, Font.BOLD, 14));

                //String[] ids = sourceTextField.getName().replaceAll("[{}]", "").split(",");
                getIdsOfSumSources(sourceTextField.getName(), billingMonth.getText());
                //((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
                //        .setDetailIdsForMarkingDifferenceValue(ids);
            }
        }
    }

    private void getIdsOfSumSources(String sumType, String billingMonth) {
        DBTools getter = new DBTools(LoginCN);

		getter.select("""
                SELECT ARRAY_AGG(ha_abschlusssummen."abschlussDetailId") idsOfSumSources
                FROM ha_abschlusssummen, ha_abschlussdetails
                WHERE ha_abschlusssummen."summenArt" = '%s'
                AND ha_abschlussdetails."abschlussDetailId" = ha_abschlusssummen."abschlussDetailId"
                AND ha_abschlussdetails."abschlussMonat" = '%s'
                """.formatted("fff", billingMonth), 1);
        try {
            getter.first();
            if (getter.getArray("idsOfSumSources") != null) {

                System.out.println(getter.getArray("idsOfSumSources"));
            } else {
                System.out.println("keine Einträge zu dieser SummenArt gefunden!");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
