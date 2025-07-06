package de.rachel.bigone;

import java.sql.Connection;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
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
    private JFrame JointAccountClosingWindow;
    private Font fontTxtFields;//, fontCmbBoxes, fontLists;
    private JPanel pnlBillingMonth, JointAccountClosingDetailPanel, EventExpenditureAmountPlanInfoAreaPanel, EventInfoAreaAccountClosingPanel;
    private JFormattedTextField BillingMonth;
	private JTable JointAccountClosingDetailTable;
	private JScrollPane JointAccountClosingDetailScrollPane, EventExpenditureAmountPlanInfoAreaScrollPane, EventInfoAreaAccountClosingScrollPane;
	private JTextArea EventExpenditureAmountPlanInfoArea, EventInfoAreaAccountClosing;

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

		// Font settings
		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
		// fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);
		// fontLists = new Font("Arial", Font.PLAIN, 10);

		// ====START Month of AccountClosing====
		pnlBillingMonth = new JPanel();
		pnlBillingMonth.setPreferredSize(new Dimension(150, 60));
		pnlBillingMonth.setBorder(new TitledBorder("Abrechnungsmonat"));

		// create Content for the Panel in shape of a txtField
		try {
			BillingMonth = new JFormattedTextField(new MaskFormatter("01-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		BillingMonth.setPreferredSize(new Dimension(110, 25));
		BillingMonth.setHorizontalAlignment(JFormattedTextField.RIGHT);
		BillingMonth.setFont(fontTxtFields);

		// put the textfield to the Panal
		pnlBillingMonth.add(BillingMonth);
		// ====END Month of AccountClosing====

		// ====START JointAccountClosingDetailTable and related Components====
		JointAccountClosingDetailTable = new JTable(new JointAccountClosingDetailTableModel(cn));
		JointAccountClosingDetailTable.setDefaultRenderer(Object.class, new JointAccountClosingDetailTableCellRenderer());

		// setting width for some columns
		// has to do

		JointAccountClosingDetailScrollPane = new JScrollPane(JointAccountClosingDetailTable);
		JointAccountClosingDetailScrollPane.setPreferredSize(new Dimension(480, 150));

		JointAccountClosingDetailPanel = new JPanel();
		JointAccountClosingDetailPanel.setBorder(new TitledBorder("Ausgabensummen"));
		JointAccountClosingDetailPanel.add(JointAccountClosingDetailScrollPane);
		// ====END JointAccountClosingDetailTable====

		// ===START EventExpenditureAmountPlanInfoArea
		EventExpenditureAmountPlanInfoArea = new JTextArea();
		EventExpenditureAmountPlanInfoArea.setLineWrap(true);
        EventExpenditureAmountPlanInfoArea.setWrapStyleWord(true);
        EventExpenditureAmountPlanInfoArea.setEditable(false);

        EventExpenditureAmountPlanInfoAreaScrollPane = new JScrollPane(EventExpenditureAmountPlanInfoArea);
        EventExpenditureAmountPlanInfoAreaScrollPane.setPreferredSize(new Dimension(200, 70));

		EventExpenditureAmountPlanInfoAreaPanel = new JPanel();
        EventExpenditureAmountPlanInfoAreaPanel.setBorder(new TitledBorder("Ausgabenplanungsinfo"));
		EventExpenditureAmountPlanInfoAreaPanel.add(EventExpenditureAmountPlanInfoAreaScrollPane);
		// ===END EventExpenditureAmountPlanInfoArea

		// ===START EventExpenditureAmountPlanInfoArea
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
		// ===END EventExpenditureAmountPlanInfoArea
    }

	private void createListeners() {
		// Listener for the textfield
		BillingMonth.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent ke) {
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER
						&& Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}", BillingMonth.getText())) {
					((JointAccountClosingDetailTableModel) JointAccountClosingDetailTable.getModel())
							.aktualisiere(BillingMonth.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent ke) {
			}
		});

		// Listeners for the JointAccountClosingDetailTable
		JointAccountClosingDetailTable.getSelectionModel().addListSelectionListener(new JointAccountClosingDetailTableSelectionListener(
			JointAccountClosingDetailTable, EventExpenditureAmountPlanInfoArea, cn, BillingMonth, EventInfoAreaAccountClosing));

		// Listener for the EventInfoAreaAccountClosingPanel
		EventInfoAreaAccountClosing.addKeyListener(new JointAccountClosingEventInfoAreaKeyListener(cn, BillingMonth, JointAccountClosingDetailTable));
	}

    private void createLayout() {
		//layout rootdefinitions
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JointAccountClosingWindow.setLayout(gbl);

        // place pnlBillingMonth
        gbc.gridx = 0;
        gbc.gridy = 0;
        // gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        // gbc.insets = new Insets(10, 10, 0, 0);
        JointAccountClosingWindow.add(pnlBillingMonth, gbc);

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
    }
}
