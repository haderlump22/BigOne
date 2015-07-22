package de.rachel.bigone.Editors;

import java.awt.Component;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

import de.rachel.bigone.DBTools;

public class ComboTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4917922491523056278L;
	private JComboBox<String> component = new JComboBox<String>();
	private Connection cn=null;
	 
	public ComboTableCellEditor(Connection LoginCN) {
		cn = LoginCN;
	}
	public Component getTableCellEditorComponent( 
	      JTable table, Object value, boolean isSelected, int rowIndex, int colIndex ) { 
	    	//noch keine eintraege in combobox sind
		  	//diese fuellen
		  	if(component.getItemCount() == 0) {
	    		fill_component();
	    	}
	    	return component; 
	  } 
	  public Object getCellEditorValue() 
	  { 
		  //damit bei erneuter auswahl immer der erste eintrag selectiert ist
		  //dieser kleine umweg
		  String strLager = new String(component.getSelectedItem().toString());
		  component.setSelectedIndex(0);
		  return strLager;
			
	  }
	  private void fill_component() {
		  DBTools getter = new DBTools(cn);
		  
		  getter.select("SELECT ereigniss_id, ereigniss_krzbez FROM kontenereignisse order by 2;",2);

		  Object[][] cmbComponentValues = getter.getData();
		  
		  for(Object[] cmbComponentValue : cmbComponentValues)
		  	component.addItem(cmbComponentValue[1] + " (" + cmbComponentValue[0]+")");
	  }
}
