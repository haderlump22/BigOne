package de.rachel.bigone.listeners;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.Expenditure;
import de.rachel.bigone.dialogs.ExpenditureSuccessorDialog;
import de.rachel.bigone.records.ExpenditureSuccessorDistributionTableRow;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class ExpenditureDetailTableMouseListener extends MouseAdapter {
    private JPopupMenu popmen;
    private JMenuItem updateRatioShare, createSuccessor;
    private Expenditure expenditureUi;
    private Connection cn;
    // private SalaryBasesIncomeDetailTableModel model;

    public ExpenditureDetailTableMouseListener(JTable expenditureDetailTable, Expenditure expenditureUi, Connection cn) {
        this.cn = cn;
        this.expenditureUi = expenditureUi;
        popmen = new JPopupMenu();

        createSuccessor = new JMenuItem("nachfolger anlegen");
        createSuccessor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ExpenditureSuccessorDialog dialog = new ExpenditureSuccessorDialog(expenditureUi.getExpenditureJFrame(),
                        cn, expenditureDetailTable);

                createNewSuccessorData(dialog.getNewDescription(), dialog.getNewAmount(), dialog.getNewDivideType(),
                        dialog.getNewValidFrom(), dialog.getNewComment(), dialog.getNewSuccessorDivideTableData(), dialog.getSuccessorToId(), dialog.getFrequency());

                // ((ExpenditureDetailTableModel)expenditureDetailTable.getModel()).aktualisiere();
                expenditureUi.refreshContent();
            }
        });

        updateRatioShare = new JMenuItem("Verhältnisanteile aktualisieren");
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
            JTable expenditureDetailTable = (JTable) me.getSource();
            int RowAtMousePoint = expenditureDetailTable.rowAtPoint(me.getPoint());

            expenditureDetailTable.clearSelection();

            expenditureDetailTable.addRowSelectionInterval(RowAtMousePoint, RowAtMousePoint);

            // if the rightclick came from a expenditure where the divide Type ist not "V"
            // then disable the menu entry "updateRatioShare" and vice versa
            String divideType = (String) expenditureDetailTable.getValueAt(expenditureDetailTable.getSelectedRow(), 2);

            if (divideType.equals("V")) {
                updateRatioShare.setEnabled(true);
                createSuccessor.setEnabled(false);
            } else {
                updateRatioShare.setEnabled(false);
                createSuccessor.setEnabled(true);
            }

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
				""");

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
        DBTools expentitureBackup = new DBTools(cn);
        record ActualRatioExpenditure(Integer expenditureId, String description, Double amount, String divideType,
        LocalDate validFrom, String expenditureHint, Integer frequency) {};
        List<ActualRatioExpenditure> actualRatioExpenditure = new ArrayList<>();

		expentitureBackup.select("""
				SELECT "ausgabenId", bezeichnung, betrag, aufteilungsart, gilt_ab, bemerkung, haeufigkeit
                FROM ha_ausgaben
                WHERE aufteilungsart = 'V'
                AND gilt_bis IS NULL;
				""");

		try {
            expentitureBackup.beforeFirst();

            while (expentitureBackup.next()) {
                actualRatioExpenditure.add(new ActualRatioExpenditure(expentitureBackup.getInt("ausgabenId"),
                        expentitureBackup.getString("bezeichnung"),
                        expentitureBackup.getDouble("betrag"),
                        expentitureBackup.getString("aufteilungsart"),
                        (expentitureBackup.getDate("gilt_ab")).toLocalDate(),
                        expentitureBackup.getString("bemerkung"),
                        expentitureBackup.getInt("haeufigkeit")));
            }
            System.out.println("Sicherheitsausgabe von zu verändernden Datensätzen:");
            System.out.println(actualRatioExpenditure);
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}

        // aktuell gültige Verhältnissbeträge mit einem GiltBis Wert versehen
        DBTools expenditureUpdate = new DBTools(cn);

        if (!expenditureUpdate.update("""
				UPDATE ha_ausgaben
                SET gilt_bis = '%s'
                WHERE aufteilungsart = 'V'
                AND gilt_bis IS NULL;
				""".formatted(newValidFromPeriod.minusMonths(1)))) {
            System.out.println("Fehler beim aktualisieren der alten Ausgabendatensätze vom Typ 'Verhältnis'");
            System.exit(1);
        }

        // neue Datensätze für die Verhältnissbeträge anlegen mit einem GiltAb
        // das der gewünschten Periode entspricht
        DBTools expenditureInsert = new DBTools(cn);
        DBTools expenditureDistributionInsert = new DBTools(cn);

        for (ActualRatioExpenditure actualRatioExpenditureRow : actualRatioExpenditure) {
            int lastInsertId;

            if (!expenditureInsert.insertWithReturn("""
                    INSERT INTO ha_ausgaben
                    (bezeichnung, betrag, aufteilungsart, bemerkung, gilt_ab, haeufigkeit)
                    VALUES
                    ('%s', %s, '%s', '%s', '%s', %d)
                    RETURNING "%s"
                    """.formatted(actualRatioExpenditureRow.description,
                    actualRatioExpenditureRow.amount,
                    actualRatioExpenditureRow.divideType,
                    actualRatioExpenditureRow.expenditureHint,
                    newValidFromPeriod.toString(),
                    actualRatioExpenditureRow.frequency,
                    "ausgabenId"))) {
                System.out.println("Fehler beim einfügen des neuen Ausgabendatenstz vom Typ 'Verhältnis' mit der Bezeichnung:" + actualRatioExpenditureRow.description);
                System.exit(1);
            } else {
                try {
                    // für diese Datensätze neue Aufteilungen anhand der aktuell ermittelten
                    // Prozentanteile erstellen
                    for (PercentOfEachParty percentOfParty : partyPercents) {
                        expenditureInsert.first();
                        lastInsertId = expenditureInsert.getInt("ausgabenId");

                        if (!expenditureDistributionInsert.insert("""
                                INSERT INTO ha_ausgaben_aufteilung
                                ("parteiId", betrag, bemerkung, "ausgabenId")
                                VALUES
                                (%d, %s, '%s', %d)
                                """.formatted(percentOfParty.partyId,
                                (actualRatioExpenditureRow.amount * percentOfParty.percent),
                                "Verhältinisanteils Aktualisierung",
                                lastInsertId))) {
                            System.out.println("Fehler beim einfügen des neuen AusgabenAufteilungsdatensatz für Partei: -" + percentOfParty.partyId + "- der Ausgabe: " + actualRatioExpenditureRow.description);
                            System.exit(1);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // refresh the detailTable
        expenditureUi.refreshContent();
    }

    private void createNewSuccessorData(String newExpenditureDescription, Double newExpenditureAmount,
            String newExpenditureDivideType, LocalDate newExpenditureValidFrom, String newExpenditureComment,
            ArrayList<ExpenditureSuccessorDistributionTableRow> newExpenditureDivideData, Integer successorToId, Integer frequency) {

        DBTools dbManipulate = new DBTools(cn);

        // first update the validTo Value of the old expenditure
        if (!dbManipulate.update("""
                UPDATE ha_ausgaben
                SET gilt_bis = '%s'
                WHERE "ausgabenId" = %d
                """.formatted(newExpenditureValidFrom.minusMonths(1), successorToId))) {
            System.out.println("Fehler beim aktualisieren der alten Ausgabendatensätze vom Typ 'Verhältnis'");
            System.exit(1);
        };

        // // then ad a new Record in Table expenditure and keep the new ID in mind
        int lastInsertId;

        if (!dbManipulate.insertWithReturn("""
                INSERT INTO ha_ausgaben
                (bezeichnung, betrag, aufteilungsart, bemerkung, gilt_ab, haeufigkeit)
                VALUES
                ('%s', %s, '%s', '%s', '%s', %d)
                RETURNING "%s"
                """.formatted(newExpenditureDescription,
                newExpenditureAmount,
                newExpenditureDivideType,
                newExpenditureComment,
                newExpenditureValidFrom.toString(),
                frequency,
                "ausgabenId"))) {
            System.out.println("Fehler beim einfügen des neuen Ausgabendatensatz vom Typ '" + newExpenditureDivideType + "' mit der Bezeichnung:" + newExpenditureDescription);
            System.exit(1);
        } else {
            try {
                dbManipulate.first();
                lastInsertId = dbManipulate.getInt("ausgabenId");

                // and add the new expenditure distribuition Values of the new expenditure if
                // insert the expenditur
                for (ExpenditureSuccessorDistributionTableRow expenditureDivideOfParty : newExpenditureDivideData) {
                    if (!dbManipulate.insert("""
                            INSERT INTO ha_ausgaben_aufteilung
                            ("parteiId", betrag, bemerkung, "ausgabenId")
                            VALUES
                            (%d, %s, '%s', %d)
                            """.formatted(expenditureDivideOfParty.partyId(),
                            expenditureDivideOfParty.amount(),
                            expenditureDivideOfParty.comment(),
                            lastInsertId))) {
                        System.out.println("Fehler beim einfügen des neuen AusgabenAufteilungsdatensatz für Partei: -" + expenditureDivideOfParty.partyId() + "- der Ausgabe: " + newExpenditureDescription);
                        System.exit(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
