package de.rachel.bigone.Renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class SalaryBasesSumOfIncomePerPartyTableCellRenderer implements TableCellRenderer {

   public Component getTableCellRendererComponent(
         JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      JLabel label = null;

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
            label.setText("%.02f".formatted((Number) value));
         }

         if (column == 2) {
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setText("%.02f".formatted((Double) value));
         }
         return label;
      } else {
         return label;
      }
   }
}
