package de.rachel.bigone;

import java.sql.Connection;
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
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import de.rachel.bigone.listeners.JointAccountClosingEventInfoAreaKeyListener;
import de.rachel.bigone.listeners.JointAccountClosingDetailTableSelectionListener;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;
import de.rachel.bigone.renderer.JointAccountClosingDetailTableCellRenderer;

public class JointAccountClosing {
	private Connection cn = null;
	private JFrame JointAccountClosingWindow;
	private Font fontTxtFields;// , fontCmbBoxes, fontLists;
	private JPanel billingMonthPanel, JointAccountClosingDetailPanel, EventExpenditureAmountPlanInfoAreaPanel,
			EventInfoAreaAccountClosingPanel, sumOverviewPanel, excessDeficitOverviewPanel, container;
	private JFormattedTextField billingMonth, sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue;
	private JTable JointAccountClosingDetailTable;
	private JScrollPane JointAccountClosingDetailScrollPane, EventExpenditureAmountPlanInfoAreaScrollPane,
			EventInfoAreaAccountClosingScrollPane;
	private JTextArea EventExpenditureAmountPlanInfoArea, EventInfoAreaAccountClosing;
	private JLabel sumOverviewNegativeLabel, sumOverviewPositiveLabel, sumOverviewPlanedLabel, sumOverviewUnplanedLabel;

    JointAccountClosing (Connection LoginCN) {
		cn = LoginCN;

        // create Components
        this.createComponents();

		// create Component Listeners
		this.createListeners();

        // Layouting
        this.createLayout();

        // showing
        JointAccountClosingWindow.setVisible(true);
    }

    private void createComponents() {
		JointAccountClosingWindow = new JFrame("Haushaltskontoabschluss");
		JointAccountClosingWindow.setSize(900, 680);
		JointAccountClosingWindow.setLocation(200, 200);
		JointAccountClosingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JointAccountClosingWindow.setResizable(true);

		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
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

		JointAccountClosingDetailTable = new JTable(new JointAccountClosingDetailTableModel(cn));
		JointAccountClosingDetailTable.setDefaultRenderer(Object.class, new JointAccountClosingDetailTableCellRenderer());

		// setting width for some columns
		// has to do

		JointAccountClosingDetailScrollPane = new JScrollPane(JointAccountClosingDetailTable);
		JointAccountClosingDetailScrollPane.setPreferredSize(new Dimension(480, 150));

		JointAccountClosingDetailPanel = new JPanel();
		JointAccountClosingDetailPanel.setBorder(new TitledBorder("Ausgabensummen"));
		JointAccountClosingDetailPanel.add(JointAccountClosingDetailScrollPane);

		EventExpenditureAmountPlanInfoArea = new JTextArea();
		EventExpenditureAmountPlanInfoArea.setLineWrap(true);
        EventExpenditureAmountPlanInfoArea.setWrapStyleWord(true);
        EventExpenditureAmountPlanInfoArea.setEditable(false);

        EventExpenditureAmountPlanInfoAreaScrollPane = new JScrollPane(EventExpenditureAmountPlanInfoArea);
        EventExpenditureAmountPlanInfoAreaScrollPane.setPreferredSize(new Dimension(200, 70));

		EventExpenditureAmountPlanInfoAreaPanel = new JPanel();
        EventExpenditureAmountPlanInfoAreaPanel.setBorder(new TitledBorder("Ausgabenplanungsinfo"));
		EventExpenditureAmountPlanInfoAreaPanel.add(EventExpenditureAmountPlanInfoAreaScrollPane);

		EventInfoAreaAccountClosing = new JTextArea();
		EventInfoAreaAccountClosing.setLineWrap(true);
        EventInfoAreaAccountClosing.setWrapStyleWord(true);
        EventInfoAreaAccountClosing.setEditable(true);
		EventInfoAreaAccountClosing.setEnabled(false);

        EventInfoAreaAccountClosingScrollPane = new JScrollPane(EventInfoAreaAccountClosing);
        EventInfoAreaAccountClosingScrollPane.setPreferredSize(new Dimension(200, 70));

		EventInfoAreaAccountClosingPanel = new JPanel();
        EventInfoAreaAccountClosingPanel.setBorder(new TitledBorder("Kategorie Abschlussinfo"));
		EventInfoAreaAccountClosingPanel.add(EventInfoAreaAccountClosingScrollPane);

		sumOverviewPanel = new JPanel();
		sumOverviewPanel.setBorder(new TitledBorder("Saldo"));
		sumOverviewPanel.setPreferredSize(new Dimension(240, 100));

		sumOverviewNegativeLabel = new JLabel("Summe -");
		sumOverviewPositiveLabel = new JLabel("Summe +");
		sumOverviewPlanedLabel = new JLabel("geplant");
		sumOverviewUnplanedLabel = new JLabel("ungeplant");

		sumOverviewNegativePlanedValue = new JFormattedTextField();
		sumOverviewNegativePlanedValue.setEditable(false);
		sumOverviewNegativePlanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewNegativePlanedValue.setFont(fontTxtFields);

		sumOverviewNegativeUnplanedValue = new JFormattedTextField();
		sumOverviewNegativeUnplanedValue.setEditable(false);
		sumOverviewNegativeUnplanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewNegativeUnplanedValue.setFont(fontTxtFields);

		sumOverviewPositivePlanedValue = new JFormattedTextField();
		sumOverviewPositivePlanedValue.setEditable(false);
		sumOverviewPositivePlanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewPositivePlanedValue.setFont(fontTxtFields);

		sumOverviewPositiveUnplanedValue = new JFormattedTextField();
		sumOverviewPositiveUnplanedValue.setEditable(false);
		sumOverviewPositiveUnplanedValue.setPreferredSize(new Dimension(70, 25));
		sumOverviewPositiveUnplanedValue.setFont(fontTxtFields);

		excessDeficitOverviewPanel = new JPanel();
		excessDeficitOverviewPanel.setBorder(new TitledBorder("Aufteilung Überschuss/Fehlbetrag"));
		excessDeficitOverviewPanel.setPreferredSize(new Dimension(300, 100));

		container = new JPanel();
		container.setPreferredSize(new Dimension(300, 200));
		container.add(sumOverviewPanel);
		container.add(excessDeficitOverviewPanel);
    }

	private void createListeners() {
		// Listener for the textfield
		billingMonth.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent ke) {
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER
						&& Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", billingMonth.getText())) {
					((JointAccountClosingDetailTableModel) JointAccountClosingDetailTable.getModel())
							.aktualisiere(billingMonth.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}
		});

		// Listeners for the JointAccountClosingDetailTable
		JointAccountClosingDetailTable.getSelectionModel().addListSelectionListener(new JointAccountClosingDetailTableSelectionListener(
			JointAccountClosingDetailTable, EventExpenditureAmountPlanInfoArea, cn, billingMonth, EventInfoAreaAccountClosing));

		// Listener for the EventInfoAreaAccountClosingPanel
		EventInfoAreaAccountClosing.addKeyListener(new JointAccountClosingEventInfoAreaKeyListener(cn, billingMonth, JointAccountClosingDetailTable));
	}

    private void createLayout() {
		// layout sumOverview START
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
		// layout sumOverview END


		// layout rootdefinitions
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JointAccountClosingWindow.setLayout(gbl);

        // place billingMonthPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        // gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        // gbc.insets = new Insets(10, 10, 0, 0);
        JointAccountClosingWindow.add(billingMonthPanel, gbc);

		// place JointAccountClosingDetailPanel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		JointAccountClosingWindow.add(JointAccountClosingDetailPanel, gbc);

		// place EventExpenditureAmountPlanInfoAreaPanel
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		JointAccountClosingWindow.add(EventExpenditureAmountPlanInfoAreaPanel, gbc);

		// place EventExpenditureAmountPlanInfoAreaPanel
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		JointAccountClosingWindow.add(EventInfoAreaAccountClosingPanel, gbc);

		// place sumOverviewPanel
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		JointAccountClosingWindow.add(container, gbc);



    }
}
