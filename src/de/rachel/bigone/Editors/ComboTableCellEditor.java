package de.rachel.bigone.Editors;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import de.rachel.bigone.DBTools;

public class ComboTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4917922491523056278L;
	private JComboBox<String> component = new JComboBox<String>();
	private Connection cn = null;
	private boolean cellEditingStopped = false;

	public ComboTableCellEditor(Connection LoginCN) {
		cn = LoginCN;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
			int colIndex) {
		// isSelected habe ich hier abgefragt weil das ankliken einer Zelle aus einer
		// anderen Zeile herraus
		// immer eine nicht aufgeklapte Combobox erzeugt hat die sich dabei aber nicht
		// aufklappte

	
		// notwendig damit eine Auswahl für ein beenden des Editmodus der Zelle sorgt
		component.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fireEditingStopped();
				}
			}
        });

		// auch wichtig damit der Editing Modus der Celle beendet wird
		component.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                cellEditingStopped = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                cellEditingStopped = true;
                fireEditingCanceled();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

		// nur wenn die Combobox keine Einträge hat wird sie gefüllt
		if (component.getItemCount() == 0) {
			fill_component();
		}

		return component;
	}
	@Override
    public boolean stopCellEditing() {
        return cellEditingStopped;
    }
    public Object getCellEditorValue() 
    { 
		//damit bei erneuter auswahl immer der erste eintrag selectiert ist
    	//dieser kleine umweg
    	// String strLager = new String(component.getSelectedItem().toString());
    	// component.setSelectedIndex(0);
		// return strLager;
		
		// die bisherige Auswahl soll bestehen bleiben
		return component.getSelectedItem().toString();
		
    }
	private void fill_component() {
		DBTools getter = new DBTools(cn);
		  
		getter.select("SELECT ereigniss_id, ereigniss_krzbez FROM kontenereignisse WHERE gueltig = 'TRUE' order by 2;",2);

		Object[][] cmbComponentValues = getter.getData();
		  
		for(Object[] cmbComponentValue : cmbComponentValues)
			component.addItem(cmbComponentValue[1] + " (" + cmbComponentValue[0]+")");
	}
}
