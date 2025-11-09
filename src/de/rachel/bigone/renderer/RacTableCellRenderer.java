package de.rachel.bigone.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.rachel.bigone.BigOneTools;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.models.RacTableModel;

import java.awt.Color;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RacTableCellRenderer implements TableCellRenderer {
	private Connection cn = null;
	private int accountId = 0;

	public RacTableCellRenderer(Connection LoginCN) {
		this.cn = LoginCN;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		accountId = ((RacTableModel) table.getModel()).getAccountId();

		JLabel label;

		if (!(value instanceof JLabel)) {
			label = new JLabel((String) value);
		} else {
			label = (JLabel) value;
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
				label.setText(BigOneTools.datum_wandeln(value.toString(), 1));
				if (checkValue(row, table)) {
					label.setBackground(Color.RED);
					//label.setForeground(Color.BLACK);
				}
			}
		}

		if (column == 2) {
			label.setHorizontalAlignment(JLabel.RIGHT);
			if (value != null)
				label.setText(value.toString().replace('.', ','));
		}

		if (column == 5) {
			label.setHorizontalAlignment(JLabel.CENTER);
			if (value != null) {// nur wenn es ein liquidatum gibt umwandeln
				label.setText(BigOneTools.datum_wandeln(value.toString(), 1));
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

		// der letzte tag eines Monats ist 30 oder 31 bzw im feb 28 (normales jahr)
		// oder 29 im schaltjahr
		String dateValue = table.getModel().getValueAt(row, 0).toString();
		int iYear = Integer.valueOf(dateValue.substring(0, 4));
		int iMonth = Integer.valueOf(dateValue.substring(5, 7)) - 1;
		int iDay = Integer.valueOf(dateValue.substring(8));

		GregorianCalendar calendar = new GregorianCalendar(iYear, iMonth, iDay);

		DBTools marker = new DBTools(cn);
		marker.select("""
				SELECT COUNT(*)
				FROM transaktionen
				WHERE betrag = %s
				AND konten_id = %d
				AND datum >= '%s'
				AND datum <= '%s'
				""".formatted(table.getModel().getValueAt(row, 2).toString(), accountId, iYear + "-" + (iMonth + 1) + "-1", iYear + "-" + (iMonth + 1) + "-" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH)), 1);

		if (Integer.valueOf(marker.getValueAt(0, 0).toString()) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
