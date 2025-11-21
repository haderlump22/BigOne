package de.rachel.bigone;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import de.rachel.bigone.listeners.JointAccountClosingEventInfoAreaKeyListener;
import de.rachel.bigone.listeners.JointAccountClosingDetailTableMouseListener;
import de.rachel.bigone.listeners.JointAccountClosingDetailTableSelectionListener;
import de.rachel.bigone.listeners.JointAccountClosingSumOverviewMouseListener;
import de.rachel.bigone.models.JointAccountClosingBalanceAllocationOverviewDetailTableModel;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;
import de.rachel.bigone.records.JointAccountClosingBalanceAllocationOverviewDetailTableRow;
import de.rachel.bigone.records.JointAccountClosingDetailTableRow;
import de.rachel.bigone.records.SalaryBasesSumOfIncomePerPartyTableRow;
import de.rachel.bigone.renderer.JointAccountClosingBalanceAllocationOverviewDetailTableCellRenderer;
import de.rachel.bigone.renderer.JointAccountClosingDetailTableCellRenderer;

public class JointAccountClosing {
	private Connection cn = null;
	private JFrame jointAccountClosingWindow;
	private Font fontTxtFields;// , fontCmbBoxes, fontLists;
	private JPanel billingMonthPanel, jointAccountClosingDetailPanel, eventExpenditureAmountPlanInfoAreaPanel,
			eventInfoAreaAccountClosingPanel, sumOverviewPanel, balanceAllocationOverviewPanel;
	private JFormattedTextField billingMonth, sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue;
	private JTable jointAccountClosingDetailTable, jointAccountClosingBalanceAllocationOverviewDetailTable;
	private JScrollPane jointAccountClosingDetailScrollPane, eventExpenditureAmountPlanInfoAreaScrollPane,
			eventInfoAreaAccountClosingScrollPane, jointAccountClosingBalanceAllocationOverviewScrollPane;
	private JTextArea eventExpenditureAmountPlanInfoArea, eventInfoAreaAccountClosing;
	private JLabel sumOverviewNegativeLabel, sumOverviewPositiveLabel, sumOverviewPlanedLabel, sumOverviewUnplanedLabel;
	private JointAccountClosingSumOverviewMouseListener sumOverviewMouseListener;
	private JButton closeBillingMonth;
	private boolean billingMonthAlreadyClosed = false;

    JointAccountClosing (Connection LoginCN) {
		cn = LoginCN;

        this.createComponents();

		this.createListeners();

		this.registerExistingListeners();

        this.createLayout();

        jointAccountClosingWindow.setVisible(true);
    }

    private void createComponents() {
		jointAccountClosingWindow = new JFrame("Haushaltskontoabschluss");
		jointAccountClosingWindow.setSize(900, 680);
		jointAccountClosingWindow.setLocation(200, 200);
		jointAccountClosingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jointAccountClosingWindow.setResizable(true);

		fontTxtFields = new Font("Arial", Font.PLAIN, 14);
		// fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);
		// fontLists = new Font("Arial", Font.PLAIN, 10);

		billingMonthPanel = new JPanel();
		billingMonthPanel.setPreferredSize(new Dimension(150, 60));
		billingMonthPanel.setBorder(new TitledBorder("Abrechnungsmonat"));

		try {
			billingMonth = new JFormattedTextField(new MaskFormatter("01-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		billingMonth.setPreferredSize(new Dimension(110, 25));
		billingMonth.setHorizontalAlignment(JFormattedTextField.RIGHT);
		billingMonth.setFont(fontTxtFields);

		billingMonthPanel.add(billingMonth);

		jointAccountClosingDetailTable = new JTable(new JointAccountClosingDetailTableModel(cn));
		jointAccountClosingDetailTable.setDefaultRenderer(Object.class, new JointAccountClosingDetailTableCellRenderer());

		// setting width for some columns
		// has to do

		jointAccountClosingDetailScrollPane = new JScrollPane(jointAccountClosingDetailTable);
		jointAccountClosingDetailScrollPane.setPreferredSize(new Dimension(420, 170));

		jointAccountClosingDetailPanel = new JPanel();
		jointAccountClosingDetailPanel.setBorder(new TitledBorder("Ausgabensummen"));

		eventExpenditureAmountPlanInfoArea = new JTextArea();
		eventExpenditureAmountPlanInfoArea.setLineWrap(true);
        eventExpenditureAmountPlanInfoArea.setWrapStyleWord(true);
        eventExpenditureAmountPlanInfoArea.setEditable(false);

        eventExpenditureAmountPlanInfoAreaScrollPane = new JScrollPane(eventExpenditureAmountPlanInfoArea);
        eventExpenditureAmountPlanInfoAreaScrollPane.setPreferredSize(new Dimension(200, 70));

		eventExpenditureAmountPlanInfoAreaPanel = new JPanel();
        eventExpenditureAmountPlanInfoAreaPanel.setBorder(new TitledBorder("Ausgabenplanungsinfo"));
		eventExpenditureAmountPlanInfoAreaPanel.add(eventExpenditureAmountPlanInfoAreaScrollPane);

		eventInfoAreaAccountClosing = new JTextArea();
		eventInfoAreaAccountClosing.setLineWrap(true);
        eventInfoAreaAccountClosing.setWrapStyleWord(true);
        eventInfoAreaAccountClosing.setEditable(true);
		eventInfoAreaAccountClosing.setEnabled(false);

        eventInfoAreaAccountClosingScrollPane = new JScrollPane(eventInfoAreaAccountClosing);
        eventInfoAreaAccountClosingScrollPane.setPreferredSize(new Dimension(200, 70));

		eventInfoAreaAccountClosingPanel = new JPanel();
        eventInfoAreaAccountClosingPanel.setBorder(new TitledBorder("Kategorie Abschlussinfo"));
		eventInfoAreaAccountClosingPanel.add(eventInfoAreaAccountClosingScrollPane);

		sumOverviewPanel = new JPanel();
		sumOverviewPanel.setBorder(new TitledBorder("Saldo"));
		sumOverviewPanel.setPreferredSize(new Dimension(240, 100));

		sumOverviewNegativeLabel = new JLabel("Summe -");
		sumOverviewPositiveLabel = new JLabel("Summe +");
		sumOverviewPlanedLabel = new JLabel("geplant");
		sumOverviewUnplanedLabel = new JLabel("ungeplant");

		/*
		 * in the Member Field Name of the JFormattedTextfield we save, in the Moment that the Value will placed,
		 * the Array of the IDs from the Detail Table whose difference Values build the Sum
		 */
		sumOverviewNegativePlanedValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		sumOverviewNegativePlanedValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		sumOverviewNegativePlanedValue.setEditable(false);
		sumOverviewNegativePlanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewNegativePlanedValue.setFont(fontTxtFields);
		sumOverviewNegativePlanedValue.setText("0,00");
		sumOverviewNegativePlanedValue.setName("{}");

		sumOverviewNegativeUnplanedValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		sumOverviewNegativeUnplanedValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		sumOverviewNegativeUnplanedValue.setEditable(false);
		sumOverviewNegativeUnplanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewNegativeUnplanedValue.setFont(fontTxtFields);
		sumOverviewNegativeUnplanedValue.setText("0,00");
		sumOverviewNegativeUnplanedValue.setName("{}");

		sumOverviewPositivePlanedValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		sumOverviewPositivePlanedValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		sumOverviewPositivePlanedValue.setEditable(false);
		sumOverviewPositivePlanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewPositivePlanedValue.setFont(fontTxtFields);
		sumOverviewPositivePlanedValue.setText("0,00");
		sumOverviewPositivePlanedValue.setName("{}");

		sumOverviewPositiveUnplanedValue = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		sumOverviewPositiveUnplanedValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		sumOverviewPositiveUnplanedValue.setEditable(false);
		sumOverviewPositiveUnplanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewPositiveUnplanedValue.setFont(fontTxtFields);
		sumOverviewPositiveUnplanedValue.setText("0,00");
		sumOverviewPositiveUnplanedValue.setName("{}");

		jointAccountClosingBalanceAllocationOverviewDetailTable = new JTable(new JointAccountClosingBalanceAllocationOverviewDetailTableModel());
		jointAccountClosingBalanceAllocationOverviewDetailTable.setDefaultRenderer(Object.class, new JointAccountClosingBalanceAllocationOverviewDetailTableCellRenderer());

		jointAccountClosingBalanceAllocationOverviewScrollPane = new JScrollPane(jointAccountClosingBalanceAllocationOverviewDetailTable);
		jointAccountClosingBalanceAllocationOverviewScrollPane.setPreferredSize(new Dimension(220, 90));

		balanceAllocationOverviewPanel = new JPanel();
		balanceAllocationOverviewPanel.setBorder(new TitledBorder("Aufteilung Saldo"));

		closeBillingMonth = new JButton("Monat abschließen!");
		// inital the Button has to disabled because only if a month is alrady open
		// we can close them
		closeBillingMonth.setEnabled(false);
    }

	private void createListeners() {
		billingMonth.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent ke) {
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER
						&& Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", billingMonth.getText())) {
					if (existAccountClosingData(billingMonth.getText())) {
						// we have to clear the IDs that for marking difference Values in the Table
						((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
								.setDetailIdsForMarkingDifferenceValue(new Integer[0]);

						((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
								.aktualisiere(billingMonth.getText());

						// now we can fill the sumOverview if there was stored Values before
						fillSumOverview();

						billingMonthAlreadyClosed = isBillingMonthAlreadyClosed(billingMonth.getText());

						// if the Sum Overview is filled we can calculate the Data for the ballance
						// allocation overview and display it, but we give to function isBillingMonthAlradyClosed
						fillBallaceAllocationOverview();
					} else {
						int result = JOptionPane.showConfirmDialog(null, "Es sind noch keine Daten für den Abrechnungsmontat zusammengestellt worden!\nSoll das jetzt gemacht werden?", "Abschluss initieren", JOptionPane.YES_NO_CANCEL_OPTION);

						if (result == JOptionPane.YES_OPTION) {
							prepareJointAccountClosingDetailData(billingMonth.getText());
						} else if (result == JOptionPane.NO_OPTION) {
							jointAccountClosingWindow.dispose();
						}
					}


				}
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}
		});

		closeBillingMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// We need a closing comment (in the current state of the program, it is stored in each party's closing record => later we can store it in a separate table... maybe)
				String closingComment = JOptionPane.showInputDialog("Bitte einen Abschlusskommentar eingeben:", "normale Aufteilung und jeder zahlt seinen Betrag nach");

				StringBuilder jointAccountClosingBalanceAllocationOverviewDetailTableImportData = new StringBuilder();
				// first get the Values from the Tabel Model that Values already is displayed
				List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> jointAccountClosingBalanceAllocationOverviewDetailTableData = ((JointAccountClosingBalanceAllocationOverviewDetailTableModel) jointAccountClosingBalanceAllocationOverviewDetailTable
						.getModel()).getTableData();

				for (JointAccountClosingBalanceAllocationOverviewDetailTableRow jointAccountClosingBalanceAllocationOverviewDetailTableDataRow : jointAccountClosingBalanceAllocationOverviewDetailTableData) {
					jointAccountClosingBalanceAllocationOverviewDetailTableImportData.append("("
							+ jointAccountClosingBalanceAllocationOverviewDetailTableDataRow.partyId() + "," +
							jointAccountClosingBalanceAllocationOverviewDetailTableDataRow.shareInPercent() + "," +
							jointAccountClosingBalanceAllocationOverviewDetailTableDataRow.finalShare() + ",'" +
							billingMonth.getText() + "'," +
							"CURRENT_TIMESTAMP, '" +
							closingComment + "'),");
				}

				// delete the Last commata from the String that represent the Import Values for the Import Statement
				jointAccountClosingBalanceAllocationOverviewDetailTableImportData.delete(jointAccountClosingBalanceAllocationOverviewDetailTableImportData.length() - 1, jointAccountClosingBalanceAllocationOverviewDetailTableImportData.length());



			}
		});
	}

	private void registerExistingListeners() {
		jointAccountClosingDetailTable.getSelectionModel()
				.addListSelectionListener(new JointAccountClosingDetailTableSelectionListener(
						jointAccountClosingDetailTable, eventExpenditureAmountPlanInfoArea, cn,
						eventInfoAreaAccountClosing, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue,
						sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue));

		eventInfoAreaAccountClosing
				.addKeyListener(new JointAccountClosingEventInfoAreaKeyListener(cn, jointAccountClosingDetailTable));

		jointAccountClosingDetailTable.addMouseListener(new JointAccountClosingDetailTableMouseListener(
				jointAccountClosingDetailTable, billingMonth, cn, this));

		sumOverviewMouseListener = new JointAccountClosingSumOverviewMouseListener(billingMonth,
				jointAccountClosingDetailTable, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue,
				sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue, cn);
		sumOverviewNegativePlanedValue.addMouseListener(sumOverviewMouseListener);
		sumOverviewNegativeUnplanedValue.addMouseListener(sumOverviewMouseListener);
		sumOverviewPositivePlanedValue.addMouseListener(sumOverviewMouseListener);
		sumOverviewPositiveUnplanedValue.addMouseListener(sumOverviewMouseListener);
	}

    private void  createLayout() {
		// ---
		GridBagLayout jointAccountClosingDetailPanelLayout = new GridBagLayout();
		GridBagConstraints jointAccountClosingDetailPanelLayoutConstraints = new GridBagConstraints();
		jointAccountClosingDetailPanel.setLayout(jointAccountClosingDetailPanelLayout);

		jointAccountClosingDetailPanelLayoutConstraints.gridx = 0;
		jointAccountClosingDetailPanelLayoutConstraints.gridy = 0;
		jointAccountClosingDetailPanelLayoutConstraints.fill = GridBagConstraints.BOTH;
		jointAccountClosingDetailPanel.add(jointAccountClosingDetailScrollPane, jointAccountClosingDetailPanelLayoutConstraints);
		// ---

		// ---
		GridBagLayout sumOverviewLayout = new GridBagLayout();
		GridBagConstraints sumOverviewLayoutConstraints = new GridBagConstraints();
		sumOverviewPanel.setLayout(sumOverviewLayout);

		sumOverviewLayoutConstraints.gridx = 1;
		sumOverviewLayoutConstraints.gridy = 0;
		sumOverviewLayoutConstraints.anchor = GridBagConstraints.WEST;
		sumOverviewPanel.add(sumOverviewPlanedLabel, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 2;
		sumOverviewLayoutConstraints.gridy = 0;
		sumOverviewLayoutConstraints.anchor = GridBagConstraints.WEST;
		sumOverviewPanel.add(sumOverviewUnplanedLabel, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 0;
		sumOverviewLayoutConstraints.gridy = 1;
		sumOverviewLayoutConstraints.anchor = GridBagConstraints.WEST;
		sumOverviewLayoutConstraints.insets = new Insets(0, 0, 0, 5);
		sumOverviewPanel.add(sumOverviewNegativeLabel, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 1;
		sumOverviewLayoutConstraints.gridy = 1;
		sumOverviewPanel.add(sumOverviewNegativePlanedValue, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 2;
		sumOverviewLayoutConstraints.gridy = 1;
		sumOverviewLayoutConstraints.insets = new Insets(0, 0, 0, 0);
		sumOverviewPanel.add(sumOverviewNegativeUnplanedValue, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 0;
		sumOverviewLayoutConstraints.gridy = 2;
		sumOverviewLayoutConstraints.anchor = GridBagConstraints.WEST;
		sumOverviewLayoutConstraints.insets = new Insets(0, 0, 0, 5);
		sumOverviewPanel.add(sumOverviewPositiveLabel, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 1;
		sumOverviewLayoutConstraints.gridy = 2;
		sumOverviewPanel.add(sumOverviewPositivePlanedValue, sumOverviewLayoutConstraints);

		sumOverviewLayoutConstraints.gridx = 2;
		sumOverviewLayoutConstraints.gridy = 2;
		sumOverviewLayoutConstraints.insets = new Insets(0, 0, 0, 0);
		sumOverviewPanel.add(sumOverviewPositiveUnplanedValue, sumOverviewLayoutConstraints);
		// ---

		// ---
		GridBagLayout balanceAllocationOverviewLayout = new GridBagLayout();
		GridBagConstraints balanceAllocationOverviewLayoutConstraints = new GridBagConstraints();
		balanceAllocationOverviewPanel.setLayout(balanceAllocationOverviewLayout);

		balanceAllocationOverviewLayoutConstraints.gridx = 0;
		balanceAllocationOverviewLayoutConstraints.gridy = 0;
		balanceAllocationOverviewPanel.add(jointAccountClosingBalanceAllocationOverviewScrollPane, sumOverviewLayoutConstraints);
		// ---

		// ---
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        jointAccountClosingWindow.setLayout(gbl);

        gbc.gridx = 0;
        gbc.gridy = 0;
        // gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        // gbc.insets = new Insets(10, 10, 0, 0);
        jointAccountClosingWindow.add(billingMonthPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		jointAccountClosingWindow.add(jointAccountClosingDetailPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		jointAccountClosingWindow.add(eventExpenditureAmountPlanInfoAreaPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.EAST;
		jointAccountClosingWindow.add(eventInfoAreaAccountClosingPanel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		//gbc.anchor = GridBagConstraints.NORTH;
		jointAccountClosingWindow.add(sumOverviewPanel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		//gbc.anchor = GridBagConstraints.NORTH;
		jointAccountClosingWindow.add(balanceAllocationOverviewPanel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.insets = new Insets(30, 30, 30, 30);
		jointAccountClosingWindow.add(closeBillingMonth, gbc);

		// reset insets
		gbc.insets = new Insets(0, 0, 0, 0);
		// ---

    }

	public void fillSumOverview() {
		// first we set the values to 0
		sumOverviewNegativePlanedValue.setText("0,00");
		sumOverviewNegativeUnplanedValue.setText("0,00");
		sumOverviewPositivePlanedValue.setText("0,00");
		sumOverviewPositiveUnplanedValue.setText("0,00");
		sumOverviewNegativePlanedValue.setName("{}");
		sumOverviewNegativeUnplanedValue.setName("{}");
		sumOverviewPositivePlanedValue.setName("{}");
		sumOverviewPositiveUnplanedValue.setName("{}");

		// and reset all formatting
		sumOverviewNegativePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewNegativeUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewPositivePlanedValue.setFont(new Font(null, Font.PLAIN, 14));
        sumOverviewPositiveUnplanedValue.setFont(new Font(null, Font.PLAIN, 14));

		DBTools getter = new DBTools(cn);

		getter.select("""
				SELECT "summenArt", sum(ha_abschlussdetails.differenz) betrag
				FROM ha_abschlusssummen, ha_abschlussdetails
				WHERE ha_abschlussdetails."abschlussMonat" = '%s'
				AND ha_abschlussdetails."abschlussDetailId" = ha_abschlusssummen."abschlussDetailId"
				GROUP BY "summenArt"
				""".formatted(billingMonth.getText()), 2);

		try {
			getter.beforeFirst();

			while (getter.next()) {
				switch (getter.getString("summenArt")) {
					case "planned+":
						sumOverviewPositivePlanedValue.setText("%.02f".formatted(getter.getDouble("betrag")));
						sumOverviewPositivePlanedValue.setName(getter.getString("summenArt"));
						break;
					case "planned-":
						sumOverviewNegativePlanedValue.setText("%.02f".formatted(getter.getDouble("betrag")));
						sumOverviewNegativePlanedValue.setName(getter.getString("summenArt"));
						break;
					case "unplanned+":
						sumOverviewPositiveUnplanedValue.setText("%.02f".formatted(getter.getDouble("betrag")));
						sumOverviewPositiveUnplanedValue.setName(getter.getString("summenArt"));
						break;
					case "unplanned-":
						sumOverviewNegativeUnplanedValue.setText("%.02f".formatted(getter.getDouble("betrag")));
						sumOverviewNegativeUnplanedValue.setName(getter.getString("summenArt"));
						break;
					default:
						break;
				}
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "+e.getStackTrace()[0].getLineNumber()+"): " + e.toString());
		}
	}

	public void fillBallaceAllocationOverview() {
		/**
		 * ToDo
		 * isBillingMonthAlreadyClosed
		 * if not =>
		 * We determine the percentage of income for the defined liquid month.
		 * Then, using these values ​​and the totals from the overview, we calculate
		 * the correct amounts per person that must be deposited into the
		 * joint account or paid out to the parties.
		 * if it so =>
		 * we get the values for the Model from the table ha_abschlusssummen_aufteilung
		 */
		List<SalaryBasesSumOfIncomePerPartyTableRow> salaryBasesForTheDefinedBillingMonth = new ArrayList<>();
		List<JointAccountClosingBalanceAllocationOverviewDetailTableRow> jointAccountClosingBalanceAllocationOverviewDetailTableData = new ArrayList<>();
		Double sumOfAllIncome = 0.0;
		Double totalSumToBeDivided = 0.0;
		DBTools dbTools = new DBTools(cn);

		if (!billingMonthAlreadyClosed) {

			dbTools.select("""
				SELECT p.personen_id, p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as party, sum(gg.betrag) as betrag
				FROM personen p, ha_gehaltsgrundlagen gg
				WHERE gilt_bis >= '%s'
				AND gilt_ab <= '%s'
				AND p.personen_id = gg.partei_id
				GROUP BY p.personen_id, p.name, p.vorname
				ORDER BY p.name;
				""".formatted(billingMonth.getText(), billingMonth.getText()),
				3);

			try {
				dbTools.beforeFirst();

				while (dbTools.next()) {
					salaryBasesForTheDefinedBillingMonth
							.add(new SalaryBasesSumOfIncomePerPartyTableRow(dbTools.getInt("personen_id"), dbTools.getString("party"),
									dbTools.getDouble("betrag")));
				}
			} catch (Exception e) {
				System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
						+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
			}

			// first calculate the total sum of the Partys in this billngmonth
			for (SalaryBasesSumOfIncomePerPartyTableRow Zeile : salaryBasesForTheDefinedBillingMonth) {
				sumOfAllIncome = sumOfAllIncome + Zeile.Sum();
			}

			// next we calculate the total Sum of the 4 SumOverview Values
			dbTools.select("""
					SELECT sum(ha_abschlussdetails.differenz) AmountToBeDivided
					FROM ha_abschlusssummen, ha_abschlussdetails
					WHERE ha_abschlussdetails."abschlussMonat" = '%s'
					AND ha_abschlussdetails."abschlussDetailId" = ha_abschlusssummen."abschlussDetailId"
					""".formatted(billingMonth.getText(), billingMonth.getText()), 1);

			try {
				dbTools.first();
				totalSumToBeDivided = dbTools.getDouble("AmountToBeDivided");
			} catch (Exception e) {
				System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
						+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
			}

			// at last we ca put the data, with calculated percentvalue and share of the
			// Parties, to the nessasary record
			for (SalaryBasesSumOfIncomePerPartyTableRow incomePerPartyThisBillingMonthRow : salaryBasesForTheDefinedBillingMonth) {
				jointAccountClosingBalanceAllocationOverviewDetailTableData
						.add(new JointAccountClosingBalanceAllocationOverviewDetailTableRow(
								incomePerPartyThisBillingMonthRow.partyId(),
								incomePerPartyThisBillingMonthRow.Name(),
								(incomePerPartyThisBillingMonthRow.Sum() * 100) / sumOfAllIncome,
								((incomePerPartyThisBillingMonthRow.Sum()) / sumOfAllIncome) * totalSumToBeDivided));
			}

			// now we can enable the closing Button
			closeBillingMonth.setEnabled(true);
		} else {
			dbTools.select("""
					SELECT "parteiId", p.name || ', ' || SUBSTRING(p.vorname, 1, 1) || '.' as partei, "abschlussAnteilInProzent", "abschlussAnteilBetrag"
					FROM ha_abschlusssummen_aufteilung, personen p
					WHERE "abschlussMonat" = '%s'
					AND ha_abschlusssummen_aufteilung."parteiId" = p.personen_id
					""".formatted(billingMonth.getText()), 0);

			try {
				dbTools.beforeFirst();

				while (dbTools.next()) {
					jointAccountClosingBalanceAllocationOverviewDetailTableData
						.add(new JointAccountClosingBalanceAllocationOverviewDetailTableRow(
								dbTools.getInt("parteiId"),
								dbTools.getString("partei"),
								dbTools.getDouble("abschlussAnteilInProzent"),
								dbTools.getDouble("abschlussAnteilBetrag")));
				}
			} catch (Exception e) {
				System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
						+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
			}
		}

		// regardless of where the data for the record came from, now we can put tehm in the TableModel
		((JointAccountClosingBalanceAllocationOverviewDetailTableModel) jointAccountClosingBalanceAllocationOverviewDetailTable
				.getModel()).aktualisiere(jointAccountClosingBalanceAllocationOverviewDetailTableData);
	}

	private boolean existAccountClosingData(String billingMonth) {
		DBTools getter = new DBTools(cn);
		int rowCountOfAcountClosingDetails = 0;

		getter.select("""
				SELECT COUNT(*)
				FROM ha_abschlussdetails
				WHERE "abschlussMonat" = '%s'
				""".formatted(billingMonth), 1);
		try {
			getter.first();
			rowCountOfAcountClosingDetails = getter.getInt("COUNT");
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}

		if (rowCountOfAcountClosingDetails > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void prepareJointAccountClosingDetailData(String billingMonth) {
		DBTools dbTool = new DBTools(cn);
		List<JointAccountClosingDetailTableRow> jointAccountDetailData = new ArrayList<>();
		Double planedValue = 0.0;

		// first put the income Values in a temporary Table
		dbTool.insert("""
				SELECT kategoriebezeichnung, SUM(tHaben.betrag) INTO TEMP closedetail FROM transaktionen tHaben, ha_kategorie
				WHERE tHaben.konten_id = 13
				AND tHaben.soll_haben = 'h'
				AND tHaben.liqui_monat = '%s'
				AND tHaben.ereigniss_id = ha_kategorie.ha_kategorie_id
				GROUP BY ha_kategorie.kategoriebezeichnung
				ORDER BY ha_kategorie.kategoriebezeichnung ASC
				""".formatted(billingMonth));

		// the came the expenditure
		dbTool.insert("""
				INSERT INTO closedetail
				SELECT kategoriebezeichnung, SUM(tSoll.betrag * -1) FROM transaktionen tSoll, ha_kategorie
				WHERE tSoll.konten_id = 13
				AND tSoll.soll_haben = 's'
				AND tSoll.liqui_monat = '%s'
				AND tSoll.ereigniss_id = ha_kategorie.ha_kategorie_id
				GROUP BY ha_kategorie.kategoriebezeichnung
				ORDER BY ha_kategorie.kategoriebezeichnung ASC
				""".formatted(billingMonth));

		// group the incomes and expenditure Values from the temp table
		dbTool.select("""
				SELECT kategoriebezeichnung, SUM(sum)
				FROM closedetail
				GROUP BY kategoriebezeichnung
				ORDER BY 1
				""", 1);

		// and put them into a local record
		try {
			dbTool.beforeFirst();

			while (dbTool.next()) {
				planedValue = getPlanedValueForCategoryInBillingMonth(dbTool.getString("kategoriebezeichnung"), billingMonth);

				jointAccountDetailData.add(
						new JointAccountClosingDetailTableRow(0, dbTool.getString("kategoriebezeichnung"),
								dbTool.getDouble("sum"), (planedValue * -1), dbTool.getDouble("sum") - (planedValue * -1)));
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}

		// drop the temp table after we save the data in to our own record
		dbTool.update("DROP TABLE closedetail");

		for (JointAccountClosingDetailTableRow row : jointAccountDetailData) {
			System.out.println(row.nameOfExpenditure()+"/"+row.actualAmount()+"/"+row.planAmount()+"/"+row.difference());
		}
	}

	private Double getPlanedValueForCategoryInBillingMonth(String categoryName, String billingMonth) {
		DBTools dbTool = new DBTools(cn);
		Double planedValue = 0.0;

		dbTool.select("""
				SELECT betrag
				FROM ha_ausgaben
				WHERE gilt_ab <= '%s'
				AND gilt_bis >= '%s'
				AND bezeichnung = '%s'
				""".formatted(billingMonth, billingMonth, categoryName), 1);

		try {
			if (dbTool.getRowCount() > 0) {
				dbTool.first();
				planedValue = dbTool.getDouble("betrag");
			} else {
				// if nothing found we returned the initial Value 0.0
				return planedValue;
			}


		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}

		return planedValue;
	}

	private boolean isBillingMonthAlreadyClosed(String billingMonth) {
		DBTools dbTool = new DBTools(cn);
		boolean billingMonthIsClosed = false;

		dbTool.select("""
				SELECT count(*)
				FROM ha_abschlusssummen_aufteilung
				WHERE "abschlussMonat" = '%s'
				""".formatted(billingMonth), 1);

		try {
			dbTool.first();
			billingMonthIsClosed = (dbTool.getInt("count") > 0) ? true : false;
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + "/" + e.getStackTrace()[2].getMethodName() + " (Line: "
					+ e.getStackTrace()[0].getLineNumber() + "): " + e.toString());
		}

		return billingMonthIsClosed;
	}

	public boolean getBillingMonthAlreadyClosed() {
		return billingMonthAlreadyClosed;
	}
}
