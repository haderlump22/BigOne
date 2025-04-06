package de.rachel.bigone.editors;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import de.rachel.bigone.DBTools;
/**
 * Generiert eine Combobox für die Auswahl eines Kontoereignis beim Importieren von
 * Kontobewegungen.
 */
public class ComboTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 4917922491523056278L;
	private JComboBox<String> component = new JComboBox<String>();
	private Connection cn = null;
	private boolean cellEditingStopped = false;

	public ComboTableCellEditor(Connection LoginCN) {
		cn = LoginCN;
		// notwendig damit eine Auswahl, für das Beenden des Editmodus der Zelle sorgt
		component.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// cellEdititngStopped wird hier mit überprüft, damit, wenn ein Tastendruck die Änderung (auswahl eines anderen Elements)
				// bewirkt, die Combobox nicht geschlossen wird
				if (e.getStateChange() == ItemEvent.SELECTED && cellEditingStopped) {
					fireEditingStopped();
				}
			}
        });

		//test keylistener
		component.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// damit das StateChange Event die cobobox nicht schliesst
				cellEditingStopped = false;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

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
				fireEditingStopped();
                //fireEditingCanceled();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

			}
        });
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int colIndex) {
		// nur wenn die Combobox keine Einträge hat wird sie gefüllt, damit sie nicht bei jedem Aufruf von getTableCellEditorComponent gefüllt wird
		if (component.getItemCount() == 0) {
			fill_component();
		}

		return component;
	}
	@Override
    public boolean stopCellEditing() {
        return cellEditingStopped;
    }
    public Object getCellEditorValue() {
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
