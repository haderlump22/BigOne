package de.rachel.bigone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConstants {
	//public static final String DRIVER   = "org.gjt.mm.mysql.Driver";
	public static final String DRIVER   = "org.postgresql.Driver";
	//public static final String PROTOCOL = "jdbc:mysql://localhost:3306/";
	public static final String PROTOCOL = "jdbc:postgresql://localhost:5432/";
	public static final String DATABASE = "bigone";
	//public static final String DATABASE = "testdb";
	public static final String URL      = PROTOCOL + DATABASE;
	public static final String USER     = "domm";
	public static final String PASS     = "Fdosco+65";
	private Statement  st = null;
	private ResultSet  rs = null;
	private Connection cn = null;
	private Object[][] daten;

	public DatabaseConstants() { 
		try {
			//Select fitting database driver and connect:
			Class.forName( DRIVER );
			cn = DriverManager.getConnection( URL, USER, PASS );
			st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
		} 
	}
	public boolean insert(String sql) {
		try {
			st.executeUpdate(sql);
			return true;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public void select(String sql, int iFields) {
		try {
			rs = st.executeQuery( sql );
			
			//auf Grund der uebergebenen Feldanzahl und der ermittelten
			//datensaetze das array redimensionieren
			rs.last();
			daten = new Object[rs.getRow()][iFields];
			rs.beforeFirst();
			
			//daten aus dem recordset in das stringarray lesens
			while ( rs.next() ) {
				for(int i=0; i<iFields ; i++)
					daten[rs.getRow()-1][i] = rs.getObject(i+1);
			}

			//recordset/statement wieder leeren
			rs = null;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		} 
	}
	public Object[][] getData(){
		return daten;
	}
	public int getRowCount() {
		return daten.length;
	}
	public Object getValueAt(int row, int col) {
		return daten[row][col];
	}
	public void connection_close() {
		try {
			cn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
