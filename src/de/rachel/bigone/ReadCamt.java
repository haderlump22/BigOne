package de.rachel.bigone;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadCamt {
	private Document KontoAuszug;
	private String[][] buchungen;
	private String[] csvContent;
	private int buchungsAnzahl;
	private String sIBAN = "";
	private String[] NodeToFind = {"ValDt","CdtDbtInd","Amt","Ustrd","Cdtr"};
	private static int ValueDate = 0;
	private static int CreditDebitIndicator = 1;
	private static int Amount = 2;
	private static int Unstructured = 3;	//Unstrukturierter Verwendungszweck 140zeichen max
	private static int Creditor = 4;
	
	ReadCamt(String PathAndFile) {
		
		// decide what FileType must be read
		if (PathAndFile.endsWith("xml")) {
			KontoAuszug = parseXML(PathAndFile);
			
			if (KontoAuszug != null) {
				buchungsAnzahl = KontoAuszug.getElementsByTagName("Ntry").getLength();
				
				buchungen = new String[buchungsAnzahl][5];
				
				//set IBAN from EBICs File
				sIBAN = findSubs(KontoAuszug.getElementsByTagName("Acct").item(0), "IBAN", "").trim();
				
				//write Entrys from EBICs File in to Array
				NodeList rows = KontoAuszug.getElementsByTagName("Ntry");
				
				for(int i = 0; i < rows.getLength(); i++){
					buchungen[i][ValueDate] = findSubs(rows.item(i), NodeToFind[0], "").trim();
					buchungen[i][CreditDebitIndicator] = findSubs(rows.item(i), NodeToFind[1], "").trim();
					buchungen[i][Amount] = findSubs(rows.item(i), NodeToFind[2], "").trim();
					buchungen[i][Unstructured] = findSubs(rows.item(i), NodeToFind[3], "").trim();
					buchungen[i][Creditor] = findSubs(rows.item(i), NodeToFind[4], "").trim();
				}
			}
		} else {
			// the other possibility is csv
			csvContent = ReadCsv(PathAndFile);
			
			// chech witch Bank is the Account from
			// 0 nothing is found
			// 1 ING DIBA
			int iBank = checkBank();
			
			switch (iBank) {
			case 0:
				JOptionPane.showMessageDialog(null, "CSV Daten konten keiner bekannten Bank zugeordnet werden!", "Achtung", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
				break;
			case 1:
				// read the DIBA Content in this Object like the CAMT Data above
				readDibaData(csvContent);
				break;
			default:
				break;
			}
		}	
	}
	private Document parseXML(String PathAndFile){
		DocumentBuilder DocBuilder = getDocBuilder();
		
		try {
			return DocBuilder.parse(new File(PathAndFile));
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	private DocumentBuilder getDocBuilder(){
		DocumentBuilderFactory DocBuilderFactory = DocumentBuilderFactory.newInstance();;
		
		try {
			return DocBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	public int getBuchungsanzahl(){
		return buchungen.length;
	}
	public String getValDt(int iZeile){
		return buchungen[iZeile][ValueDate];
	}
	public String getCdtDbtInd(int iZeile){
		return buchungen[iZeile][CreditDebitIndicator];
	}
	public String getAmt(int iZeile){
		return buchungen[iZeile][Amount];
	}
	public String getUstrd(int iZeile){
		return buchungen[iZeile][Unstructured];
	}
	public String getCdtr(int iZeile){
		return buchungen[iZeile][Creditor];
	}
	public String getIBAN(){
		return sIBAN;
	}
	public String getIbanFormatted() {
		// format the IBAN String with Spaces after every 4th Character
		String sIbanFormatted = "";
		int iIbanCharacters = 0;
		int iCounter = 0;
		
		for (iIbanCharacters = 0; iIbanCharacters < this.getIBAN().length(); iIbanCharacters++) {
			if(iCounter == 4) {
				sIbanFormatted = sIbanFormatted + " ";
				iCounter = 0;
			}
			sIbanFormatted = sIbanFormatted + this.getIBAN().charAt(iIbanCharacters);
			iCounter++;
		}
		
		return sIbanFormatted;
	}
	private String findSubs(Node listItem, String NodeName, String FindValue){
		NodeList Subs = listItem.getChildNodes();
		
		for(int i = 0; i < Subs.getLength(); i++){
			Node Item = Subs.item(i);

			if(Item.getNodeName().equals(NodeName)){
				if(NodeName.equals("Cdtr")){
					//der Kreditor steht im Nm Tag unter Cdtr, aber nicht immer an gleicher Stelle
					//deshalb wird das Item Nm gesucht und dessen Textcontent zur�ckgeben
					for(int b = 0; b < Item.getChildNodes().getLength(); b++){
						if(Item.getChildNodes().item(b).getNodeName().equals("Nm"))
							FindValue = Item.getChildNodes().item(b).getTextContent();
					}
				}else{
					FindValue = FindValue.trim() + " " + Item.getTextContent().trim();
				}

				if(!NodeName.equals("Ustrd"))
						break;
			}else{
				if(Item.hasChildNodes()){
					FindValue = findSubs(Item, NodeName, FindValue);
				}
			}
		}
		return FindValue;
	}
	private String[] ReadCsv(String PathAndFile) {
		String[] sContent = new String[0];
		int iRows = 0;
		
		// if the CSV File has more than 0 Rows
		if ((iRows = getCsvRowCount(PathAndFile)) > 0) {
			BufferedReader KontoauszugCsv = null;
			try {
				KontoauszugCsv = new BufferedReader(new FileReader(PathAndFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sContent = new String[iRows];

			// to inspect the content of the CSV File we import all in an Array
			for (int i = 0; i < iRows; i++) {
				try {
					sContent[i] = KontoauszugCsv.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sContent;
	}
	private String delteSign(String sAmountWithSign) {
		if (sAmountWithSign.startsWith("-"))
			return sAmountWithSign.substring(1);
		else
			return sAmountWithSign;
	}
	private String getCreditDebitIndicator(String sAmount) {
		//CRDT should have
		//DBIT should
		if (sAmount.startsWith("-"))
			return "DBIT";
		else
			return "CRDT";
	}
	private int getCsvRowCount(String PathAndFile) {
		BufferedReader KontoauszugCsv = null;
		try {
			KontoauszugCsv = new BufferedReader(new FileReader(PathAndFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// count the Rows
		int iRows = 0;
		
		try {
			while (KontoauszugCsv.readLine() != null)
				iRows++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		try {
			KontoauszugCsv.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iRows;
	}
	private void readDibaData(String[] csvContent) {
		int iHeaderRow;
		
		// search IBAN in the csvContent
		this.sIBAN = searchIban().replaceAll(" ", "");

		// check how many usable Datarows are in the csv
		// the length of the csvContent Array minus the not usable Rows one to that line that 
		// begins with "Buchung;Valuta"
		iHeaderRow = findHeaderRow();
		
		// reinitial the Array buchunge new
		this.buchungen = new String[csvContent.length - iHeaderRow - 1][5];

		// the Diba Data begin in the row after the Header
		for (int i = iHeaderRow + 1; i < csvContent.length; i++) {
			buchungen[i - (iHeaderRow + 1)][ValueDate] = BigOneTools.datum_wandeln(csvContent[i].split(";")[1], 0);
			
			// the Credit or Dbit Inticator is in the csv Data not a separate field
			// they is indicates by e minus or nothing bevore the amount
			buchungen[i - (iHeaderRow + 1)][CreditDebitIndicator] = getCreditDebitIndicator(csvContent[i].split(";")[7].replace(".", "").replaceAll(",", "."));
			
			// the amount in the csv is german, we have to replace the thousand dot with null
			// and the decimal separator with a dot
			buchungen[i - (iHeaderRow + 1)][Amount] = delteSign(csvContent[i].split(";")[7].replace(".", "").replaceAll(",", ".")); 
			buchungen[i - (iHeaderRow + 1)][Unstructured] = csvContent[i].split(";")[4];
			buchungen[i - (iHeaderRow + 1)][Creditor] = csvContent[i].split(";")[2];
		}
	}
	private int findHeaderRow() {
		// find the Row that contians the Headers like "Buchung" or "Valuta"
		for (int i = 0; i < csvContent.length; i++) {
			// only if the String isn't empty
			if (!csvContent[i].equals("")) {
				if (csvContent[i].matches("^Buchung;Valuta.*")) {
					return i;
				}
			}
		}
		return 0;
	}
	private String searchIban() {
		// search in the whole Array at a german IBAN
		for (int i = 0; i < csvContent.length; i++) {
			// only if the String isn't empty
			if (!csvContent[i].equals("")) {
				String[] rowParts = csvContent[i].split(";");
				
				for (int i2 = 0; i2 < rowParts.length; i2++) {
					if (rowParts[i2].matches("^DE\\d{2}\\s?([0-9]{4}\\s?){4}[0-9]{2}$")) {
						return rowParts[i2];
					}
				}
			}
		}
		return null;
	}
	private int checkBank() {
		// in the CSV Data we can check from witch Bank they are
		for (int i = 0; i < csvContent.length; i++) {
			String[] rowParts = csvContent[i].split(";");
			
			// check if the Result hat min 2 Parts with the right Content
			if (rowParts.length == 2) {
				// the only Bank we know at this Point ist the ING DIBA
				// the String "Bank" followed by "ING"
				if (rowParts[0].equals("Bank") && rowParts[1].equals("ING")) {
					return 1;
				}
			}
		}
		return 0;
	}
}
