package de.rachel.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    private JFrame menuwindow = null;
    private JMenuBar mbar = null;
    private LoginOld login = null;
    private Connection cn = null;

    private void initSwingGui() {
        // kill aplication by 3 loginerrors or however getConnection brings null
        if (login.getLogincount() == 3 || login.getConnection() == null)
            System.exit(0);

        cn = login.getConnection();

        menuwindow = new JFrame("BigOne");
        menuwindow.setLocation(100, 100);
        menuwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuwindow.setResizable(false);

        mbar = new JMenuBar();
        // Einzelmenues mit Ihren unterpunkten erstellen
        JMenu prog = new JMenu("Programme");
        JMenuItem trans = new JMenuItem("Transaktionen");
        trans.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Transaktionen(cn);
            }
        });
        prog.add(trans);

        JMenu Accounts = new JMenu("Konten");
        JMenuItem Abschluss = new JMenuItem("Abschluss");
        Abschluss.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new AccountClosing(cn);
            }
        });
        Accounts.add(Abschluss);

        JMenuItem values = new JMenuItem("Beträge finden");
        values.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Values(cn);
            }
        });
        Accounts.add(values);

        JMenuItem kdab = new JMenuItem("Kontostand");
        kdab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new KeyDateAccountBalance(cn);
            }
        });
        Accounts.add(kdab);

        JMenuItem rac = new JMenuItem("Import");
        rac.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Rac(cn);
            }
        });
        Accounts.add(rac);

        JMenu tables = new JMenu("Tabellen");

        JMenuItem monat = new JMenuItem("Monatsausgaben");
        monat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // new Rac();
            }
        });
        tables.add(monat);

        JMenuItem jahres = new JMenuItem("Jahresausgaben");
        jahres.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // new Rac();
            }
        });
        tables.add(jahres);

        // Haushaltskonto Bereich
        JMenu Haushaltskonto = new JMenu("Haushaltskonto");

        // Untermenüs Haushaltskonto
        JMenuItem Gehaltsgrundlagen = new JMenuItem("Gehaltsgrundlagen");
        JMenuItem Ausgaben = new JMenuItem("Ausgaben");
        JMenuItem Ueberweisungsbetrag = new JMenuItem("Überweisungsbetrag");
        JMenuItem Haushaltsabschluss = new JMenuItem("Haushaltsabschluss");

        // Listener für die Untermenüns
        Gehaltsgrundlagen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new SalaryBases(cn);
            }
        });

        Ausgaben.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new Expenditure(cn);
            }
        });

        Ueberweisungsbetrag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new TransferAmount(cn);
            }
        });

        Haushaltsabschluss.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new JointAccountClosing(cn);
            }
        });

        // Untermenüns an Menüpunkt heften
        Haushaltskonto.add(Gehaltsgrundlagen);
        Haushaltskonto.add(Ausgaben);
        Haushaltskonto.add(Ueberweisungsbetrag);
        Haushaltskonto.add(Haushaltsabschluss);

        // Einzellmenues in die menuebar einbetten
        mbar.add(prog);
        mbar.add(Accounts);
        mbar.add(tables);
        mbar.add(Haushaltskonto);

        // menue an das fenster haengen
        menuwindow.setJMenuBar(mbar);

        menuwindow.pack();
        menuwindow.setVisible(true);
    }

    @Override
    public void start(Stage loginStage) throws IOException{
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));

      Scene scene = new Scene(fxmlLoader.load());

      loginStage.setResizable(false);
      loginStage.setTitle("Login to BigOne");
      loginStage.setScene(scene);
      loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
