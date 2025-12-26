package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.ExpenditureSuccessor;
import de.rachel.bigone.records.SalaryBasesSumOfIncomePerPartyTableRow;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

// import de.rachel.bigone.Models.SalaryBasesIncomeDetailTableModel;

public class ExpenditureDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JFrame expenditureUi;
    private Connection cn;
    // private SalaryBasesIncomeDetailTableModel model;

    public ExpenditureDetailTableMouseListener(JTable ExpenditureDetailTable, JFrame expenditureUi, Connection cn) {
        this.cn = cn;
        this.expenditureUi = expenditureUi;
        popmen = new JPopupMenu();

        JMenuItem createSuccessor = new JMenuItem("nachfolger anlegen");
        createSuccessor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // JOptionPane.showMessageDialog(null, "ALERT MESSAGE", "TITLE", JOptionPane.WARNING_MESSAGE); ratio share
                new ExpenditureSuccessor(expenditureUi, cn);
            }
        });

        JMenuItem updateRatioShare = new JMenuItem("Verhältnisanteile aktualisieren");
        updateRatioShare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                createNewRatioShareData();
            }
        });

        popmen.add(createSuccessor);
        popmen.add(updateRatioShare);
    }

    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            // wenn die Zeile auf der der Rechtsklick ausgefürht wurde nicht selectiert war
            // wird diese Zeile erst selectiert
            JTable ExpenditureDetailTable = (JTable) me.getSource();
            int RowAtMousePoint = ExpenditureDetailTable.rowAtPoint(me.getPoint());

            // vorherige Selection aufheben
            ExpenditureDetailTable.clearSelection();

            // diese eine Zeile selectieren
            ExpenditureDetailTable.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // popup zum Löschen der selectierten Zeile anzeigen
            popmen.show(me.getComponent(), me.getX(), me.getY());
        }
    }

    private void createNewRatioShareData() {
        // gewünschte Periode ab der die neuen Daten gelten sollen ermitteln
        LocalDate acutalMonth = LocalDate.now();
        LocalDate firstOfActualMonth = acutalMonth.minusDays(acutalMonth.getDayOfMonth() - 1);
        LocalDate newValidFromPeriod = LocalDate.parse(JOptionPane.showInputDialog("Bitte Periode angeben ab der die Werte gültig sind:", firstOfActualMonth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        // aktuell Prozentanteile der Parteien ermitteln
		DBTools percentStatement = new DBTools(cn);
        Double sumOfAllIncome = 0.0;
        record PercentOfEachParty(Integer partyId, double percent) {};
        List<PercentOfEachParty> partyPercents = new ArrayList<>();

		percentStatement.select("""
				SELECT partei_id, sum(betrag) as betrag
				FROM ha_gehaltsgrundlagen gg
				WHERE gilt_bis IS NULL
				GROUP BY partei_id
				ORDER BY partei_id;
				""",
				2);

		try {
            // first get the Sum of all Incomes
			percentStatement.beforeFirst();

			while (percentStatement.next()) {
				sumOfAllIncome = sumOfAllIncome + percentStatement.getDouble("betrag");
			}

            // then we calculate the actual percent of each Party
            percentStatement.beforeFirst();

			while (percentStatement.next()) {
				partyPercents.add(new PercentOfEachParty(percentStatement.getInt("partei_id"), percentStatement.getDouble("betrag") / sumOfAllIncome));
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}

        // die aktuellen Ausgaben die im Modus "Verhältnis" aufgeteilt werden
        // merken
        DBTools expentitureStatement = new DBTools(cn);
        record ActualRatioExpenditure(Integer expenditureId, String description, Double amount, String divideType,
        LocalDate validFrom, String expenditureHint, Integer frequency) {};
        List<ActualRatioExpenditure> actualRatioExpenditure = new ArrayList<>();

		expentitureStatement.select("""
				SELECT "ausgabenId", bezeichnung, betrag, aufteilungsart, gilt_ab, bemerkung, haeufigkeit
                FROM ha_ausgaben
                WHERE aufteilungsart = 'V'
                AND gilt_bis IS NULL;
				""",
				2);

		try {
            expentitureStatement.beforeFirst();

            while (expentitureStatement.next()) {
                actualRatioExpenditure.add(new ActualRatioExpenditure(expentitureStatement.getInt("ausgabenId"),
                        expentitureStatement.getString("bezeichnung"),
                        expentitureStatement.getDouble("betrag"),
                        expentitureStatement.getString("aufteilungsart"),
                        (expentitureStatement.getDate("gilt_ab")).toLocalDate(),
                        expentitureStatement.getString("bemerkung"),
                        expentitureStatement.getInt("haeufigkeit")));
            }
            System.out.println("Sicherheitsausgabe von zu verändernden Datensätzen:");
            System.out.println(actualRatioExpenditure);
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}

        // aktuell gültige Verhältnissbeträge mit einem GiltBis Wert versehen

        // neue Datensätze für die Verhältnissbeträge anlegen mit einem GiltAb
        // das der gewünschten Periode entspricht

        // für diese Datensätze neue Aufteilungen anhand der aktuell ermittelten
        // Prozentanteile erstellen
    }
}
