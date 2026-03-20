package de.rachel.bigone.models;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.ValuesTableRow;

public class ValuesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2431676313753205738L;
	private Connection cn = null;
	private String[] columnName = new String[] { "TransID", "s/h", "Datum", "Betrag", "Buchtext", "LiquiMon",
			"Ereigniss" };
	private String accountType;
	private List<ValuesTableRow> importedBookings = new ArrayList<>();

	public ValuesTableModel(Connection LoginCN) {
		cn = LoginCN;
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return importedBookings.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		ValuesTableRow valuesTabelRow = importedBookings.get(row);
		Object returnValue = null;

		switch (col) {
			case 0:
				returnValue = valuesTabelRow.transaktionsId();
				break;
			case 1:
				returnValue = valuesTabelRow.cdtDbtIndicator();
				break;
			case 2:
				returnValue = valuesTabelRow.date();
				break;
			case 3:
				returnValue = valuesTabelRow.amount();
				break;
			case 4:
				returnValue = valuesTabelRow.comment();
				break;
			case 5:
				returnValue = valuesTabelRow.billingMonth();
				break;
			case 6:
				returnValue = valuesTabelRow.bookingEvent();
				break;
			default:
				break;
		}

		return returnValue;
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 2 || col == 3 || col == 4 || col == 5)
			return true;
		else
			return false;
	}

	public void setValueAt(Object value, int row, int col) {
		ValuesTableRow tmpRow;
		Integer strTransID;

		// first del row
		tmpRow = importedBookings.remove(row);

		if (value != null) {
			switch (col) {
				case 2:
					// and then put an new at the same position
					importedBookings.add(row,
							new ValuesTableRow(tmpRow.transaktionsId(), tmpRow.cdtDbtIndicator(),
									LocalDate.parse(value.toString()), tmpRow.amount(), tmpRow.comment(),
									tmpRow.billingMonth(), tmpRow.bookingEvent()));
					break;
				case 3:
					// and then put an new at the same position
					importedBookings.add(row,
							new ValuesTableRow(tmpRow.transaktionsId(), tmpRow.cdtDbtIndicator(), tmpRow.date(),
									Double.valueOf(value.toString()), tmpRow.comment(), tmpRow.billingMonth(),
									tmpRow.bookingEvent()));
					break;
				case 4:
					// and then put an new at the same position
					importedBookings.add(row,
							new ValuesTableRow(tmpRow.transaktionsId(), tmpRow.cdtDbtIndicator(), tmpRow.date(),
									tmpRow.amount(), value.toString(), tmpRow.billingMonth(), tmpRow.bookingEvent()));
					break;
				case 5:
					// and then put an new at the same position
					importedBookings.add(row,
							new ValuesTableRow(tmpRow.transaktionsId(), tmpRow.cdtDbtIndicator(), tmpRow.date(),
									tmpRow.amount(), tmpRow.comment(), LocalDate.parse(value.toString()),
									tmpRow.bookingEvent()));
					break;
				default:
					break;
			}
		}

		strTransID = tmpRow.transaktionsId();

		if (value != null)
			schreibe_neue_daten(row, col, value.toString(), strTransID);
		else
			schreibe_neue_daten(row, col, "NULL", strTransID);

		fireTableCellUpdated(row, col);
	}

	private void lese_werte(String strValue, String strLiquiDate, String sIban) {
		/*
		 * ermittelt die dem Betrag entsprechenden Datensätze
		 * des ausgewählten Kontos und des gesetzten Liquizeitraumes
		 * sortiert nach Datum in absteigender Form
		 * zur Darstellung in der Tabelle
		 * Wird das Liquidatum als leerer String übergeben
		 * wird es nicht berücksichtigt
		 */
		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT t.transaktions_id, t.soll_haben, t.datum, t.betrag, t.buchtext, t.liqui_monat, k.%s ereignis
				FROM transaktionen t, %s k, konten kto
				WHERE t.betrag = %s
				AND k.%s = t.ereigniss_id
				%s
				AND t.konten_id = kto.konten_id
				AND kto.iban = '%s'
				ORDER BY t.datum DESC
				""".formatted((accountType.equals("Haushaltskonto") ? "kategoriebezeichnung" : "ereigniss_krzbez"),
				(accountType.equals("Haushaltskonto") ? "ha_kategorie" : "kontenereignisse"),
				strValue,
				(accountType.equals("Haushaltskonto") ? "ha_kategorie_id" : "ereigniss_id"),
				(strLiquiDate.isEmpty() ? "" : " and t.liqui_monat = '" + strLiquiDate + "'"),
				sIban));

		try {
			getter.beforeFirst();

			while (getter.next()) {
				importedBookings.add(new ValuesTableRow(getter.getInt("transaktions_id"),
						getter.getString("soll_haben"),
						(getter.getDate("datum")).toLocalDate(),
						getter.getDouble("betrag"),
						getter.getString("buchtext"),
						(getter.getDate("liqui_monat")).toLocalDate(),
						getter.getString("ereignis")));
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}
	}

	private void schreibe_neue_daten(int row, int col, String strNewValue, Integer strTransID) {
		// hier werden anhand des alten Wertes und der neuen
		// werte in der Tabelle die sql stings erzeugt und
		// abgesetzt um die daten auch in der db zu aendern

		String strSqlUpdate = null;
		DBTools updater = new DBTools(cn);

		switch(col)	{
		//fuer den betrag
		case 3:
			strSqlUpdate = """
					UPDATE transaktionen SET betrag = %s
					WHERE transaktions_id = %d;
					""".formatted(strNewValue, strTransID);
			break;
		// fuer das wertstellungsdatum
		case 2:
			strSqlUpdate = """
					UPDATE transaktionen SET datum = '%s'
					WHERE transaktions_id = %d;
					""".formatted(strNewValue, strTransID);
			break;
		//fuer den Buchtext
		case 4:
			strSqlUpdate = """
					UPDATE transaktionen SET buchtext = '%s'
					WHERE transaktions_id = %d;
					""".formatted(strNewValue, strTransID);
			break;
		//fuer das liquidatum
		case 5:
			if (strNewValue != "NULL")
				strNewValue = "'" + strNewValue + "'";
			strSqlUpdate = """
					UPDATE transaktionen SET liqui_monat = %s
					WHERE transaktions_id = %d;
					""".formatted(strNewValue, strTransID);
			break;
		}

		if (!updater.update(strSqlUpdate)) {
			System.err.println("Aktualisierung des neuen Wertes: -" + strNewValue + "- für die TransaktionsId: -" + strTransID + "- nicht erfolgreich!");
			System.exit(1);
		}
	}

	public void aktualisiere(String strValue, String strLiquiDate, String sIban, String accountType) {
		this.accountType = accountType;
		importedBookings.clear();
		lese_werte(strValue, strLiquiDate, sIban);
		fireTableDataChanged();
	}
}
