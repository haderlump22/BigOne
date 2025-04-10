package de.rachel.bigone;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBTools {
	private Statement st = null;
	private ResultSet rs = null;
	private Connection cn = null;
	private Object[][] daten;

	public DBTools(Connection LoginCN) {
		try {
			cn = LoginCN;
			st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public boolean insert(String sql) {
		try {
			st.executeUpdate(sql);
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean update(String sql) {
		try {
			st.executeUpdate(sql);
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void select(String sql, int iFields) {
		try {
			rs = st.executeQuery(sql);

			// auf Grund der uebergebenen Feldanzahl und der ermittelten
			// datensaetze das array redimensionieren
			rs.last();
			daten = new Object[rs.getRow()][iFields];
			rs.beforeFirst();

			// daten aus dem recordset in das stringarray lesens
			while (rs.next()) {
				for (int i = 0; i < iFields; i++) {
					daten[rs.getRow() - 1][i] = rs.getObject(i + 1);
				}
			}

			// recordset/statement wieder leeren
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void beforeFirst() {
		try {
			rs.beforeFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getInt(String columnLabel) throws SQLException {
		return rs.getInt(columnLabel);
	}

	public String getString(String columnLabel) throws SQLException {
		return rs.getString(columnLabel);
	}

	public Object[][] getData() {
		return daten;
	}

	public int getRowCount() {
		return daten.length;
	}

	public Object getValueAt(int row, int col) {
		return daten[row][col];
	}
}
