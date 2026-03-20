package de.rachel.bigone.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class DecimalTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTextField component = new JTextField();

    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int col) {
        component.setHorizontalAlignment(JFormattedTextField.RIGHT);
        component.setText(value.toString().replace('.', ','));
        return component;
    }

    public Object getCellEditorValue() {
        return component.getText().replace(',', '.');
    }
}
