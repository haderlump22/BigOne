package de.rachel.bigone;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBTools {
	private Statement st = null;
	private ResultSet rs = null;
	private Connection cn = null;
	private int RowCount;
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
			RowCount = rs.getRow();
			daten = new Object[RowCount][iFields];
			rs.beforeFirst();

			// daten aus dem recordset in das stringarray lesens
			while (rs.next()) {
				for (int i = 0; i < iFields; i++) {
					daten[rs.getRow() - 1][i] = rs.getObject(i + 1);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void beforeFirst() throws SQLException {
		rs.beforeFirst();
	}

	public int getInt (String columnLabel) throws SQLException {
		return rs.getInt(columnLabel);
	}

	public String getString (String columnLabel) throws SQLException {
		return rs.getString(columnLabel);
	}

	public Date getDate (String columnLabel) throws SQLException {
		return rs.getDate(columnLabel);
	}

	public Double getDouble (String columnLabel) throws SQLException {
		return rs.getDouble(columnLabel);
	}

	public Boolean next () throws SQLException {
		return rs.next();
	}

	public Object[][] getData() {
		return daten;
	}

	public int getRowCount() {
		return RowCount;
	}

	public Object getValueAt(int row, int col) {
		return daten[row][col];
	}

	public ResultSet getResultSet() {
		return rs;
	}
}
