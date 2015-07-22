package de.rachel.bigone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
		//in das fuer MySql notwendige Datumsformat JJJJ-MM-TT
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
}
