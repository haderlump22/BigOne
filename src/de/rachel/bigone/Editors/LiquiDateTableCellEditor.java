package de.rachel.bigone.Editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import de.rachel.bigone.BigOneTools;

public class LiquiDateTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4917922491523056278L;
	private JTextField component = new JTextField(); 
	 
	  public Component getTableCellEditorComponent( 
	      JTable table, Object value, boolean isSelected, int rowIndex, int colIndex ) { 
	    	component.setText("");	//sicherheitshalber leer machen da sonst bei erneutem aufruf
	    							//und leerem value der letzte wert zurueckgegeben wird
		  	if(value != null)
	    		component.setText(BigOneTools.datum_wandeln(value.toString(),1));
	    	return component; 
	  } 
	  public Object getCellEditorValue() 
	  { 
		  if(component.getText().length() > 0)
			  return BigOneTools.datum_wandeln(component.getText(),0); 
		  else
			  return null;
	  }
}
