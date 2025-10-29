package de.rachel.bigone;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

		jointAccountClosingBalanceAllocationOverviewDetailTable = new JTable(new JointAccountClosingBalanceAllocationOverviewDetailTableModel(cn));
		jointAccountClosingBalanceAllocationOverviewDetailTable.setDefaultRenderer(Object.class, new JointAccountClosingBalanceAllocationOverviewDetailTableCellRenderer());

		jointAccountClosingBalanceAllocationOverviewScrollPane = new JScrollPane(jointAccountClosingBalanceAllocationOverviewDetailTable);
		jointAccountClosingBalanceAllocationOverviewScrollPane.setPreferredSize(new Dimension(220, 90));

		balanceAllocationOverviewPanel = new JPanel();
		balanceAllocationOverviewPanel.setBorder(new TitledBorder("Aufteilung Saldo"));
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
					((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
							.aktualisiere(billingMonth.getText());

					// now we can fill the sumOverview if there was stored Values before
					fillSumOverview();

					// if the Sum Overview ist filled we can calculate the Data for the ballance allocation overview and display it
					fillBallaceAllocationOverview();
				}
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}
		});
	}

	private void registerExistingListeners() {
		jointAccountClosingDetailTable.getSelectionModel().addListSelectionListener(new JointAccountClosingDetailTableSelectionListener(
			jointAccountClosingDetailTable, eventExpenditureAmountPlanInfoArea, cn, eventInfoAreaAccountClosing));

		eventInfoAreaAccountClosing.addKeyListener(new JointAccountClosingEventInfoAreaKeyListener(cn, jointAccountClosingDetailTable));

		jointAccountClosingDetailTable.addMouseListener(new JointAccountClosingDetailTableMouseListener(jointAccountClosingDetailTable, billingMonth, cn, this));

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

	private void fillBallaceAllocationOverview() {
		/**
		 * ToDo
		 * get for the defines Liquimonth the part of income in Percent
		 * then calculate with theese and the sumOverview Values the correct Values per Person
		 */
	}
}
