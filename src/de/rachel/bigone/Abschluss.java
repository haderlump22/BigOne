package de.rachel.bigone;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class Abschluss {
	private JFrame liquiwindow;
	private JPanel pnlAbrMonat, pnlRestwert, pnlEinahmenAufteilung;
	private JFormattedTextField txtAbrMonat, txtNutzBetr;
	private JTextArea txtHinweis, txtHinweisAufteilung;
	private JComboBox<String> cmbPerson;
	private JCheckBox chkAufteilung;
	private JLabel lblPerson, lblBuchtext;
	private DefaultListModel<String> lstModelAllIncome, lstModelIncomePerPerson;
	private DefaultComboBoxModel<String> cmbModelPerson;
	private JList<String> lstAllIncome, lstIncomPerPerson;
	private JScrollPane spAllIncome, spIncomePerPerson;
	private JButton btnAdd, btnRemove, btnCalcPercentualPortion;
	private Font fontTxtFields, fontCmbBoxes, fontLists;
	private Object[][] daten = null;
	private Connection cn = null;
	private String KontenID = "12";


	Abschluss(Connection LoginCN){
		cn = LoginCN;
		liquiwindow = new JFrame("Monatsabschluss");
		liquiwindow.setSize(800,580);
		liquiwindow.setLocation(200,200);
		liquiwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		liquiwindow.setLayout(null);
		liquiwindow.setResizable(false);

		//schriftenfestlegungen
		fontTxtFields = new Font("Arial", Font.PLAIN,16);
		fontCmbBoxes = new Font("Arial", Font.PLAIN,16);
		fontLists = new Font("Arial",Font.PLAIN,10);

		pnlAbrMonat = new JPanel();
		pnlAbrMonat.setLayout(null);
		pnlAbrMonat.setBounds(30,30,150,60);
		pnlAbrMonat.setBorder(new TitledBorder("Abrechnungsmonat"));
			//inhalt fuer Panel AbrMonat erstellen
			try {
				txtAbrMonat = new JFormattedTextField(new MaskFormatter("01-##-20##"));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			txtAbrMonat.setBounds(20,25,110,25);
			txtAbrMonat.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtAbrMonat.setFont(fontTxtFields);
			txtAbrMonat.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent ke) {
				}
				@Override
				public void keyReleased(KeyEvent ke) {
					if( ke.getKeyCode() == KeyEvent.VK_ENTER && Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}",txtAbrMonat.getText())) {
						txtHinweis.setText(""); //clear txtHinweis Field
						lstModelAllIncome.clear(); //clear form old values from the previos call
						cmbModelPerson.removeAllElements(); //clear befor put new elements in to it
						calculate_profit(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));
						putPersonNameToCmbPerson();
						putIncomeToListAllIncome(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));

						//check if the actual liquimonth is already fix and it is so, then set the buttons, who put and remove
						//incomes to and from persons, inactive
						if(is_liqui_fix(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0))){
							btnAdd.setEnabled(false);
							btnRemove.setEnabled(false);
						}else {
							btnAdd.setEnabled(true);
							btnRemove.setEnabled(true);
						}
					}
				}
				@Override
				public void keyPressed(KeyEvent ke) {
				}
			});
		//inhalte fuer das Panel AbrMonat auf selbiges legen
		pnlAbrMonat.add(txtAbrMonat);

		pnlRestwert = new JPanel();
		pnlRestwert.setLayout(null);
		pnlRestwert.setBounds(30,115,150,60);
		pnlRestwert.setBorder(new TitledBorder("Monatsrestwert"));
			//inhalt fuer Panel Restwert erstellen
			txtNutzBetr = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
			txtNutzBetr.setBounds(20,25,110,25);
			txtNutzBetr.setHorizontalAlignment(JFormattedTextField.RIGHT);
			txtNutzBetr.setFont(fontTxtFields);
			txtNutzBetr.setText("0,00");
			txtNutzBetr.addFocusListener( new FocusListener() {
				public void focusLost( FocusEvent fe ) {

				}
				public void focusGained(FocusEvent fe) {
					txtNutzBetr.selectAll();
				}
			});
		//inhalte fuer das Panel AbrMonat auf selbiges legen
		pnlRestwert.add(txtNutzBetr);

		//Textfeld fuer Hinweise zum errechneten Betrag
		txtHinweis = new JTextArea();
		txtHinweis.setBounds(195, 30, 260, 205);
		txtHinweis.setBorder(BorderFactory.createEtchedBorder());
		txtHinweis.setEditable(false); //infos sollen nur vom Programm gesetzt werden

		//Bereich fuer die Einnahmenaufteilung anlegen
		pnlEinahmenAufteilung = new JPanel();
		pnlEinahmenAufteilung.setLayout(null);
		pnlEinahmenAufteilung.setBounds(30,240,640,300);
		pnlEinahmenAufteilung.setBorder(new TitledBorder("Einnahmenaufteilung"));
			//elemente fuer die Einnahmenaufteilung anlegen
			lblPerson = new JLabel("Person");
			lblPerson.setBounds(20, 25, 110, 25);

			cmbModelPerson = new DefaultComboBoxModel<String>();
			cmbPerson = new JComboBox<String>(cmbModelPerson);
			cmbPerson.setBounds(130, 25, 170, 25);
			cmbPerson.setFont(fontCmbBoxes);
			cmbPerson.addItemListener( new ItemListener() {
			      public void itemStateChanged( ItemEvent e ) {
			    	  if(e.getStateChange() == 1) {
		    			  //clear the List IncomePerPerson when Person is changed (to another or to nothing)
		    			  lstModelIncomePerPerson.clear();

			    		  if(cmbPerson.getSelectedIndex() > 0){
			    			  //extract personen_id from selected Value
			    			  String selectedValue = cmbPerson.getSelectedItem().toString();
			    			  Integer personen_id = Integer.valueOf(selectedValue.substring(selectedValue.indexOf("(") + 1, selectedValue.indexOf(")")));

			    			  //fill lstIncomePerPerson
			    			  getIncomeForSelectedPerson(personen_id, BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));
			    		  }
			    	  }
			      }
			 } );

			//Scrollliste der gesammten Einnahmen
			lstModelAllIncome = new DefaultListModel<String>();

			lstAllIncome = new JList<String>(lstModelAllIncome);
			lstAllIncome.setFont(fontLists);
			lstAllIncome.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseClicked(MouseEvent e) {
					//do someone only when elements exist
					if(lstModelAllIncome.getSize() > 0) {
						String elementValue = lstModelAllIncome.getElementAt(lstAllIncome.getSelectedIndex());
						lblBuchtext.setToolTipText(getBuchText(elementValue.substring(elementValue.indexOf("(") + 1, elementValue.indexOf(")"))));

						//chek if a splitted part of the transaktions_id of the selectet Value is already put to a
						//person, if it is so, the checkbox split must checked and protect against unchecked
						if(existSplittedPartInIpp(elementValue.substring(elementValue.indexOf("(") + 1, elementValue.indexOf(")")))) {
							chkAufteilung.setSelected(true);
							chkAufteilung.setEnabled(false);
						}else {
							chkAufteilung.setSelected(false);
							chkAufteilung.setEnabled(true);
						}
					}

				}
			});

			spAllIncome = new JScrollPane(lstAllIncome);
			spAllIncome.setBounds(20, 65, 100, 205);

			//scrollliste der einnahmen pro Person
			lstModelIncomePerPerson = new DefaultListModel<String>();

			lstIncomPerPerson = new JList<String>(lstModelIncomePerPerson);
			lstIncomPerPerson.setFont(fontLists);

			spIncomePerPerson = new JScrollPane(lstIncomPerPerson);
			spIncomePerPerson.setBounds(220, 65, 100, 205);

			//checkbox fuer die Aufteilungskennzeichnung
			chkAufteilung = new JCheckBox("Split",false);
			chkAufteilung.setBounds(145, 70, 60, 30);

			//Buttons fuer das hinzufuegen und entfernen der Betraege zu den Personen
			btnAdd = new JButton(">>");
			btnAdd.setBounds(145, 130, 60, 20);
			btnAdd.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent ae){
	            	Integer AllIncomeSelectedIndex;
	            	Integer PersonSelectedIndex;

	            	//note important selected Index
	            	AllIncomeSelectedIndex = lstAllIncome.getSelectedIndex();
	            	PersonSelectedIndex = cmbPerson.getSelectedIndex();

	            	//move the selected field from lstAllIncome to lstIncomePerPerson
	            	if(AllIncomeSelectedIndex >= 0 && PersonSelectedIndex >= 1){
	            		//note some selected Values
	            		String selectedValue = cmbPerson.getSelectedItem().toString();
	            		Integer personen_id = Integer.valueOf(selectedValue.substring(selectedValue.indexOf("(") + 1, selectedValue.indexOf(")")));

	            		//get Selected Value from the AllIncomeList
	            		String selectedAmountWithKey = lstModelAllIncome.getElementAt(AllIncomeSelectedIndex);

	            		//extract transaktions_id from the selected Amountvalue
	            		Integer transaktions_id = Integer.valueOf(selectedAmountWithKey.substring(selectedAmountWithKey.indexOf("(") + 1, selectedAmountWithKey.indexOf(")")));
	            		String sAmount = selectedAmountWithKey.substring(0, selectedAmountWithKey.indexOf(" ("));

	            		//check if Amount have to split and get the Person_id who get the splited Amount
	            		if(chkAufteilung.isSelected()) {
	            			String AmountPart = JOptionPane.showInputDialog("Bitte den Teilbetrag von " + sAmount + " eingeben:", sAmount);
	            			AmountPart = AmountPart.replace(",", ".");
	            			if(AmountPart.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
	            				lstModelIncomePerPerson.insertElementAt(AmountPart+" ("+transaktions_id+")", lstModelIncomePerPerson.getSize());

	            				//remove old Amount Value from List AllIncome
	            				lstModelAllIncome.remove(AllIncomeSelectedIndex);

	            				//calculate the diffrence between Old Amount and the AmountPart
	            				Float AmountAllIncomeNew = Float.valueOf(sAmount) - Float.valueOf(AmountPart);

	            				//put difference in the List AllIncome if it greater then zero
	            				if(AmountAllIncomeNew > 0)
	            					lstModelAllIncome.insertElementAt(AmountAllIncomeNew.toString()+" ("+transaktions_id+")", lstModelAllIncome.getSize());

	            				//put AmountPart in the IPP Table
	            				pushAmountToTableIPP(AmountPart, transaktions_id, personen_id, "true");
	            			}else {
	            				JOptionPane.showMessageDialog(liquiwindow, "Eingabe ist kein gültiger Zahlwert! Der Gesamtbetrag wird genommen.");
	            				lstModelIncomePerPerson.insertElementAt(selectedAmountWithKey, lstModelIncomePerPerson.getSize());
			            		lstModelAllIncome.remove(AllIncomeSelectedIndex);
			            		pushAmountToTableIPP(sAmount, transaktions_id, personen_id, "false");
	            			}
	            		}else {
	            			lstModelIncomePerPerson.insertElementAt(selectedAmountWithKey, lstModelIncomePerPerson.getSize());
		            		lstModelAllIncome.remove(AllIncomeSelectedIndex);
		            		pushAmountToTableIPP(sAmount, transaktions_id, personen_id, "false");
	            		}
	            	}
	            }
	        });

			btnRemove = new JButton("<<");
			btnRemove.setBounds(145, 175, 60, 20);
			btnRemove.addActionListener(new ActionListener(){
	            public void actionPerformed(ActionEvent ae){
	            	Integer IncomePerPersonSelectedIndex;
	            	Integer PersonSelectedIndex;
	            	Boolean transaktions_id_is_in_elements = false;
            		int eID = 0; //counter for the elements in the Enumeration to find an element with the same transaktions_id

	            	//note important selected Indexes
	            	IncomePerPersonSelectedIndex = lstIncomPerPerson.getSelectedIndex();
	            	PersonSelectedIndex = cmbPerson.getSelectedIndex();

	            	//move the selected field from lstIncomePerPerson to lstAllIncome
	            	if(IncomePerPersonSelectedIndex >= 0 && PersonSelectedIndex >= 1){
	            		String selectedPersonAmountWithKey = lstModelIncomePerPerson.getElementAt(IncomePerPersonSelectedIndex);

	            		//note the transaktions_id from the selectet Value at the right site
	            		String transaktions_id_right_site = selectedPersonAmountWithKey.substring(selectedPersonAmountWithKey.indexOf("(") + 1, selectedPersonAmountWithKey.indexOf(")"));

	            		//check if the transaktions_id from the element that remove from the
	            		//IncomPerPerson List at the right site, is alredy in the list AllIncome at the
	            		//left Site. If it is so, then we must add the amount of the elements at the left side to the right
	            		//where the transaktions_id is the same
	            		Enumeration<String> e = lstModelAllIncome.elements();

            			while(e.hasMoreElements() && !transaktions_id_is_in_elements) {
            				//note the actual Elemant in the Enumeration
            				String actualElement = e.nextElement().toString();

            				//note the transaktions_id from the actual element at the left site
            				String transaktions_id_left_site = actualElement.substring(actualElement.indexOf("(") + 1, actualElement.indexOf(")"));

            				if(transaktions_id_right_site.equals(transaktions_id_left_site)){
            					//we need not look further and set transaktions_id_is_in_elements to true
            					//that will break the while loop
            					transaktions_id_is_in_elements = true;
            				}else {
            					transaktions_id_is_in_elements = false;
            					eID++;
            				}
            			}

            			if(transaktions_id_is_in_elements) {
            				//if found an element with the same id in the left field, add the amount of the removed
                			//element at the right site, to the amount of the found element and put the result
                			//at the end of the listAllIncome

            				//note the two Amounts that have to add
            				String AmountFromRightSite = selectedPersonAmountWithKey.substring(0, selectedPersonAmountWithKey.indexOf(" ("));
            				String AmountFromLeftSite = lstModelAllIncome.getElementAt(eID).substring(0, lstModelAllIncome.getElementAt(eID).indexOf(" ("));
            				Float newAmount = Float.valueOf(AmountFromRightSite) + Float.valueOf(AmountFromLeftSite);

            				//remove the element at the right and the left site
            				lstModelIncomePerPerson.remove(IncomePerPersonSelectedIndex);
            				lstModelAllIncome.remove(eID);

            				//put the new build Element to the left site
            				lstModelAllIncome.insertElementAt(newAmount+" ("+transaktions_id_right_site+")", lstModelAllIncome.getSize());
            			}else {
            				//if not found, only put the removed Elemnt to the end of the List at the left site
            				lstModelAllIncome.insertElementAt(selectedPersonAmountWithKey, lstModelAllIncome.getSize());
    	            		lstModelIncomePerPerson.remove(IncomePerPersonSelectedIndex);
            			}

	            		//delete the moved Amount in the Table ipp for the selected Person
	            		String selectedValue = cmbPerson.getSelectedItem().toString();
	            		Integer personen_id = Integer.valueOf(selectedValue.substring(selectedValue.indexOf("(") + 1, selectedValue.indexOf(")")));
	            		deleteAmountFromTableIPP(selectedPersonAmountWithKey, personen_id);
	            	}
	            }
	        });

			lblBuchtext = new JLabel("Buchungstext (Tooltip)");
			lblBuchtext.setBounds(20, 275, 300, 20);

			btnCalcPercentualPortion = new JButton("Anteilberechnung");
			btnCalcPercentualPortion.setBounds(330, 65, 160, 20);
			btnCalcPercentualPortion.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					int personCount;
					double[] PersonAmount;
					double TotalPersonAmount = 0;

					//clear the infotextfield
					txtHinweisAufteilung.setText("");

					//note the amount of person in the cmb box person
					//the first entry is not a person
					personCount = cmbModelPerson.getSize() - 1;

					//sum up the acounts of each person if there are one
					if(personCount > 0) {
						PersonAmount = new double[personCount];
						double dResidualValue = Double.valueOf(txtNutzBetr.getText().replace(",", "."));

						for(int PersonCounter = 1; PersonCounter <= personCount; PersonCounter++) {
							String selectedValue = cmbPerson.getItemAt(PersonCounter).toString();
		            		Integer personen_id = Integer.valueOf(selectedValue.substring(selectedValue.indexOf("(") + 1, selectedValue.indexOf(")")));

		            		PersonAmount[PersonCounter - 1] = getPersonAcountSumPerLiqui(personen_id, BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));
		            		TotalPersonAmount = roundScale2(TotalPersonAmount + PersonAmount[PersonCounter - 1]);

						}

						//hint the total sum of all person
						txtHinweisAufteilung.append("Gesammtsumme:\n");
						txtHinweisAufteilung.append(Double.toString(TotalPersonAmount).replace(".", ",") + "\n");

						//calc the proportion of the sums in percent, to the total Sum of all persons
						txtHinweisAufteilung.append("Sparbetrag: "+Double.toString(dResidualValue).replace(".", ",")+"\n");
						for (int PersonCounter = 0; PersonCounter < PersonAmount.length; PersonCounter++) {
							String tmpPersonName = cmbPerson.getItemAt(PersonCounter + 1).toString();
							String PersonName = tmpPersonName.substring(0, tmpPersonName.indexOf(" "));
							double percent = roundScale2((PersonAmount[PersonCounter] * 100) / TotalPersonAmount);
							double dValue = roundScale2((percent * dResidualValue) / 100);
							txtHinweisAufteilung.append(PersonName + ": "
									+ Double.toString(percent).replace(".", ",") + "% / "
									+ Double.toString(dValue).replace(".", ",") + "\n");
						}
					}
				}
			});

			//Textfeld fuer Hinweise zum errechneten Betrag
			txtHinweisAufteilung = new JTextArea();
			txtHinweisAufteilung.setBounds(330, 90, 220, 180);
			txtHinweisAufteilung.setBorder(BorderFactory.createEtchedBorder());
			txtHinweisAufteilung.setEditable(false); //infos sollen nur vom Programm gesetzt werden

			//zuweisen der Elemente fuer das Panel Einnahmenaufteilung
		pnlEinahmenAufteilung.add(lblPerson);
		pnlEinahmenAufteilung.add(cmbPerson);
		pnlEinahmenAufteilung.add(spAllIncome);
		pnlEinahmenAufteilung.add(spIncomePerPerson);
		pnlEinahmenAufteilung.add(chkAufteilung);
		pnlEinahmenAufteilung.add(btnAdd);
		pnlEinahmenAufteilung.add(btnRemove);
		pnlEinahmenAufteilung.add(lblBuchtext);
		pnlEinahmenAufteilung.add(btnCalcPercentualPortion);
		pnlEinahmenAufteilung.add(txtHinweisAufteilung);

		liquiwindow.add(pnlAbrMonat);
		liquiwindow.add(pnlRestwert);
		liquiwindow.add(txtHinweis);
		liquiwindow.add(pnlEinahmenAufteilung);

		liquiwindow.setVisible(true);

	}
	protected void deleteAmountFromTableIPP(String selectedPersonAmountWithKey,	Integer personen_id) {
		//IPP = IncomePerPerson
		DBTools setter = new DBTools(cn);

		//extract transaktions_id from the selected Amountvalue
		Integer transaktions_id = Integer.valueOf(selectedPersonAmountWithKey.substring(selectedPersonAmountWithKey.indexOf("(") + 1, selectedPersonAmountWithKey.indexOf(")")));

		//delete Amount in the Table IPP for the Person how is identified over the personen_id
		setter.update("DELETE FROM income_per_person WHERE personen_id = "+ personen_id +
			" AND transaktions_id = " + transaktions_id);

	}
	protected void pushAmountToTableIPP(String Amount, Integer transaktions_id, Integer personen_id, String Splitt) {
		//IPP = IncomePerPerson
		DBTools setter = new DBTools(cn);

		//push Amount in the Table IPP for the Person how is identified over the personen_id
		setter.insert("INSERT INTO income_per_person (personen_id, transaktions_id, betrag, split) " +
					"VALUES ("+ personen_id +", " + transaktions_id +", " + Amount +", '"+Splitt+"')");
	}
	protected void getIncomeForSelectedPerson(Integer personen_id, String sAbrMonat) {
		//get all Income for the Person that is identified by the Personen_id
		DBTools getter = new DBTools(cn);

		getter.select("SELECT income_per_person.betrag, income_per_person.transaktions_id FROM income_per_person, transaktionen t WHERE personen_id = " + personen_id +
				" AND income_per_person.transaktions_id = t.transaktions_id "+
				"AND t.liqui_monat = '" + sAbrMonat + "'",2);
		if(getter.getRowCount() > 0) {
			Object[][] lstModelIncomePerPersonValues = getter.getData();

			for(Object[] lstModelIncomePerPersonValue : lstModelIncomePerPersonValues)
				lstModelIncomePerPerson.addElement(lstModelIncomePerPersonValue[0].toString() + " (" + lstModelIncomePerPersonValue[1].toString() + ")");
		}
	}
	protected void putPersonNameToCmbPerson() {
		//put all persons to the cmbmodel cmbModelPerson
		DBTools getter = new DBTools(cn);

		//set First Value (select invitation) to cmbPerson
		cmbModelPerson.addElement("---bitte wählen---");

		getter.select("SELECT p.name, p.vorname, p.personen_id FROM personen p " +
				"WHERE p.gueltig = TRUE",3);

		if(getter.getRowCount() > 0) {
			Object[][] cmbModelPersonValues = getter.getData();

		    for(Object[] cmbModelPersonValue : cmbModelPersonValues)
		    	cmbModelPerson.addElement(cmbModelPersonValue[1] + " " + cmbModelPersonValue[0] + " (" + cmbModelPersonValue[2] + ")");
		}
	}
	private void putIncomeToListAllIncome(String sAbrMonat) {
		//put all incomes that not put to a person to the listmodel lstModelAllIncome
		//incomes that partially split, are not determined here
		DBTools getter = new DBTools(cn);

		getter.select("SELECT t.betrag, t.transaktions_id FROM transaktionen t LEFT JOIN income_per_person ON t.transaktions_id = income_per_person.transaktions_id " +
				"WHERE income_per_person.transaktions_id IS NULL " +
				"AND t.soll_haben= 'h' AND t.konten_id= " + KontenID + " AND t.liqui_monat='" +
				sAbrMonat + "'",2);

		if(getter.getRowCount() > 0) {
			Object[][] lstModelAllIncomeValues = getter.getData();

		    for(Object[] lstModelAllIncomeValue : lstModelAllIncomeValues)
		    	lstModelAllIncome.addElement(lstModelAllIncomeValue[0].toString()+" ("+lstModelAllIncomeValue[1].toString()+")");
		}

		//now find Balance from incomes that partially split

		//first get transaktion_id's in actual liqui_monat that have asisngned splited value in ipp
		DBTools ids_getter = new DBTools(cn);
		ids_getter.select("select income_per_person.transaktions_id from transaktionen t, income_per_person "+
				"where t.soll_haben = 'h' and t.liqui_monat = '" + sAbrMonat + "' "+
				"AND t.konten_id = " + KontenID + " and income_per_person.transaktions_id = t.transaktions_id "+
				"and income_per_person.split = TRUE GROUP BY income_per_person.transaktions_id", 1);

		//get the the sum of partially assigned ammount for each received transaktion_id
		if(ids_getter.getRowCount() > 0) {
			DBTools sum_getter = new DBTools(cn);

			Object[][] transaktion_ids = ids_getter.getData();
			for(Object[] ids : transaktion_ids) {
				sum_getter.select("select sum(income_per_person.betrag) from income_per_person where split = TRUE and transaktions_id = "+ids[0].toString(), 1);

				//now get the difference of the just received sum and the total amount in transtaktionen
				DBTools diff_getter = new DBTools(cn);

				diff_getter.select("select (t.betrag - "+ sum_getter.getValueAt(0, 0).toString()+") diff, t.transaktions_id "+
							"from transaktionen t where t.transaktions_id = "+ ids[0].toString(), 1);

				//if the diff is greater than zero than add the value and the transaktion_id to
				//the lstModelAllIncome
				if(Double.valueOf(diff_getter.getValueAt(0, 0).toString()).doubleValue() > 0)
					lstModelAllIncome.addElement(Double.valueOf(diff_getter.getValueAt(0, 0).toString()).doubleValue()+" ("+ids[0].toString()+")");
			}
		}
	}
	private double getPersonAcountSumPerLiqui(Integer personen_id, String liquiDatum) {
		DBTools getter = new DBTools(cn);

		String sql = "SELECT sum(ipp.betrag) FROM income_per_person ipp, transaktionen t WHERE personen_id = " + personen_id + " " +
					"AND t.transaktions_id = ipp.transaktions_id AND t.liqui_monat = '"+ liquiDatum +"'";

		getter.select(sql, 1);

		// check whether income was set per person at all, if not, give back zero
		if (getter.getValueAt(0, 0) == null) {
			return 0;
		} else {
			return Double.valueOf(getter.getValueAt(0, 0).toString()).doubleValue();
		}
	}
	private void calculate_profit(String sAbrMonat) {
		double dblMtlJahrKosten, dblEin, dblAus, dblSummeFixkosten, dblNutzBetrag;

		//jahressparbetrag ermitteln
		dblMtlJahrKosten = monatliche_jahreskosten(sAbrMonat);
		//System.out.println("Jahrsparwert: " + dblMtlJahrKosten);
		txtHinweis.append("Jahrsparwert: " + dblMtlJahrKosten+"\n");

 		//die monatliche fixkosten in ein array schreiben
		monats_fixkosten(sAbrMonat);

		//einnahmen des abrechnungsmonats ermitteln
		dblEin = summiere_einnahmen(sAbrMonat);
		//System.out.println("Einnahmen: " + dblEin);
		txtHinweis.append("Einnahmen: " + dblEin+"\n");

		//ausgaben des abrechnungsmonats ermitteln (dabei sind die saetze mit
		//den ereignissid's der monatlichen fixausgaben ausgenommen, und auch die mit
		//der eieignissid 47 Jahresausgaben)
		dblAus = summiere_ausgaben(sAbrMonat);
		//System.out.println("Ausgaben: " + dblAus);
		txtHinweis.append("Ausgaben (ohne Fixkosten): " + dblAus+"\n");

		//die liquiditaetsfahigen teile von aufteilungsdatensaetzen zusammenaddieren
		//dabei werden einzeln haben und soll anteile zusammengerechnet
		//die ergebnisse werden dann jeweils den summen der einnahmen bzw der ausgaben
		//hinzugerechnet
		dblEin = dblEin + summiere_liqui_aus_aufteilung("h",sAbrMonat);
		dblAus = dblAus + summiere_liqui_aus_aufteilung("s",sAbrMonat);
		//System.out.println("Einnahmen(incl Aufteilung): " + dblEin);
		//System.out.println("Ausgaben(incl Aufteilung): " + dblAus);
		txtHinweis.append("Einnahmen(incl Aufteilung): " + dblEin+"\n");
		txtHinweis.append("Ausgaben(incl Aufteilung): " + dblAus+"\n");

		//nun wird von der differenz zwischen einnahmen und ausgaben
		//das jahressparen und alle gueltigen Monatlichen fixausgaben abgezogen
		dblSummeFixkosten = berechne_summe_fixkosten_aus_transaktionen(sAbrMonat);
		//System.out.println("Monatliche FixKost: " + dblSummeFixkosten);
		txtHinweis.append("Monatliche FixKost: " + roundScale2(dblSummeFixkosten)+"\n");

		//nun noch die endsumme berechnen und der textbox zuweisen
		dblNutzBetrag = roundScale2(dblEin - dblAus - dblSummeFixkosten - dblMtlJahrKosten);
		txtNutzBetr.setText(String.valueOf(dblNutzBetrag).replace(".", ","));
	}
	private double berechne_summe_fixkosten_aus_transaktionen(String sAbrMonat) {
		//berechnet anhand der bisher zusammengesellten Daten
		//den nutzbaren betrag
		int iZaehler;
		double dblSumFixKosten=0;
		double dblErgSql;
		DBTools getterSoll = new DBTools(cn);
		DBTools getterHaben = new DBTools(cn);

		for(iZaehler = 0; iZaehler < daten.length; iZaehler++)
		{
			// die Sollsumme des Betrags errechenen
			getterSoll.select("select sum(betrag) from transaktionen where ereigniss_id = " + daten[iZaehler][1].toString() +
					" and konten_id = " + KontenID + " and soll_haben = 's' and liqui_monat = '" + sAbrMonat + "';", 1);

			// eventuelle Habenbuchungen für Monatsfixkostenermitteln
			// z.B. ein Rücküberweisung von zuviel überwiesenen HaushaltHolle Beträgen
			getterHaben.select("select sum(betrag) from transaktionen where ereigniss_id = " + daten[iZaehler][1].toString() +
					" and konten_id = " + KontenID + " and soll_haben = 'h' and liqui_monat = '" + sAbrMonat + "';", 1);

			// sowohl in getterHaben als auch in getter Soll kommt nur ein Datensatz raus der kann aber NULL enthalten
			if(getterSoll.getValueAt(0, 0) != null)
				dblErgSql = roundScale2(Double.valueOf(getterSoll.getValueAt(0, 0).toString()).doubleValue());
			else
				dblErgSql = 0;

			// wenn ein Habenbetrag für diese FixKostenpositon existiert wird sie von der eben
			// ermittelten Sollsumme abgezogen
			if(getterHaben.getValueAt(0, 0) != null)
				dblErgSql = dblErgSql - roundScale2(Double.valueOf(getterHaben.getValueAt(0, 0).toString()).doubleValue());

		    //==========================================================================
		    //==========================================================================

		    //wenn von dieser geplanten fixausgabe nicht der ganze geplante
		    //Betrag ausgegeben wurde wird trozdem der ganze geplante Betrag zur berechnung
		    //herrangezogen es sei denn der geplante Betrag ist nicht "hart" dann
		    //nur die bisherigen ausgaben
			//System.out.println(dblErgSql + " / " + Double.valueOf(daten[iZaehler][0].toString()).doubleValue()+":"+daten[iZaehler][2]);
			txtHinweis.append(dblErgSql + " / " + Double.valueOf(daten[iZaehler][0].toString()).doubleValue()+":"+daten[iZaehler][2]+"\n");
		    if(dblErgSql > 0 && dblErgSql <= Double.valueOf(daten[iZaehler][0].toString()).doubleValue())
		    {
		    	if(daten[iZaehler][2].toString().equals("true"))
		    		dblSumFixKosten = dblSumFixKosten + Double.valueOf(daten[iZaehler][0].toString()).doubleValue();
		    	else
		    		dblSumFixKosten = dblSumFixKosten + roundScale2(dblErgSql);
		    }
		    //sollte die summe der zu diesem ereigniss eingetragenen ausgaben groesser sein
		    //als der geplante Betrag dann dann wird dieser groessere wert zur berechnung
		    //herangezogen
		    if(dblErgSql > Double.valueOf(daten[iZaehler][0].toString()).doubleValue())
		    {
		    	dblSumFixKosten = dblSumFixKosten + roundScale2(dblErgSql);
		    }
		    //falls fuer den eigentlich budgetierten Wert gilt "nur das was wirklich
		    //ausgegeben wurde" (sprich NICHT hart) dann auch nur das zu der Summe der Fixkosten hinzurechen
		    //ansonsten, im fall "hart" den Budgetierten Wert
		    if(dblErgSql == 0)
		    {
		    	if(daten[iZaehler][2].toString().equals("1"))
		    		dblSumFixKosten = dblSumFixKosten + Double.valueOf(daten[iZaehler][0].toString()).doubleValue();
		    	else
		    		dblSumFixKosten = dblSumFixKosten + 0;
		    }
		}

		return dblSumFixKosten;

	}
	private double summiere_liqui_aus_aufteilung(String sh, String sAbrMonat) {
		double dblWert;
		DBTools getter = new DBTools(cn);

		getter.select("select sum(aufteilung.betrag) from transaktionen, " +
    		  	"aufteilung where soll_haben = '" + sh + "' and liqui_monat = '" +
    		  	sAbrMonat + "'" +
    		  	"and transaktionen.ereigniss_id in (52) and transaktionen.konten_id = " + KontenID + " " +
    		  	"and transaktionen.transaktions_id = aufteilung.transaktions_id " +
      			"and aufteilung.liqui = TRUE;", 1);

		if(getter.getRowCount() > 0 && getter.getValueAt(0, 0) != null)
			dblWert = roundScale2(Double.valueOf(""+getter.getValueAt(0, 0)).doubleValue());
	    else
	    	dblWert = 0;

		//System.out.println("Aufteilung("+sh+"): "+ dblWert);
		return dblWert;

	}
	private double summiere_ausgaben(String sAbrMonat) {
		DBTools getter = new DBTools(cn);

		getter.select("select sum(betrag) from transaktionen where soll_haben " +
	    		  "= 's' and konten_id = " + KontenID + " and liqui_monat = '" + sAbrMonat + "' " +
	    		  "and ereigniss_id not in (select ereigniss_id from mtlausgaben where gilt_ab <= '" +
	    		  sAbrMonat + "' and gilt_bis >= '" +
	    		  sAbrMonat + "') and ereigniss_id not in(47,52);",1);

		//wenn keine Ausgaben (erkennbar an null im Objekt) dann 0.00 zurueckgeben
		if(getter.getValueAt(0, 0) == null)
			return 0.00;
		else
			return roundScale2(Double.valueOf(getter.getValueAt(0, 0).toString()));
	}
	private double summiere_einnahmen(String sAbrMonat) {
		DBTools getter = new DBTools(cn);

		getter.select("select sum(betrag) from transaktionen " +
	    		"where soll_haben = 'h' and konten_id = " + KontenID + " and liqui_monat = '" +
	    		sAbrMonat + "' and ereigniss_id not in (52);",1);

		//wenn keine Einnahmen (erkennbar an null im Objekt) dann 0.00 zurueckgeben
		if(getter.getValueAt(0, 0) == null)
			return 0.00;
		else
			return roundScale2(Double.valueOf(getter.getValueAt(0, 0).toString()));

	}
	private void monats_fixkosten(String sAbrMonat) {
		DBTools getter = new DBTools(cn);

		getter.select("select betrag, ereigniss_id, hart from mtlausgaben where gilt_ab <= '" +
		  		BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' " +
		  		"and gilt_bis >= '" + sAbrMonat + "';",3);

		//datenarray der liquiDBC instanz in das lokale datenarray kopieren
	    daten = new Object[getter.getRowCount()][3];

	    for(int i = 0; i < getter.getRowCount(); i++) {
	    	daten[i][0] = getter.getValueAt(i, 0);
	    	//System.out.print(daten[i][0]+"/");
	    	daten[i][1] = getter.getValueAt(i, 1);
	    	//System.out.print(daten[i][1]+"/");
	    	daten[i][2] = getter.getValueAt(i, 2);
	    	//System.out.println(daten[i][2]+"/");
	    }
	}
	private double monatliche_jahreskosten(String sAbrMonat) {
		DBTools getter = new DBTools(cn);

		getter.select("select sum(betrag) from jahresausgaben where gilt_ab <= '" +
		  		BigOneTools.datum_wandeln(txtAbrMonat.getText(),0) + "' " +
		  		"and gilt_bis >= '" + sAbrMonat + "';",1);

	    return roundScale2(Double.valueOf(getter.getValueAt(0, 0).toString()) / 12);

	}
	private double roundScale2( double d )
	  {
	    return Math.round( d * 100 ) / 100.;
	  }
	private String getBuchText(String transaktions_id) {
		//get the transaction wording to the transaktions_id
		DBTools getter = new DBTools(cn);

		getter.select("SELECT t.buchtext, ke.ereigniss_krzbez FROM transaktionen t, kontenereignisse ke " +
				"WHERE t.transaktions_id = " + transaktions_id + " AND ke.ereigniss_id = t.ereigniss_id", 2);

		if(getter.getRowCount() > 0) {
			return getter.getValueAt(0, 0).toString() +"//"+ getter.getValueAt(0, 1).toString();
		}else {
			return "not found";
		}
	}
	private boolean existSplittedPartInIpp(String transaktions_id) {
		DBTools getter = new DBTools(cn);

		getter.select("SELECT count(*) FROM income_per_person ipp " +
				"WHERE ipp.transaktions_id = " + transaktions_id, 1);

		if(getter.getValueAt(0, 0).toString().equals("0")) {
			return false;
		}else {
			return true;
		}
	}
	private boolean is_liqui_fix(String txtAbrMonat) {
		//check if a row in the table abschluss with the same value in the argument txtAbrMonat
		//exist and if the field abgeschlossen is true

		DBTools getter = new DBTools(cn);

		getter.select("SELECT abgeschlossen FROM abschluss WHERE liqui_monat = '" + txtAbrMonat + "'", 1);

		if(getter.getRowCount() > 0) {
			if(getter.getValueAt(0, 0).toString().equals("true")) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
}
