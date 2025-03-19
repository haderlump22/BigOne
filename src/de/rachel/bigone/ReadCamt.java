package de.rachel.bigone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.JOptionPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Einlesen von
 *
 * - SEPA Dateien des Formats (SEPA CAMT.053) Bank To Customer Statement = Kontoauszug
 * - Kontoauszug Diba im CSV Format
 */
public class ReadCamt {
	private Document KontoAuszug;
	private String[][] buchungen;
	private String[] csvContent;
	private int buchungsAnzahl;
	private String sIBAN = "";
	private String AccountOwner = "";
	private String[] NodeToFind = {"ValDt","CdtDbtInd","Amt","Ustrd","Cdtr","Dbtr"};
	private static int ValueDate = 0;
	private static int CreditDebitIndicator = 1;
	private static int Amount = 2;
	private static int Unstructured = 3;	//Unstrukturierter Verwendungszweck 140zeichen max
	private static int Creditor = 4;
	private static int Debitor = 5;

	ReadCamt(String PathAndFile) {

		// decide what FileType must be read
		if (PathAndFile.endsWith("ZIP") || PathAndFile.endsWith("zip")) {
			// extract Zip File and read each xml file to one Array

		} else if (PathAndFile.endsWith("xml")) {
			KontoAuszug = parseXML(PathAndFile);

			if (KontoAuszug != null) {
				buchungsAnzahl = KontoAuszug.getElementsByTagName("Ntry").getLength();

				buchungen = new String[buchungsAnzahl][6];

				//set IBAN from EBICs File
				sIBAN = findSubs(KontoAuszug.getElementsByTagName("Acct").item(0), "IBAN", "").trim();

				//write Entrys from EBICs File in to Array
				NodeList rows = KontoAuszug.getElementsByTagName("Ntry");

				for(int i = 0; i < rows.getLength(); i++){
					buchungen[i][ValueDate] = findSubs(rows.item(i), NodeToFind[0], "").trim();
					buchungen[i][CreditDebitIndicator] = findSubs(rows.item(i), NodeToFind[1], "").trim();
					buchungen[i][Amount] = findSubs(rows.item(i), NodeToFind[2], "").trim();
					buchungen[i][Unstructured] = findSubs(rows.item(i), NodeToFind[3], "").trim();

					if (buchungen[i][CreditDebitIndicator].equals("CRDT")) {
						buchungen[i][Creditor] = null;
						buchungen[i][Debitor] = findSubs(rows.item(i), NodeToFind[5], "").trim();
					} else {
						buchungen[i][Creditor] = findSubs(rows.item(i), NodeToFind[4], "").trim();
						buchungen[i][Debitor] = null;
					}
				}
			}
		} else {
			// the other possibility is csv
			csvContent = ReadCsv(PathAndFile);

			// chech witch Bank is the Account from
			// 0 nothing is found
			// 1 ING DIBA
			// 2 Postbank csv
			int iBank = checkBank();

			switch (iBank) {
				case 0:
					JOptionPane.showMessageDialog(null, "CSV Daten konten keiner bekannten Bank zugeordnet werden!",
							"Achtung", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
					break;
				case 1:
					// read the DIBA Content in this Object like the CAMT Data above
					readDibaData(csvContent);
					break;
				case 2:
					// read the DIBA Content in this Object like the CAMT Data above
					readPostbankData(csvContent);
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
	public String getDbtr(int iZeile) {
		return buchungen[iZeile][Debitor];
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
	public String getAccountOwner() {
		return this.AccountOwner;
	}

	private String findSubs(Node listItem, String NodeName, String ValueToFind) {
		NodeList Subs = listItem.getChildNodes();

		for (int i = 0; i < Subs.getLength(); i++) {
			Node Item = Subs.item(i);

			if (Item.getNodeName().equals(NodeName)) {
				if (NodeName.equals("Cdtr") || NodeName.equals("Dbtr") ) {
					// der Kreditor steht im Nm Tag unter Cdtr, bzw. der Debitor unter Dbtr, aber nicht immer an gleicher Stelle
					// deshalb wird das Item Nm gesucht und dessen Textcontent zur�ckgeben
					for (int b = 0; b < Item.getChildNodes().getLength(); b++) {
						if (Item.getChildNodes().item(b).getNodeName().equals("Nm"))
							ValueToFind = Item.getChildNodes().item(b).getTextContent();
					}
				} else {
					ValueToFind = ValueToFind.trim() + " " + Item.getTextContent().trim();
				}

				// Ustrd (Verwednungszweck) kommt mehrfach hintereinander vor
				// deshalb wird bei diesem Node nicht abgebrochen
				// bei allen anderen endet das Auslesen an dieser Stelle
				if (!NodeName.equals("Ustrd"))
					break;
			} else {
				if (Item.hasChildNodes()) {
					ValueToFind = findSubs(Item, NodeName, ValueToFind);
				}
			}
		}
		return ValueToFind;
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
				e.printStackTrace();
			}

			sContent = new String[iRows];

			// to inspect the content of the CSV File we import all in an Array
			for (int i = 0; i < iRows; i++) {
				try {
					sContent[i] = KontoauszugCsv.readLine();
				} catch (IOException e) {
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
			e.printStackTrace();
		}

		// count the Rows
		int iRows = 0;

		try {
			while (KontoauszugCsv.readLine() != null)
				iRows++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			KontoauszugCsv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iRows;
	}
	private void readDibaData(String[] csvContent) {
		int iHeaderRow;

		// search IBAN in the csvContent
		this.sIBAN = searchIban().replaceAll(" ", "");
		this.AccountOwner = searchAccountOwner();

		// check how many usable Datarows are in the csv
		// the length of the csvContent Array minus the not usable Rows one to that line that
		// begins with "Buchung;Valuta"
		iHeaderRow = findHeaderRow();

		// reinitial the Array buchunge new
		this.buchungen = new String[csvContent.length - iHeaderRow - 1][6];

		// the Diba Data begin in the row after the Header
		for (int i = iHeaderRow + 1; i < csvContent.length; i++) {
			buchungen[i - (iHeaderRow + 1)][ValueDate] = BigOneTools.datum_wandeln(csvContent[i].split(";")[1], 0);

			// the Credit or Dbit Inticator is in the csv Data not a separate field
			// they is indicates by e minus or nothing bevore the amount (creditorische Buchung = haben / Debitorische Buchung = soll)
			buchungen[i - (iHeaderRow + 1)][CreditDebitIndicator] = getCreditDebitIndicator(csvContent[i].split(";")[7].replace(".", "").replaceAll(",", "."));

			// the amount in the csv is german, we have to replace the thousand dot with null
			// and the decimal separator with a dot
			buchungen[i - (iHeaderRow + 1)][Amount] = delteSign(csvContent[i].split(";")[7].replace(".", "").replaceAll(",", "."));
			buchungen[i - (iHeaderRow + 1)][Unstructured] = csvContent[i].split(";")[4];
			buchungen[i - (iHeaderRow + 1)][Creditor] = csvContent[i].split(";")[2];
			buchungen[i - (iHeaderRow + 1)][Debitor] = csvContent[i].split(";")[2]; // wird nicht extra aufgeführt deshalb wird der selbe wert gelesen
		}
	}
	private void readPostbankData(String[] csvContent) {
		int iHeaderRow;
		String AccountBalance;
		boolean isGermanFormat = true;

		// IBAN is in Line 3 in the csvContent
		this.sIBAN = csvContent[2].split(";")[2];

		// AccountOwner ar not present in Postbank CSV
		this.AccountOwner = "";

		// Headerrow is in Line 8
		iHeaderRow = 7;

		// Postbank sometimes change the Format of Numbers (sometime German (3.222,67) sometime englisch (3,112.23) )
		// At the amount of the account balance in line 6 ca we check what Format is it at this time
		AccountBalance = csvContent[5].split(";")[4];

		// look at the Third place from behind, if there is a dot the Format is not German
		Character DecimalSeparator = AccountBalance.charAt(AccountBalance.length() - 3);
		if (DecimalSeparator.equals('.')) {
			isGermanFormat = false;
		}

		// reinitial the Array buchungen new
		this.buchungen = new String[csvContent.length - 1 - 8][6];

		// the Diba Data begin in the row after the Header, end ends 1 line bevor the last
		for (int i = iHeaderRow + 1; i < (csvContent.length - 1); i++) {
			// the Date in this fields has no leading zeros
			// so i converted in so one
			Integer year, month, day;
			String convertedDate;
			year = Integer.parseInt(csvContent[i].split(";")[1].split("\\.")[2]);
			month = Integer.parseInt(csvContent[i].split(";")[1].split("\\.")[1]);
			day = Integer.parseInt(csvContent[i].split(";")[1].split("\\.")[0]);

			// Date as String in Format yyyy-MM-dd
			convertedDate = LocalDate.of(year, month, day).toString();

			buchungen[i - (iHeaderRow + 1)][ValueDate] = convertedDate;

			// the Credit or Dbit Inticator is in the csv Data not a separate field
			// they is indicates by a minus or nothing bevore the amount (creditorische Buchung = haben / Debitorische Buchung = soll)
			buchungen[i - (iHeaderRow + 1)][CreditDebitIndicator] = getCreditDebitIndicator(csvContent[i].split(";")[11].replace(".", "").replaceAll(",", "."));

			// the amount in the csv is german, we have to replace the thousand dot with null
			// and the decimal separator with a dot
			if (isGermanFormat) {
				buchungen[i - (iHeaderRow + 1)][Amount] = delteSign(csvContent[i].split(";")[11].replace(".", "").replaceAll(",", "."));
			} else {
				buchungen[i - (iHeaderRow + 1)][Amount] = delteSign(csvContent[i].split(";")[11].replaceAll(",", ""));
			}
			buchungen[i - (iHeaderRow + 1)][Unstructured] = csvContent[i].split(";")[4];
			buchungen[i - (iHeaderRow + 1)][Creditor] = csvContent[i].split(";")[3];
			buchungen[i - (iHeaderRow + 1)][Debitor] = csvContent[i].split(";")[3]; // wird nicht extra aufgeführt deshalb wird der selbe wert gelesen
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
	private String searchAccountOwner() {
		// search in the whole Array at the Line that begin with "Kunde"
		for (int i = 0; i < csvContent.length; i++) {
			// only if the String isn't empty
			if (!csvContent[i].equals("")) {
				String[] rowParts = csvContent[i].split(";");

				if (rowParts[0].equals("Kunde")) {
					return rowParts[1];
				}
			}
		}

		return "";
	}
	private int checkBank() {
		String[] rowParts;
		// in the CSV Data we can check from witch Bank they are

		// in line 5 we find the info for the ING
		rowParts = csvContent[4].split(";");
		if (rowParts[0].equals("Bank") && rowParts[1].equals("ING")) {
			return 1;
		}

		// or in line 3 we find the info for the Postbank
		rowParts = csvContent[2].split(";");
		if (rowParts[0].equals("Postbank Giro plus")) {
			return 2;
		}

		return 0;
	}
}
