package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.Editors.DateTableCellEditor;
import de.rachel.bigone.Editors.DecimalTableCellEditor;
import de.rachel.bigone.Models.ValuesTableModel;
import de.rachel.bigone.Renderer.ValuesTableCellRenderer;

public class Values {
	private Connection cn = null;
	private JFrame valuewindow;
	private JFormattedTextField txtValue, txtLiquiDate;
	private Font fontTxtFields;
	private JTable table;
	private ValuesTableModel model; 
	private JPopupMenu popmen;

	Values(Connection LoginCN){
		cn = LoginCN;
		valuewindow = new JFrame("Beträge finden");
		valuewindow.setSize(785,480);
		valuewindow.setLocation(200,200);
		valuewindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		valuewindow.setLayout(null);
		valuewindow.setResizable(false);

		//schriftenfestlegungen
		fontTxtFields = new Font("Arial",Font.PLAIN,16);
		
		popmen = new JPopupMenu(); 
		JMenuItem ktonfo = new JMenuItem("Kontoinfo");
		ktonfo.addActionListener(new ActionListener(){ 
	        public void actionPerformed(ActionEvent ae){
	        	String transaktions_id;
				model = (ValuesTableModel)table.getModel();
				transaktions_id = "" + model.getValueAt(table.getSelectedRow(), 0);
				
				DBTools getter = new DBTools(cn);
				String sql = "SELECT k.kreditinstitut, ko.bemerkung, p.name, p.vorname, ko.kontonummer, k.blz FROM kreditinstitut k, personen p, konten ko, transaktionen t " +
							"WHERE t.transaktions_id = "+ transaktions_id +" AND t.konten_id = ko.konten_id AND p.personen_id = ko.personen_id AND k.kreditinstitut_id = ko.kreditinstitut_id;";
				getter.select(sql, 6);
				
				//zusammenfuegen der selectierten Werte fuer die MsgBox
				String infotext = getter.getValueAt(0, 0) + "(" + getter.getValueAt(0, 5) + ")\n";
				infotext = infotext + getter.getValueAt(0, 1) + "(" + getter.getValueAt(0, 4) + ")\n";
				infotext = infotext + getter.getValueAt(0, 2) + ", ";
				infotext = infotext + getter.getValueAt(0, 3);
				
				JOptionPane.showMessageDialog(valuewindow, infotext, "Kontoinfo ID= " + transaktions_id, JOptionPane.INFORMATION_MESSAGE);
	        }
	    });
		popmen.add(ktonfo);
		
		//textfeld fuer den zu suchenden Betrag definieren
		txtValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		txtValue.setBounds(30,25,100,25);		
		txtValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtValue.setFont(fontTxtFields);
		txtValue.setText("0,00");
		txtValue.addFocusListener( new FocusListener() {
			public void focusLost( FocusEvent fe ) {
	    	 
			}
			public void focusGained(FocusEvent fe) {
				txtValue.selectAll();
			}
		});
		txtValue.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent ke) {
				// TODO Automatisch erstellter Methoden-Stub
				if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
					model = (ValuesTableModel)table.getModel();
					model.aktualisiere(txtValue.getText().replace(".", "").replace(',', '.'),BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0));
				}
			}

			public void keyReleased(KeyEvent ke) {
				// TODO Automatisch erstellter Methoden-Stub
				if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
					txtValue.requestFocus();
				}
			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Automatisch erstellter Methoden-Stub
				
			}
			
		});
		
		
		String now = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		//String now = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		try {
			txtLiquiDate = new JFormattedTextField(new MaskFormatter("##-##-20##"));
		} catch (ParseException e1) {
			// TODO Automatisch erstellter Catch-Block
			e1.printStackTrace();
		}
		txtLiquiDate.setBounds(140,25,110,25);
		txtLiquiDate.setHorizontalAlignment(JTextField.RIGHT);
		txtLiquiDate.setFont(fontTxtFields);
		//aktuellen Monat als Liquimonat setzten
		txtLiquiDate.setText("01-" + now.substring(3, 5) + "-20" + now.substring(8));

		valuewindow.add(txtValue);
		valuewindow.add(txtLiquiDate);
				
		zeichne_tabelle();
		
		valuewindow.setVisible(true);

	}
   	private void zeichne_tabelle() {

		table = new JTable(new ValuesTableModel(txtValue.getText().replace(".", "").replace(',', '.'),BigOneTools.datum_wandeln(txtLiquiDate.getText(), 0),cn));
		ValuesTableCellRenderer ren = new ValuesTableCellRenderer();
		table.setDefaultRenderer( Object.class, ren );
		table.getColumnModel().getColumn(2).setCellEditor(new DateTableCellEditor());
		table.getColumnModel().getColumn(3).setCellEditor(new DecimalTableCellEditor());
		table.getColumnModel().getColumn(5).setCellEditor(new DateTableCellEditor());
		//table.setDefaultEditor(Object.class, new ValuesTableCellEditor());
				
		//fuer einige spalten feste breiten einrichten
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

		table.addMouseListener( new MouseAdapter(){ 
			public void mouseReleased( MouseEvent me ) { 
				if ( me.getButton() == MouseEvent.BUTTON3 ) 
					popmen.show( me.getComponent(), me.getX(), me.getY() ); 
			} 
		});
		
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(30,70,725,355);
		
		valuewindow.add(sp);
		valuewindow.validate();
		valuewindow.repaint();
	}
}