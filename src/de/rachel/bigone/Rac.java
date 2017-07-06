package de.rachel.bigone;

//import java.awt.Font;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.rachel.bigone.Editors.ComboTableCellEditor;
import de.rachel.bigone.Editors.LiquiDateTableCellEditor;
import de.rachel.bigone.Models.RACTableModel;
import de.rachel.bigone.Renderer.RACTableCellRenderer;


public class Rac {
	private Connection cn = null;
	private static final int TANKEN = 6;
	private static final int AUFTEILUNG = 52;
	//private static final int KONTOID = 7;
	private JFrame RACWindow;
	//private Font fontTxtFields;
	private JTable table;
	private JPopupMenu popmen;
	private RACTableModel model;
	private JButton btnOpen;
	private JButton btnImp;
	private JFileChooser open;
	private JScrollPane sp;

	Rac(Connection LoginCN){
		cn = LoginCN;
		RACWindow = new JFrame("Kontoauszug einlesen");
		RACWindow.setSize(785,480);
		RACWindow.setLocation(200,200);
		RACWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		RACWindow.setLayout(null);
		RACWindow.setResizable(false);
		
		//schriftenfestlegungen
		//fontTxtFields = new Font("Arial",Font.PLAIN,16);
		
		open = new JFileChooser();
		
		popmen = new JPopupMenu(); 
		JMenuItem delrow = new JMenuItem("Zeile löschen");
		delrow.addActionListener(new ActionListener(){ 
	        public void actionPerformed(ActionEvent ae){ 
	        	model = (RACTableModel)table.getModel();
				model.removeRow(table.getSelectedRow());
	        }
	    });
		popmen.add(delrow); 
		
		btnOpen = new JButton("Auszug öffnen");
		btnOpen.setBounds(30,17,115,35);
		btnOpen.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent ae){
            	int iZeilen;
            	
        		open.showOpenDialog(RACWindow);
        		iZeilen = new BigOneTools().ermittle_anzahl_zeilen(open.getSelectedFile().toString());
        		if(iZeilen >= 10) {
        			if(table == null) {
        				zeichne_tabelle(open.getSelectedFile().toString());
        			} else {
            			model = (RACTableModel)table.getModel();
            			model.aktualisiere(open.getSelectedFile().toString(),iZeilen);	
        			}
        			//nach dem erfolgreichen einlesen der zu importierenden daten
        			//den Button aktivieren
        			btnImp.setEnabled(true);
        		} else {
        			System.out.println("Zu wenige Zeilen in der einzulesenden Datei!");
        			RACWindow.remove(sp);
        			RACWindow.validate();
        			RACWindow.repaint();
        		}
            }
        });
		
		
		btnImp = new JButton("importieren");
		btnImp.setBounds(150,17,115,35);
		btnImp.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent ae){
            	//Tabelle zeilenweise durchlaufen und 
            	//daten in die DB einfuegen
            	String sql="";
            	boolean insert_details_success = true;
            	boolean insert_tankdaten_success = true;
            	DBTools pusher = new DBTools(cn);
            	DBTools getter = new DBTools(cn);
            	model = (RACTableModel)table.getModel();
            	
            	//tabellendaten von der letzten Zeile zur ersten hin importieren
            	//damit die altesten buchungen als erste in der Tabelle geschrieben
            	//werden
            	for(int i = model.getRowCount() -1 ; i >= 0; i--) {
            		insert_details_success = true;
            		
            		sql = "INSERT into transaktionen " +
            			"(soll_haben, konten_id, datum, betrag, buchtext, ereigniss_id, liqui_monat) " +
 	      			   	"VALUES " +
 	      			   	"('" + model.getValueAt(i, 1) + "', " +
 	      			   	"'12', '" +
 	      			   	model.getValueAt(i, 0) + "'," +
 	      			   	model.getValueAt(i, 2) + ",'" +
 	      			   	model.getValueAt(i, 3).toString().replace("'", "''") + "'," +
 	      			   	BigOneTools.extractEreigId(model.getValueAt(i, 6).toString()) + ",'" +
 	      			   	model.getValueAt(i, 5) + "');";
            		
            		//neuen datensatz einfuegen und den erfolg pruefen
            		//falls Datensatz nicht eingefuegt werden konnt
            		//mit dem naechsten weiter machen und nicht abbrechen
            		if(pusher.insert(sql) == false) {
            			System.out.println("Fehler beim Import des Datensatzes Nr: " + i);
            			continue;
            		}
            		//die Ereignissid pruefen und eventuelle Tankdaten oder
            		//aufteilungen in die DB Tabellen eintragen
            		switch(BigOneTools.extractEreigId(model.getValueAt(i, 6).toString())) {
            		case AUFTEILUNG:
            			//Aufteilungen zur Eintragung aufnehmen
                        //das Programm arbeitet weiter wenn
                        //dialog geschlossen wird
                        Aufteilung aufteil = new Aufteilung(RACWindow, Double.valueOf(model.getValueAt(i, 2).toString()).doubleValue(), cn);
                        
                        //da gerade der letzte Datensatz in die tabelle transaktionen eingetragen
                        //wurde kann man auch schon dessen ID feststellen
                        getter.select("SELECT max(transaktions_id) from transaktionen;", 1);
                        
                        String[][] datenAuft = aufteil.getDaten();
                        
						for(String[] arg : datenAuft) {
						    String sql_auft = "INSERT INTO aufteilung " +
						  			   "( transaktions_id, betrag, ereigniss_id, liqui) " +
						  			   "VALUES " +
						  			   "(" + getter.getValueAt(0, 0) + ", " +
						  			   arg[1] + ", " +
						  			   arg[0] + ", " +
						  			   arg[2] + ");";
						    //System.out.println(sql_auft);  
						    if(pusher.insert(sql_auft) == false) {
						    	System.out.println("Fehler beim Einfuegen der Detaildatensaetze zu Datensatz Nr: " + i);
						    	insert_details_success = false;
						    	continue; 	// mit dem naechsten Datensatz beim einfuegen weitermachen
						    }
						}
            			break;
            		case TANKEN:
            			//tankwerte zur eintragung aufnehmen
                        //das Programm arbeitet weiter wenn
                        //dialog geschlossen wird
                        TankDialog td = new TankDialog(RACWindow,model.getValueAt(i, 2).toString(),cn);
                        getter.select("SELECT max(transaktions_id) from transaktionen;", 1);
                        
                        String sql_tanken = "INSERT INTO tankdaten " +
 	      			   				"( transaktions_id, liter, km, kraftstoff_id, datum_bar, betrag_bar, kfz_id) " +
 	      			   				"VALUES " +
 	      			   				"(" + getter.getValueAt(0, 0) + ", " +
 	      			   				td.get_liter() + ", " +
 	      			   				td.get_km() + ", " +
 	      			   				td.get_treibstoff_id() + ", " +
 	      			   				"NULL, " +
 	      			   				"NULL, " +
 	      			   				td.get_kfz_id() + ");";
                        
                        //neuen datensatz einfuegen und den erfolg pruefen
                		if(pusher.insert(sql_tanken) == false) {
                			System.out.println("Fehler beim Einfuegen der Tankdaten zu Datensatz Nr: " + i);
                			insert_tankdaten_success = false;
                			break;
                		}
            		}
            		
            		if(insert_details_success == false || insert_tankdaten_success == false) {
            			continue; 	//ist ein fehler aufgetreten dann mit dem naechsten datensatz
            			 		//beim einfuegen weitermachen
            		}
            	}
            //nach erfolgreicher importierung den Improtbutton deaktivieren
            btnImp.setEnabled(false);
            }
        });

		RACWindow.add(btnOpen);
		RACWindow.add(btnImp);
		RACWindow.validate();
		RACWindow.repaint();
		
		open.showOpenDialog(RACWindow);
		
		BigOneTools tool = new BigOneTools();
		if(tool.ermittle_anzahl_zeilen(open.getSelectedFile().toString()) >= 10) {
			zeichne_tabelle(open.getSelectedFile().toString());
		} else {
			System.out.println("Zu wenige Zeilen in der einzulesenden Datei!");
		}
		RACWindow.setVisible(true);
	}
	private void zeichne_tabelle(String strFile) {
		
		BigOneTools tool = new BigOneTools();
		
		table = new JTable(new RACTableModel(strFile,tool.ermittle_anzahl_zeilen(open.getSelectedFile().toString())));
		RACTableCellRenderer ren  = new RACTableCellRenderer(cn);
		table.setDefaultRenderer( Object.class, ren );
		table.getColumnModel().getColumn(5).setCellEditor(new LiquiDateTableCellEditor());
		table.getColumnModel().getColumn(6).setCellEditor(new ComboTableCellEditor(cn));

		//fuer einige spalten feste breiten einrichten
		table.getColumnModel().getColumn(0).setMinWidth(78);
		table.getColumnModel().getColumn(0).setMaxWidth(78);
		table.getColumnModel().getColumn(1).setMinWidth(25);
		table.getColumnModel().getColumn(1).setMaxWidth(25);
		table.getColumnModel().getColumn(2).setMinWidth(55);
		table.getColumnModel().getColumn(2).setMaxWidth(55);
		table.getColumnModel().getColumn(4).setMinWidth(68);
		table.getColumnModel().getColumn(4).setMaxWidth(68);
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
		
		sp = new JScrollPane(table);
		sp.setBounds(30,70,725,355);
		
		RACWindow.add(sp);
		RACWindow.validate();
		RACWindow.repaint();
	}
}
