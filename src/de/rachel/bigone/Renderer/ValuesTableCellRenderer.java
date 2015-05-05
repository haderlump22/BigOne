package de.rachel.bigone.Renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.rachel.bigone.BigOneTools;

public class ValuesTableCellRenderer implements TableCellRenderer {


   public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      JLabel label;
      
      if(!(value instanceof JLabel)) {
         label = new JLabel((String)value);
      } else {
         label = (JLabel) value;
      }
      label.setOpaque(true);
      label.setFont(table.getFont());
      label.setForeground(table.getForeground());
      label.setBackground(table.getBackground());

      if(isSelected) {
         label.setBackground(table.getSelectionBackground());
         label.setForeground(table.getSelectionForeground());
      }
      if(column == 0) {
         label.setHorizontalAlignment(JLabel.RIGHT);
      }
      if(column == 1) {
	     label.setHorizontalAlignment(JLabel.CENTER);
	  }
      if(column == 2) {
	     label.setHorizontalAlignment(JLabel.CENTER);
	     label.setText(BigOneTools.datum_wandeln(value.toString(),1));
	  }
      if(column == 3) {
         label.setHorizontalAlignment(JLabel.RIGHT);
         label.setText(value.toString().replace('.', ','));
      }
      if(column == 5) {
	     label.setHorizontalAlignment(JLabel.CENTER);
	     if(value != null) {//nur wenn es ein liquidatum gibt umwandeln
	    	 label.setText(BigOneTools.datum_wandeln(value.toString(),1));
	     }
	  }
      return label;
   }
}
