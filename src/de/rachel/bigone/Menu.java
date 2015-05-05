package de.rachel.bigone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu {
	private JFrame menuwindow;
	private JMenuBar mbar;
	private DatabaseConstants DBC;

	Menu(){
	DBC = new DatabaseConstants();
	
	menuwindow = new JFrame("BigOne");
	menuwindow.setLocation(100,100);
	menuwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	menuwindow.setResizable(false);
	
	mbar = new JMenuBar();
	//Einzelmenues mit Ihren unterpunkten erstellen
	JMenu prog = new JMenu("Programme");
	JMenuItem trans = new JMenuItem("Transaktionen");
	trans.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            new Transaktionen();
        }
    });
	prog.add(trans);

	JMenu quer = new JMenu("Abfragen");
	JMenuItem liqui = new JMenuItem("Liqui");
	liqui.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            new Liqui(DBC);
        }
    });
	quer.add(liqui);
	
	JMenuItem values = new JMenuItem("Beträge finden");
	values.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            new Values();
        }
    });
	quer.add(values);
	
	JMenuItem kdab = new JMenuItem("Kontostand");
	kdab.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            new KeyDateAccountBalance();
        }
    });
	quer.add(kdab);
	
	JMenuItem rac = new JMenuItem("Import");
	rac.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            new Rac();
        }
    });
	quer.add(rac);
	
	JMenu tables = new JMenu("Tabellen");
	
	JMenuItem monat = new JMenuItem("Monatsausgaben");
	monat.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            //new Rac();
        }
    });
	tables.add(monat);
	
	JMenuItem jahres = new JMenuItem("Jahresausgaben");
	jahres.addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent ae){ 
            //new Rac();
        }
    });
	tables.add(jahres);
	
	//Einzellmenues in die menuebar einbetten
	mbar.add(prog);
	mbar.add(quer);
	mbar.add(tables);
	
	//menue an das fenster haengen
	menuwindow.setJMenuBar(mbar);
	
	menuwindow.pack();
	menuwindow.setVisible(true);
	
	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Menu();

	}

}
