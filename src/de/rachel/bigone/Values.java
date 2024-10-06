
package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.Editors.DateTableCellEditor;
import de.rachel.bigone.Editors.DecimalTableCellEditor;
import de.rachel.bigone.Models.ValuesTableModel;
import de.rachel.bigone.Renderer.ValuesTableCellRenderer;

/**
 * @author Normen Rachel
 */

public class Values {
	private Connection cn = null;
	private JFrame valuewindow;
	private JFormattedTextField txtValue, txtLiquiDate;
	private Font fontTxtFields, fontCmbBoxes;
	private JTable table;
	private ValuesTableModel model;
	private JComboBox<String> cmbKto;
	private JCheckBox withLiquiDate;

	Values(Connection LoginCN) {
		cn = LoginCN;
		valuewindow = new JFrame("Beträge finden");
		valuewindow.setSize(785, 480);
		valuewindow.setLocation(200, 200);
		valuewindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		valuewindow.setLayout(null);
		valuewindow.setResizable(false);

		// schriftenfestlegungen
		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
		fontCmbBoxes = new Font("Arial", Font.PLAIN, 14);

		// textfeld fuer den zu suchenden Betrag definieren
		txtValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		txtValue.setBounds(30, 25, 100, 25);
		txtValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtValue.setFont(fontTxtFields);
		txtValue.setText("0,00");
		txtValue.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent fe) {

			}

			public void focusGained(FocusEvent fe) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						txtValue.selectAll();
					}
				});
			}
		});
		txtValue.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					model = (ValuesTableModel) table.getModel();
					model.aktualisiere(txtValue.getText().replace(".", "").replace(',', '.'),
							BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0),
							cmbKto.getSelectedItem().toString().replace(" ", ""));
				}
			}

			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					txtValue.requestFocus();
				}
			}

			public void keyTyped(KeyEvent arg0) {

			}

		});

		String now = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		// String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		try {
			txtLiquiDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		txtLiquiDate.setBounds(140, 25, 110, 25);
		txtLiquiDate.setHorizontalAlignment(JTextField.RIGHT);
		txtLiquiDate.setFont(fontTxtFields);
		// aktuellen Monat als Liquimonat setzten
		txtLiquiDate.setText("01-" + now.substring(3, 5) + "-20" + now.substring(8));

		// add Account choose and fill it with valid Account IDs (IBAN)
		cmbKto = new JComboBox<String>();
		cmbKto.setBounds(260, 25, 250, 25);
		cmbKto.setFont(fontCmbBoxes);

		// and fill it with Values
		fill_cmbKto();

		// Create the check box to specify whether liquidatum is in Query or not
		withLiquiDate = new JCheckBox("mit Liquidatum suchen", true);
		withLiquiDate.setBounds(520, 25, 180, 25);

		// ad an Valuechange Listener for aktivate/deaktivate the LiquiDate Textfield
		withLiquiDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (withLiquiDate.isSelected()) {
					txtLiquiDate.setEnabled(true);
					withLiquiDate.setText("mit Liquidatum suchen");
				} else {
					txtLiquiDate.setEnabled(false);
					withLiquiDate.setText("ohne Liquidatum suchen");
				}
			}
		});		

		// put all Elements on the Window
		valuewindow.add(txtValue);
		valuewindow.add(txtLiquiDate);
		valuewindow.add(cmbKto);
		valuewindow.add(withLiquiDate);
		zeichne_tabelle();

		valuewindow.setVisible(true);

	}

	private void zeichne_tabelle() {

		table = new JTable(new ValuesTableModel(txtValue.getText().replace(".", "").replace(',', '.'),
				BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0),
				cmbKto.getSelectedItem().toString().replace(" ", ""), cn));
		ValuesTableCellRenderer ren = new ValuesTableCellRenderer();
		table.setDefaultRenderer(Object.class, ren);
		table.getColumnModel().getColumn(2).setCellEditor(new DateTableCellEditor());
		table.getColumnModel().getColumn(3).setCellEditor(new DecimalTableCellEditor());
		table.getColumnModel().getColumn(5).setCellEditor(new DateTableCellEditor());
		// table.setDefaultEditor(Object.class, new ValuesTableCellEditor());

		// fuer einige spalten feste breiten einrichten
		table.getColumnModel().getColumn(0).setMinWidth(55);
		table.getColumnModel().getColumn(0).setMaxWidth(55);
		table.getColumnModel().getColumn(1).setMinWidth(25);
		table.getColumnModel().getColumn(1).setMaxWidth(25);
		table.getColumnModel().getColumn(2).setMinWidth(78);
		table.getColumnModel().getColumn(2).setMaxWidth(78);
		table.getColumnModel().getColumn(3).setMinWidth(65);
		table.getColumnModel().getColumn(3).setMaxWidth(65);
		table.getColumnModel().getColumn(5).setMinWidth(78);
		table.getColumnModel().getColumn(5).setMaxWidth(78);
		table.getColumnModel().getColumn(6).setMinWidth(120);
		table.getColumnModel().getColumn(6).setMaxWidth(120);

		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(30, 70, 725, 355);

		valuewindow.add(sp);
		valuewindow.validate();
		valuewindow.repaint();
	}

	private void fill_cmbKto() {
		DBTools getter = new DBTools(cn);

		getter.select("SELECT konten.iban, konten.bemerkung " +
				"FROM konten " +
				"where konten.gueltig = TRUE;", 1);

		Object[][] cmbKtoValues = getter.getData();

		for (Object[] cmbKtoValue : cmbKtoValues)
			cmbKto.addItem(BigOneTools.getIbanFormatted(cmbKtoValue[0].toString()));
	}
}