package de.rachel.bigone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

/**
 * some little tools for work
 *
 */
public class BigOneTools {
	private String strFileName;
	
	BigOneTools(){
		
	}
	public int ermittle_anzahl_zeilen(String strValue) {
		//ermittelt die Anzahl der Zeilen strValue uebergebenen
		//Datei
		strFileName = strValue;
		int iZeilen = 0;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(strFileName));
			while (in.readLine() != null) {
				iZeilen++; //zeilenzaehler erhoehen
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iZeilen;
	}
	public double roundScale2( double d ){
	    return Math.round( d * 100 ) / 100.;
	  }
	static public int extractEreigId(String strEreigAusCmbBox) {
		return Integer.valueOf(strEreigAusCmbBox.substring(strEreigAusCmbBox.indexOf('(') + 1,strEreigAusCmbBox.indexOf(')'))).intValue();
	}
	static public String datum_wandeln(String text,int iArt) {
		//Art 0
		//diese Funktion konvertiert ein Datum im Format TT.MM.JJJJ
		//in das fuer die SQL DB notwendige Datumsformat JJJJ-MM-TT
		//Art 1
		//genau umgekehrt
		String strTag, strMonat, strJahr, strErg = "";
		if(iArt == 0) {
			strTag = text.substring(0,2);
			strMonat = text.substring(3,5);
			strJahr = text.substring(6);
			strErg = strJahr+"-"+strMonat+"-"+strTag;
		}
		if(iArt == 1) {
			strTag = text.substring(8);
			strMonat = text.substring(5,7);
			strJahr = text.substring(0,4);
			strErg = strTag+"."+strMonat+"."+strJahr;
		}
		return strErg;
	}

	static public String getKontenId(String IBAN, Connection cn) {
		//ermittelt die KontenID der angegebenen IBAN aus der Tabelle konten
		DBTools getter = new DBTools(cn);
		
		String sql = "select konten_id from konten WHERE iban = '" + IBAN + "'";
		getter.select(sql, 1);
		
		//wenn genau ein Datensatz gefunden wurde dann die ID zurückgeben
		if (getter.getRowCount() == 0)
			return (String) getter.getValueAt(0, 0);
		else
			return "";
	}
}
