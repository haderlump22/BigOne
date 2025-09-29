package de.rachel.bigone.models;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;
import de.rachel.bigone.DBTools;
import de.rachel.bigone.records.JointAccountClosingDetailTableRow;

public class JointAccountClosingDetailTableModel extends AbstractTableModel {
	private Connection cn = null;
	private String[] columnName = new String[] { "Ausgabenart", "Betrag IST", "Betrag PLAN", "Differenz" };
	private List<JointAccountClosingDetailTableRow> tableData = new ArrayList<>();
	private String billingMonth = null;
	private String[] detailIdsForMarkingDifferenceValue = new String[0];

	public JointAccountClosingDetailTableModel(Connection LoginCN) {
		cn = LoginCN;
		lese_werte();
	}

	public int getColumnCount() {
		return columnName.length;
	}

	public int getRowCount() {
		return tableData.size();
	}

	public String getColumnName(int col) {
		return columnName[col];
	}

	public Object getValueAt(int row, int col) {
		JointAccountClosingDetailTableRow accountClosingDetailTableModelRow = tableData.get(row);
		Object ReturnValue = null;

		switch (col) {
			case -1:
				ReturnValue = accountClosingDetailTableModelRow.closingDetailId();
				break;
			case 0:
				ReturnValue = accountClosingDetailTableModelRow.nameOfExpenditure();
				break;
			case 1:
				ReturnValue = accountClosingDetailTableModelRow.actualAmount();
				break;
			case 2:
				ReturnValue = accountClosingDetailTableModelRow.planAmount();
				break;
			case 3:
				ReturnValue = accountClosingDetailTableModelRow.difference();
				break;
			default:
				break;
		}

		return ReturnValue;
	}

	public boolean isCellEditable(int row, int col) {
		// this has to change, because for AccountClosing we must edit this Values sometimes
		return false;
	}

	private void lese_werte() {
		/*
		 * get the current Amount Sum of each type of money
		 */
		int closingDetailId = 0;
		String expenditureEventName = "";
		Double expenditureAmount = 0.0;
		Double expenditureAmountPlan = 0.0;
		Double expenditureDifference = 0.0;

		if (billingMonth != null && Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}",billingMonth)) {
			// if TableData contain Values => flush them before fill it with new data
			if (tableData.size() > 0) {
				tableData.clear();
			}

			DBTools getter = new DBTools(cn);

			getter.select("""
					SELECT "abschlussDetailId", "kategorieBezeichnung", "summeBetraege", "planBetrag", differenz
					FROM ha_abschlussdetails
					WHERE "abschlussMonat" = '%s'
					ORDER BY "kategorieBezeichnung"
					""".formatted(billingMonth), 5);

			try {
				getter.beforeFirst();

				while (getter.next()) {
					closingDetailId = getter.getInt("abschlussDetailId");
					expenditureEventName = getter.getString("kategorieBezeichnung");
					expenditureAmount = getter.getDouble("summeBetraege");
					expenditureAmountPlan = getter.getDouble("planBetrag");
					expenditureDifference = getter.getDouble("differenz");

					tableData.add(new JointAccountClosingDetailTableRow(closingDetailId, expenditureEventName,
							expenditureAmount, expenditureAmountPlan, expenditureDifference));
				}
			} catch (Exception e) {
				System.out.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
			}
		}
	}

	public void aktualisiere(String billingMonth) {
		this.billingMonth = billingMonth;
		this.lese_werte();
		fireTableDataChanged();
	}

	public void setDetailIdsForMarkingDifferenceValue(String[] detailIdsForMarkingDifferenceValue) {
		this.detailIdsForMarkingDifferenceValue = detailIdsForMarkingDifferenceValue;
		fireTableDataChanged();
	}

	public boolean rowHasToMark(int row) {
		for (String detailId : detailIdsForMarkingDifferenceValue) {
			if ((Integer)this.getValueAt(row, -1) == Integer.parseInt(detailId)) {
				return true;
			}
		}
		return false;
	}
}
