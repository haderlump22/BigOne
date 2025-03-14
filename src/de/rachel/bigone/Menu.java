package de.rachel.bigone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu {
	private JFrame menuwindow = null;
	private JMenuBar mbar = null;
	private Login login = null;
	private Connection cn = null;

	Menu(){
	//show Login
	login = new Login(menuwindow);

	//kill aplication by 3 loginerrors or however getConnection brings null
	if(login.getLogincount() == 3 || login.getConnection() == null)
		System.exit(0);

	cn = login.getConnection();

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
            new Transaktionen(cn);
        }
    });
	prog.add(trans);

	JMenu quer = new JMenu("Abfragen");
	JMenuItem liqui = new JMenuItem("Liqui");
	liqui.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new Liqui(cn);
        }
    });
	quer.add(liqui);

	JMenuItem values = new JMenuItem("Beträge finden");
	values.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new Values(cn);
        }
    });
	quer.add(values);

	JMenuItem kdab = new JMenuItem("Kontostand");
	kdab.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new KeyDateAccountBalance(cn);
        }
    });
	quer.add(kdab);

	JMenuItem rac = new JMenuItem("Import");
	rac.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new Rac(cn);
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

	// Haushaltskonto Bereich
	JMenu Haushaltskonto = new JMenu("Haushaltskonto");

	// Untermenüs Haushaltskonto
	JMenuItem Gehaltsgrundlagen = new JMenuItem("Gehaltsgrundlagen");
	JMenuItem Ausgaben = new JMenuItem("Ausgaben");
	JMenuItem Ueberweisungsbetrag = new JMenuItem("Überweisungsbetrag");

	// Listener für die Untermenüns
	Gehaltsgrundlagen.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new SalaryBases(cn);
        }
    });

	Ausgaben.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new Expenditure(cn);
        }
    });

	Ueberweisungsbetrag.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            new TransferAmount(cn);
        }
    });

	// Untermenüns an Menüpunkt heften
	Haushaltskonto.add(Gehaltsgrundlagen);
	Haushaltskonto.add(Ausgaben);
	Haushaltskonto.add(Ueberweisungsbetrag);

	//Einzellmenues in die menuebar einbetten
	mbar.add(prog);
	mbar.add(quer);
	mbar.add(tables);
	mbar.add(Haushaltskonto);

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
