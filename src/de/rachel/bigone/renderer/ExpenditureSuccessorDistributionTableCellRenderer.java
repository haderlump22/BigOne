package de.rachel.bigone.renderer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.rachel.bigone.models.JointAccountClosingDetailTableModel;
import de.rachel.bigone.models.ExpenditureSuccessorDistributionTableModel;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;

public class ExpenditureSuccessorDistributionTableCellRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = null;
        ExpenditureSuccessorDistributionTableModel successorDivideTableModel;

        successorDivideTableModel = (ExpenditureSuccessorDistributionTableModel) table.getModel();

        // only react when value is not null
        if (value != null) {
            if (!(value instanceof JLabel)) {
                label = new JLabel(value.toString());
            } else {
                label = (JLabel) value;
            }
            label.setOpaque(true);
            label.setFont(table.getFont());
            label.setForeground(table.getForeground());
            label.setBackground(table.getBackground());

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }

            if (column == 0) {
                label.setHorizontalAlignment(JLabel.LEFT);
            }

            if (column == 1) {
                label.setHorizontalAlignment(JLabel.RIGHT);
                label.setText("%.02f".formatted((Double) value));
            }

            if (column == 2) {
                label.setHorizontalAlignment(JLabel.RIGHT);
                label.setText("%.02f".formatted((Double) value));
            }

            if (column == 3) {
                label.setHorizontalAlignment(JLabel.RIGHT);
                label.setText("%.02f".formatted((Double) value));
            }

            return label;
        } else {
            return label;
        }
    }
}
