package de.rachel.bigone.Editors;

import static de.rachel.bigone.DatabaseConstants.DRIVER;
import static de.rachel.bigone.DatabaseConstants.PASS;
import static de.rachel.bigone.DatabaseConstants.URL;
import static de.rachel.bigone.DatabaseConstants.USER;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class ComboTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4917922491523056278L;
	private JComboBox component = new JComboBox(); 
	 
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
			try
			{
				Class.forName(DRIVER);
			}
			catch ( ClassNotFoundException e )
		    {
		      System.err.println( "Keine Treiber-Klasse!" );
		      return;
		    }
			Connection con = null;
		    try
		    {
		      con = DriverManager.getConnection( URL, USER, PASS );
		      Statement stmt = con.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT ereigniss_id, ereigniss_krzbez FROM kontenereignisse order by 2;" );
		      while ( rs.next() )
		        component.addItem(rs.getString(2) + " (" + rs.getInt(1)+")");
		      rs.close();
		      stmt.close();
		    }
		    catch ( SQLException e )
		    {
		      e.printStackTrace();
		      return;
		    }
		    finally
		    {
		      if ( con != null )
		        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
		    }
		}
}
