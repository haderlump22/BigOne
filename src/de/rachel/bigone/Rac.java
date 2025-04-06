package de.rachel.bigone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.toedter.calendar.JDateChooser;

import de.rachel.bigone.editors.ComboTableCellEditor;
import de.rachel.bigone.editors.LiquiDateTableCellEditor;
import de.rachel.bigone.listeners.RACMouseListener;
import de.rachel.bigone.models.RACTableModel;
import de.rachel.bigone.renderer.RACTableCellRenderer;

public class Rac {
	private Connection cn = null;
	private static final int TANKEN = 6;
	private static final int AUFTEILUNG = 52;
	private JFrame RACWindow;
	private JTable table;
	private RACTableModel model;
	private JButton btnOpen;
	private JButton btnImp;
	private JFileChooser open;
	private JScrollPane sp;
	private JDateChooser dateFrom, dateTo;
	private JLabel lblDateFrom, lblDateTo, lblIbanValue;
	private JPanel pnlTimeRangeSection, pnlIbanInfo;
	private String BankStatementFile;

	Rac(Connection LoginCN) {
		cn = LoginCN;

		RACWindow = new JFrame("Kontoauszug einlesen");
		RACWindow.setSize(785, 530);
		RACWindow.setLocation(200, 200);
		RACWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		RACWindow.setLayout(null);
		RACWindow.setResizable(false);

		open = new JFileChooser();
		// beim OpenDialog sollen nur XML oder CSV Dateien angezeigt werden
		open.setFileFilter(new FileNameExtensionFilter("XML/CSV/ZIP", "xml", "csv", "zip"));

		btnOpen = new JButton("Auszug öffnen");
		btnOpen.setBounds(30, 17, 115, 35);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// clear Date Fields if open a new Statement of Account
				dateFrom.setCalendar(null);
				dateTo.setCalendar(null);

				if (open.showOpenDialog(RACWindow) != JFileChooser.CANCEL_OPTION) {
					BankStatementFile = open.getSelectedFile().toString();
					ReadCamt Auszug = new ReadCamt(BankStatementFile);

					if (Auszug.getBuchungsanzahl() > 0) {
						// set a eventualy new IBAN
						lblIbanValue.setText(Auszug.getIbanFormatted());
						lblIbanValue.setToolTipText(Auszug.getAccountOwner());

						if (table == null) {
							zeichne_tabelle(Auszug);
						} else {
							RACTableCellRenderer ren = new RACTableCellRenderer(cn, lblIbanValue.getText());
							table.setDefaultRenderer(Object.class, ren);
							model = (RACTableModel) table.getModel();
							model.aktualisiere(Auszug);
						}

						// nach dem erfolgreichen einlesen der zu importierenden daten
						// den Button aktivieren
						btnImp.setEnabled(true);
					} else {
						System.out
								.println("Keine Buchungen in Datei " + open.getSelectedFile().toString() + "gefunden!");
						RACWindow.remove(sp);
						RACWindow.validate();
						RACWindow.repaint();
					}
				} else {
					btnImp.setEnabled(false);
				}
			}
		});

		btnImp = new JButton("importieren");
		btnImp.setBounds(150, 17, 115, 35);
		btnImp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				String sql = "";
				String AccountId = "";
				String sLiquiMonat = "";
				boolean insert_details_success = true;
				boolean insert_tankdaten_success = true;
				DBTools pusher = new DBTools(cn);
				DBTools getter = new DBTools(cn);
				DBTools AccountIdGetter = new DBTools(cn);
				model = (RACTableModel) table.getModel();

				// insert each Row in the DB
				// if exist a Account ID to the IBAN that comes from the Bank Statement
				if (lblIbanValue.getText() != "") {
					// because the IBAN in the Lable contains Spaces for easy to read, they must
					// delete before get some Data from the DB
					AccountIdGetter.select("SELECT konten_id FROM konten WHERE iban = '"
							+ lblIbanValue.getText().replaceAll(" ", "") + "'", 1);
					AccountId = AccountIdGetter.getValueAt(0, 0).toString();

					// tabellendaten von der letzten Zeile zur ersten hin importieren
					// damit die altesten buchungen als erste in der Tabelle geschrieben
					// werden
					for (int i = model.getRowCount() - 1; i >= 0; i--) {
						insert_details_success = true;

						// set the Value for liqui_monat in case of the model Value at field 5
						if (model.getValueAt(i, 5) == null) {
							sLiquiMonat = "NULL";
						} else {
							sLiquiMonat = "'" + model.getValueAt(i, 5) + "'";
						}
						sql = "INSERT into transaktionen "
								+ "(soll_haben, konten_id, datum, betrag, buchtext, ereigniss_id, liqui_monat) "
								+ "VALUES " + "('" + model.getValueAt(i, 1) + "', " + "'" + AccountId + "', '"
								+ model.getValueAt(i, 0) + "'," + model.getValueAt(i, 2) + ",'"
								+ model.getValueAt(i, 3).toString().replace("'", "''") + "',"
								+ BigOneTools.extractEreigId(model.getValueAt(i, 6).toString()) + "," + sLiquiMonat
								+ ");";

						// neuen datensatz einfuegen und den erfolg pruefen
						// falls Datensatz nicht eingefuegt werden konnt
						// mit dem naechsten weiter machen und nicht abbrechen
						if (pusher.insert(sql) == false) {
							System.out.println("Fehler beim Import des Datensatzes Nr: " + i);
							continue;
						}
						// die Ereignissid pruefen und eventuelle Tankdaten oder
						// aufteilungen in die DB Tabellen eintragen
						switch (BigOneTools.extractEreigId(model.getValueAt(i, 6).toString())) {
							case AUFTEILUNG:
								// Aufteilungen zur Eintragung aufnehmen
								// das Programm arbeitet weiter wenn
								// dialog geschlossen wird
								Aufteilung aufteil = new Aufteilung(RACWindow,
										Double.valueOf(model.getValueAt(i, 2).toString()).doubleValue(), model.getValueAt(i, 3).toString(), cn);

								// da gerade der letzte Datensatz in die tabelle transaktionen eingetragen
								// wurde kann man auch schon dessen ID feststellen
								getter.select("SELECT max(transaktions_id) from transaktionen;", 1);

								String[][] datenAuft = aufteil.getDaten();

								for (String[] arg : datenAuft) {
									String sql_auft = "INSERT INTO aufteilung "
											+ "( transaktions_id, betrag, ereigniss_id, liqui) " + "VALUES " + "("
											+ getter.getValueAt(0, 0) + ", " + arg[1] + ", " + arg[0] + ", " + arg[2]
											+ ");";
									// System.out.println(sql_auft);
									if (pusher.insert(sql_auft) == false) {
										System.out.println(
												"Fehler beim Einfuegen der Detaildatensaetze zu Datensatz Nr: " + i);
										insert_details_success = false;
										continue; // mit dem naechsten Datensatz beim einfuegen weitermachen
									}
								}
								break;
							case TANKEN:
								// tankwerte zur eintragung aufnehmen
								// das Programm arbeitet weiter wenn
								// dialog geschlossen wird
								TankDialog td = new TankDialog(RACWindow, model.getValueAt(i, 2).toString(), cn);
								getter.select("SELECT max(transaktions_id) from transaktionen;", 1);

								String sql_tanken = "INSERT INTO tankdaten "
										+ "( transaktions_id, liter, km, kraftstoff_id, datum_bar, betrag_bar, kfz_id) "
										+ "VALUES " + "(" + getter.getValueAt(0, 0) + ", " + td.get_liter() + ", "
										+ td.get_km() + ", " + td.get_treibstoff_id() + ", " + "NULL, " + "NULL, "
										+ td.get_kfz_id() + ");";

								// neuen datensatz einfuegen und den erfolg pruefen
								if (pusher.insert(sql_tanken) == false) {
									System.out.println("Fehler beim Einfuegen der Tankdaten zu Datensatz Nr: " + i);
									insert_tankdaten_success = false;
									break;
								}
						}

						if (insert_details_success == false || insert_tankdaten_success == false) {
							continue; // ist ein fehler aufgetreten dann mit dem naechsten datensatz
							// beim einfuegen weitermachen
						}
					}
				} else {
					System.out.println("Keine IBAN vorhanden. Kann Daten nicht importieren");

				}

				// nach erfolgreicher importierung

				// disable the import Button
				btnImp.setEnabled(false);

				// set the Timerange to null
				dateFrom.setCalendar(null);
				dateTo.setCalendar(null);

				// show all Rows from the originally opened Bankfile to choose an new Timerange
				ReadCamt Auszug = new ReadCamt(BankStatementFile);
				RACTableCellRenderer ren = new RACTableCellRenderer(cn, lblIbanValue.getText());
				table.setDefaultRenderer(Object.class, ren);
				model = (RACTableModel) table.getModel();
				model.aktualisiere(Auszug);
			}
		});

		// create the time range section that clean the Array from Rows that outside the
		// Timerange
		pnlTimeRangeSection = new JPanel();
		pnlTimeRangeSection.setLayout(null);
		pnlTimeRangeSection.setBounds(605, 17, 150, 70);
		pnlTimeRangeSection.setBorder(new TitledBorder("Zeitraum"));

		lblDateFrom = new JLabel("von");
		lblDateFrom.setBounds(10, 15, 30, 20);

		dateFrom = new JDateChooser();
		dateFrom.setBounds(40, 15, 100, 20);
		dateFrom.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// if select a Date Property and it is not null
				if (evt.getPropertyName() == "date" && evt.getNewValue() != null) {
					// before compare the Dates, check is not null
					if (dateTo.getDate() != null) {
						if (dateFrom.getDate().before(dateTo.getDate())) {
							// Arraycleaning can start
							model = (RACTableModel) table.getModel();
							model.removeUnusedRows(dateFrom.getDate(), dateTo.getDate());

							// after choose Timerange enabled Import Button
							btnImp.setEnabled(true);
						} else {
							System.out.println("bis Datum ist kleiner als das von Datum!!!");
						}
					}
				}
			}
		});

		lblDateTo = new JLabel("bis");
		lblDateTo.setBounds(10, 40, 30, 20);

		dateTo = new JDateChooser();
		dateTo.setBounds(40, 40, 100, 20);
		dateTo.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == "date" && evt.getNewValue() != null) {
					// before compare the Dates, check is not null
					if (dateFrom.getDate() != null) {
						if (dateFrom.getDate().before(dateTo.getDate())) {
							// Arraycleaning can start
							model = (RACTableModel) table.getModel();
							model.removeUnusedRows(dateFrom.getDate(), dateTo.getDate());

							// after choose Timerange enabled Import Button
							btnImp.setEnabled(true);
						} else {
							System.out.println("bis Datum ist kleiner als das von Datum!!!");
						}
					}
				}
			}
		});

		pnlTimeRangeSection.add(lblDateFrom);
		pnlTimeRangeSection.add(dateFrom);
		pnlTimeRangeSection.add(lblDateTo);
		pnlTimeRangeSection.add(dateTo);

		// create the IBAN Info
		pnlIbanInfo = new JPanel();
		pnlIbanInfo.setLayout(null);
		pnlIbanInfo.setBounds(275, 17, 220, 45);
		pnlIbanInfo.setBorder(new TitledBorder("IBAN"));

		lblIbanValue = new JLabel();
		lblIbanValue.setBounds(10, 15, 200, 20);

		pnlIbanInfo.add(lblIbanValue);

		// put all to the Frame
		RACWindow.add(btnOpen);
		RACWindow.add(btnImp);
		RACWindow.add(pnlTimeRangeSection);
		RACWindow.add(pnlIbanInfo);
		RACWindow.validate();
		RACWindow.repaint();

		// Datei auswählen lassen
		if (open.showOpenDialog(RACWindow) != JFileChooser.CANCEL_OPTION) {
			BankStatementFile = open.getSelectedFile().toString();
			ReadCamt Auszug = new ReadCamt(BankStatementFile);
			if (Auszug.getBuchungsanzahl() > 0) {
				lblIbanValue.setText(Auszug.getIbanFormatted());
				lblIbanValue.setToolTipText(Auszug.getAccountOwner());
				zeichne_tabelle(Auszug);
				btnImp.setEnabled(true);
			} else {
				System.out.println("Keine Buchungen in Datei " + open.getSelectedFile().toString() + " gefunden!");
			}
		} else {
			btnImp.setEnabled(false);
		}

		RACWindow.setVisible(true);
	}

	private void zeichne_tabelle(ReadCamt Auszug) {

		table = new JTable(new RACTableModel(Auszug));

		// festlegen von diversen Verhaltensweisen der Tabelle
		RACTableCellRenderer ren = new RACTableCellRenderer(cn, lblIbanValue.getText());
		table.setDefaultRenderer(Object.class, ren); //vieleicht auch direkt als new in klammern???
		// table.getSelectionModel().addListSelectionListener(new RACSelectionListener(table));
		table.getColumnModel().getColumn(5).setCellEditor(new LiquiDateTableCellEditor());
		table.getColumnModel().getColumn(6).setCellEditor(new ComboTableCellEditor(cn));

		// fuer einige spalten feste breiten einrichten
		table.getColumnModel().getColumn(0).setMinWidth(78);
		table.getColumnModel().getColumn(0).setMaxWidth(78);
		table.getColumnModel().getColumn(1).setMinWidth(25);
		table.getColumnModel().getColumn(1).setMaxWidth(25);
		table.getColumnModel().getColumn(2).setMinWidth(55);
		table.getColumnModel().getColumn(2).setMaxWidth(55);
		table.getColumnModel().getColumn(4).setMinWidth(68);
		table.getColumnModel().getColumn(4).setMaxWidth(68);
		table.getColumnModel().getColumn(5).setMinWidth(78);
		table.getColumnModel().getColumn(5).setMaxWidth(78);
		table.getColumnModel().getColumn(6).setMinWidth(120);
		table.getColumnModel().getColumn(6).setMaxWidth(120);

		// selbst definierten Mouselistener der RAC Tabelle hinzufügen
		table.addMouseListener(new RACMouseListener(table));

		sp = new JScrollPane(table);
		sp.setBounds(30,120,725,355);

		RACWindow.add(sp);
		RACWindow.validate();
		RACWindow.repaint();
	}
}
