package de.rachel.bigone;

import java.sql.Array;
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
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
		}
	}

	public boolean insert(String sql) {
		try {
			st.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
			System.err.println("insert: Programmabbruch!!!");
			System.exit(1);
			return false;
		}
	}

	public boolean insertWithReturn(String sql) {
		try {
			rs = st.executeQuery(sql);
			return true;
		} catch (SQLException e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
			System.err.println("insertWithReturn: Programmabbruch!!!");
			System.exit(1);
			return false;
		}
	}

	public boolean update(String sql) {
		try {
			st.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			System.err.println("UpdateStatement failed: -" + sql + "-");
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + ": " + e.toString());
			return false;
		}
	}

    public void select(String sql) {
        try {
            rs = st.executeQuery(sql);

        } catch (SQLException e) {
            System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
                    + e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
        }
    }

	public void beforeFirst() throws SQLException {
		rs.beforeFirst();
	}

	public void first() throws SQLException {
		rs.first();
	}

	public int getInt (String columnLabel) throws SQLException {
		return rs.getInt(columnLabel);
	}

	public Array getArray (String columnLabel) throws SQLException {
		return rs.getArray(columnLabel);
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

	public boolean getBoolean (String columnLabel) throws SQLException {
		return rs.getBoolean(columnLabel);
	}

	public Boolean next () throws SQLException {
		return rs.next();
	}

	public int getRowCount() {
		Integer row = 0;

		try {
			if (rs.last()) {
				row = rs.getRow();
				rs.beforeFirst();
			}
		} catch (Exception e) {
			System.err.println("Fehler beim ermitteln der Zeilenanzahl im Resultset");
		}

		return row;
	}
}
