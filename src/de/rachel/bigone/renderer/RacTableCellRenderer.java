package de.rachel.bigone.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.RacTableModel;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RacTableCellRenderer implements TableCellRenderer {
	private Connection cn = null;
	private int accountId = 0;

	public RacTableCellRenderer(Connection LoginCN) {
		this.cn = LoginCN;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		accountId = ((RacTableModel) table.getModel()).getAccountId();

		JLabel label;

		if (!(value instanceof JLabel) && value != null) {
			label = new JLabel(value.toString());
		} else {
			if (value != null) {
				label = (JLabel) value;
			} else {
				label = new JLabel("");
			}
		}

		label.setOpaque(true);
		label.setFont(table.getFont());
		label.setForeground(table.getForeground());
		label.setBackground(table.getBackground());

		if (isSelected) {
			label.setBackground(table.getSelectionBackground());
			label.setForeground(table.getSelectionForeground());
		}

		if (column == 0) {
			label.setHorizontalAlignment(JLabel.CENTER);
			if (value != null) {
				label.setText(((LocalDate)value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
				if (checkValue(row, table)) {
					label.setBackground(Color.RED);
					//label.setForeground(Color.BLACK);
				}
			}
		}

		if (column == 2) {
			label.setHorizontalAlignment(JLabel.RIGHT);
			if (value != null)
				label.setText("%.02f".formatted((Double) value));
		}

		if (column == 5) {
			label.setHorizontalAlignment(JLabel.CENTER);
			if (value != null) {// nur wenn es ein liquidatum gibt umwandeln
				label.setText(((LocalDate)value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			}
		}

		return label;
	}

	public boolean checkValue(int row, JTable table) {
		// versucht anhand der Datensaetze der Importdatei
		// aenliche in der Datenbank zu finden und rot zu markieren
		// dabei wird auf die Menge der Datensaetze mit dem selben
		// Betrag innerhalb eines Monats (01 bis 31) fuer das konto geprueft
		// es wird hier aus performancegrunden nur eine zelle
		// eingefaerbt anstatt die ganze zeile
		// fuer die ganze zeile muesste diese funktion auch
		// fuer jede Zelle aufgerufen werden und das dauert
		boolean checkValue = false;
		LocalDate dateValue = (LocalDate) table.getModel().getValueAt(row, 0);

		DBTools marker = new DBTools(cn);

		marker.select("""
				SELECT COUNT(*)
				FROM transaktionen
				WHERE betrag = %s
				AND konten_id = %d
				AND datum >= '%s'
				AND datum <= '%s'
				""".formatted(table.getModel().getValueAt(row, 2).toString(), accountId, dateValue.minusDays(dateValue.getDayOfMonth() - 1).toString(), dateValue.plusDays(dateValue.lengthOfMonth() - dateValue.getDayOfMonth()).toString()), 1);

		try {
			if (marker.getInt("count") > 0) {
				checkValue = true;
			} else {
				checkValue = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return checkValue;
	}
}
