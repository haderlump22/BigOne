package de.rachel.bigone.Renderer;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

// import de.rachel.bigone.BigOneTools;

public class ExpenditureDetailTableCellRenderer implements TableCellRenderer {

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
            label.setText("%.02f".formatted((Double) value));
         }

         if (column == 2) {
            label.setHorizontalAlignment(JLabel.CENTER);
         }

         if (column == 3) {
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setText(new SimpleDateFormat("MMM yyyy").format((Date) value));
            // label.setText(BigOneTools.datum_wandeln(value.toString(),1));
         }

         if (column == 5) {
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setText("%.02f".formatted((Double) value));
         }

         if (column == 7) {
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setText("%.02f".formatted((Double) value));
         }

         return label;
      } else {
         return label;
      }
   }
}
