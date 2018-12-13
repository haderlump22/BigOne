package de.rachel.bigone;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadCamt {
	private Document KontoAuszug;
	private String[][] buchungen;
	private int buchungsAnzahl;
	private String sIBAN = "";
	private String[] NodeToFind = {"ValDt","CdtDbtInd","Amt","Ustrd","Cdtr"};
	private static int ValueDate = 0;
	private static int CreditDebitIndicator = 1;
	private static int Amount = 2;
	private static int Unstructured = 3;	//Unstrukturierter Verwendungszweck 140zeichen max
	private static int Creditor = 4;
	
	ReadCamt(String PathAndFile){
		
		KontoAuszug = parseXML(PathAndFile);
		
		if(KontoAuszug != null){
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
}
