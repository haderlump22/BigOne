package de.rachel.bigone.Editors;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class DecimalTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4917922491523056278L;
	private JTextField component = new JTextField(); 
	 
	  public Component getTableCellEditorComponent( 
	      JTable table, Object value, boolean isSelected, int rowIndex, int colIndex ) { 
	    	component.setText(value.toString().replace('.',','));
	    	return component; 
	  } 
	  public Object getCellEditorValue() 
	  { 
		  return component.getText().replace(',','.'); 
	  }
}
