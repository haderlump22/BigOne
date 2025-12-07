package de.rachel.bigone.models;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.rachel.bigone.DBTools;
import de.rachel.bigone.ReadCamt;
import de.rachel.bigone.records.RacTableRow;

public class RacTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -2431676313753205738L;
	private Connection cn = null;
	private String[] columnName = new String[]{"Wertstellung","s/h","Betrag","Buchungshinweis","DBIT/CRDT","LiquiMon","Ereignis"};
	private static int LiquiMonth = 5;
	private ReadCamt Auszug = null;
	private ArrayList<String> componentList = new ArrayList<String>();
	private List<RacTableRow> buchungen = new ArrayList<>();
	SimpleDateFormat SQLDATE = new SimpleDateFormat("yyyy-MM-dd");

 	public RacTableModel(Connection LoginCN){
		this.cn = LoginCN;
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return buchungen.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		RacTableRow racTableRow = buchungen.get(row);
		Object ReturnValue = null;

		switch (col) {
			case 0:
				ReturnValue = racTableRow.valueDate();
				break;
			case 1:
				ReturnValue = racTableRow.cdtDbtInd();
				break;
			case 2:
				ReturnValue = racTableRow.amount();
				break;
			case 3:
				ReturnValue = racTableRow.comment() + " (" + racTableRow.cdtDbtName() + ")";
				break;
			case 4:
				ReturnValue = racTableRow.cdtDbtName();
				break;
			case 5:
				ReturnValue = racTableRow.billingMonth();
				break;
			case 6:
				ReturnValue = racTableRow.bookingEvent();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public boolean isCellEditable(int row, int col){
		if ( col == 3 || col == 5 || col == 6)
			return true;
		else
			return false;
	}

	public void setValueAt(Object value, int row, int col) {
		RacTableRow tmpRow;

		if(value != null) {
			switch (col) {
				case 3:
					// first del row
					tmpRow = buchungen.remove(row);
					// and then put an new at the same position
					buchungen.add(row, new RacTableRow(tmpRow.valueDate(), tmpRow.cdtDbtInd(), tmpRow.cdtDbtName(), tmpRow.amount(), value.toString(), tmpRow.billingMonth(), tmpRow.bookingEvent()));
					break;
				case 5:
					// first del row
					tmpRow = buchungen.remove(row);
					// and then put an new at the same position
					buchungen.add(row, new RacTableRow(tmpRow.valueDate(), tmpRow.cdtDbtInd(), tmpRow.cdtDbtName(), tmpRow.amount(), tmpRow.comment(), LocalDate.parse(value.toString()), tmpRow.bookingEvent()));
					break;
				case 6:
					// first del row
					tmpRow = buchungen.remove(row);
					// and then put an new at the same position
					buchungen.add(row, new RacTableRow(tmpRow.valueDate(), tmpRow.cdtDbtInd(), tmpRow.cdtDbtName(), tmpRow.amount(), tmpRow.comment(), tmpRow.billingMonth(), value.toString()));
					break;
				default:
					break;
			}
		}
        fireTableCellUpdated(row, col);
    }

	public void setLiquiToNull(int iZeile) {
		RacTableRow tmpRow;

		// mit dieser Methode kann der Wert des Liquidatums der aktuellen Zeile auf NULL gesetzt werden
		// weil der Editor ja jetzt das Format vorgibt, und so das Liquidatum NICHT mehr durch manuelle
		// Eingabe auf NULL gesetzt werden kann
		if(iZeile < buchungen.size()) {
			tmpRow = buchungen.remove(iZeile);

			buchungen.add(iZeile, new RacTableRow(tmpRow.valueDate(), tmpRow.cdtDbtInd(), tmpRow.cdtDbtName(), tmpRow.amount(), tmpRow.comment(), null, tmpRow.bookingEvent()));
			fireTableCellUpdated(iZeile, LiquiMonth);

		}
	}

	public void setAllLiquiToNull() {
		RacTableRow tmpRow;
		// setzt alle Liqudatumswerte der Tabelle auf NULL
		for (int i = 0; i < buchungen.size(); i++) {
			tmpRow = buchungen.remove(i);

			buchungen.add(i, new RacTableRow(tmpRow.valueDate(), tmpRow.cdtDbtInd(), tmpRow.cdtDbtName(), tmpRow.amount(), tmpRow.comment(), null, tmpRow.bookingEvent()));
		}
		// änderungen in der Darstellung aktualisieren#
		fireTableDataChanged();
	}

	public void aktualisiere(ReadCamt Auszug) {
		this.Auszug = Auszug;

		// we make a copy of the List, so the original from the "Auszug"
		// is not effected when we reduce the showing Rows with an
		// Date Range
		this.buchungen.addAll(this.Auszug.getBuchungen());

		// sorting the intern ArrayList from new to old
		this.buchungen.sort(new Comparator<RacTableRow>() {
			@Override
			public int compare(RacTableRow rowOne, RacTableRow rowNext) {
				if (rowOne.valueDate().isAfter(rowNext.valueDate())) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		// if we know the Auszug we can decide where to get the category
		updateEntrysForCategory(this.Auszug.isJointAccount());

		fireTableDataChanged();
	}

	public void removeUnusedRows(LocalDate from, LocalDate to) {
		// remove all Rows that are not in the Timerange

		// check rows
		for (int iRow = 0; iRow < buchungen.size(); iRow++) {
			// if is not in the timerange, remove it
			if (buchungen.get(iRow).valueDate().isBefore(from) || buchungen.get(iRow).valueDate().isAfter(to)) {
				buchungen.remove(iRow);

				// because we delete one Row in the array and the row after move to the actual index, wie must go on on the same index,
				iRow--;
			}
		}

		//tabellendarstellung aktualisieren
		fireTableDataChanged();
	}

	private void updateEntrysForCategory(boolean isJointAccount) {
		DBTools getter = new DBTools(cn);

		// first clear the old Content
		componentList.clear();

		// if the iban is from an jointAccount the Accountevents will be get from another table
		if (isJointAccount) {
			getter.select("SELECT ha_kategorie_id, kategoriebezeichnung FROM ha_kategorie ORDER BY 2",2);
		} else {
			getter.select("SELECT ereigniss_id, ereigniss_krzbez FROM kontenereignisse WHERE gueltig = 'TRUE' ORDER BY 2",2);
		}

		try {
			getter.beforeFirst();

			while (getter.next()) {
				componentList.add(getter.getString(isJointAccount ? "kategoriebezeichnung" : "ereigniss_krzbez") + " (" + getter.getString(isJointAccount ? "ha_kategorie_id" : "ereigniss_id")+")");
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}
	}

	public Object[] getComponent() {
		return componentList.toArray();
	}

	public int getAccountId() {
		return Auszug.getAccountId();
	}

	public boolean isJointAccount() {
		return Auszug.isJointAccount();
	}
}
