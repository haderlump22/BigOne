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
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import de.rachel.bigone.listeners.JointAccountClosingEventInfoAreaKeyListener;
import de.rachel.bigone.listeners.JointAccountClosingDetailTableSelectionListener;
import de.rachel.bigone.models.JointAccountClosingDetailTableModel;
import de.rachel.bigone.renderer.JointAccountClosingDetailTableCellRenderer;

public class JointAccountClosing {
	private Connection cn = null;
	private JFrame jointAccountClosingWindow;
	private Font fontTxtFields;// , fontCmbBoxes, fontLists;
	private JPanel billingMonthPanel, jointAccountClosingDetailPanel, eventExpenditureAmountPlanInfoAreaPanel,
			eventInfoAreaAccountClosingPanel, sumOverviewPanel, balanceAllocationOverviewPanel;
	private JFormattedTextField billingMonth, sumOverviewPositivePlanedValue, sumOverviewPositiveUnplanedValue, sumOverviewNegativePlanedValue, sumOverviewNegativeUnplanedValue;
	private JTable jointAccountClosingDetailTable;
	private JScrollPane jointAccountClosingDetailScrollPane, eventExpenditureAmountPlanInfoAreaScrollPane,
			eventInfoAreaAccountClosingScrollPane;
	private JTextArea eventExpenditureAmountPlanInfoArea, eventInfoAreaAccountClosing;
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
        jointAccountClosingWindow.setVisible(true);
    }

    private void createComponents() {
		jointAccountClosingWindow = new JFrame("Haushaltskontoabschluss");
		jointAccountClosingWindow.setSize(900, 680);
		jointAccountClosingWindow.setLocation(200, 200);
		jointAccountClosingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jointAccountClosingWindow.setResizable(true);

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

		balanceAllocationOverviewPanel = new JPanel();
		balanceAllocationOverviewPanel.setBorder(new TitledBorder("Aufteilung"));
		balanceAllocationOverviewPanel.setPreferredSize(new Dimension(240, 100));

		// balanceAllocationOverviewNegativeLabel = new JLabel("Summe -");
		// balanceAllocationOverviewPositiveLabel = new JLabel("Summe +");
		// balanceAllocationOverviewPlanedLabel = new JLabel("geplant");
		// balanceAllocationOverviewUnplanedLabel = new JLabel("ungeplant");
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
					((JointAccountClosingDetailTableModel) jointAccountClosingDetailTable.getModel())
							.aktualisiere(billingMonth.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}
		});

		// Listeners for the JointAccountClosingDetailTable
		jointAccountClosingDetailTable.getSelectionModel().addListSelectionListener(new JointAccountClosingDetailTableSelectionListener(
			jointAccountClosingDetailTable, eventExpenditureAmountPlanInfoArea, cn, billingMonth, eventInfoAreaAccountClosing));

		// Listener for the EventInfoAreaAccountClosingPanel
		eventInfoAreaAccountClosing.addKeyListener(new JointAccountClosingEventInfoAreaKeyListener(cn, billingMonth, jointAccountClosingDetailTable));
	}

    private void createLayout() {
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
}
