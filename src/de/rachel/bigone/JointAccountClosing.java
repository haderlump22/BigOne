package de.rachel.bigone;

import java.sql.Connection;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

public class JointAccountClosing {
	private Connection cn = null;
    private JFrame JointAccountClosingWindow;
    private Font fontTxtFields, fontCmbBoxes, fontLists;
    private JPanel pnlAbrMonat;
    private JFormattedTextField txtAbrMonat;

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
		JointAccountClosingWindow.setSize(800, 580);
		JointAccountClosingWindow.setLocation(200, 200);
		JointAccountClosingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JointAccountClosingWindow.setResizable(true);

		// Font settings
		fontTxtFields = new Font("Arial", Font.PLAIN, 16);
		fontCmbBoxes = new Font("Arial", Font.PLAIN, 16);
		fontLists = new Font("Arial", Font.PLAIN, 10);

		// ====START Month of AccountClosing====
		pnlAbrMonat = new JPanel();
		pnlAbrMonat.setPreferredSize(new Dimension(150, 60));
		pnlAbrMonat.setBorder(new TitledBorder("Abrechnungsmonat"));

		// create Content for the Panel in shape of a txtField
		try {
			txtAbrMonat = new JFormattedTextField(new MaskFormatter("01-##-20##"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		txtAbrMonat.setPreferredSize(new Dimension(110, 25));
		txtAbrMonat.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtAbrMonat.setFont(fontTxtFields);

		// put the textfield to the Panal
		pnlAbrMonat.add(txtAbrMonat);
		// ====END Month of AccountClosing====
    }

	private void createListeners() {
			// txtAbrMonat.addKeyListener(new KeyListener() {
			// 	@Override
			// 	public void keyTyped(KeyEvent ke) {
			// 	}
			// 	@Override
			// 	public void keyReleased(KeyEvent ke) {
			// 		if( ke.getKeyCode() == KeyEvent.VK_ENTER && Pattern.matches("\\d{2}.\\d{2}.[1-9]{1}\\d{3}",txtAbrMonat.getText())) {
			// 			txtHinweis.setText(""); //clear txtHinweis Field
			// 			lstModelAllIncome.clear(); //clear form old values from the previos call
			// 			cmbModelPerson.removeAllElements(); //clear befor put new elements in to it
			// 			calculate_profit(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));
			// 			putPersonNameToCmbPerson();
			// 			putIncomeToListAllIncome(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0));

			// 			//check if the actual liquimonth is already fix and it is so, then set the buttons, who put and remove
			// 			//incomes to and from persons, inactive
			// 			if(is_liqui_fix(BigOneTools.datum_wandeln(txtAbrMonat.getText(), 0))){
			// 				btnAdd.setEnabled(false);
			// 				btnRemove.setEnabled(false);
			// 			}else {
			// 				btnAdd.setEnabled(true);
			// 				btnRemove.setEnabled(true);
			// 			}
			// 		}
			// 	}
			// 	@Override
			// 	public void keyPressed(KeyEvent ke) {
			// 	}
			// });


	}

    private void createLayout() {
		//layout rootdefinitions
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JointAccountClosingWindow.setLayout(gbl);

        // place pnlAbrMonat
        gbc.gridx = 0;
        gbc.gridy = 0;
        // gbc.gridwidth = 2;
        // gbc.anchor = GridBagConstraints.WEST;
        // gbc.insets = new Insets(10, 10, 0, 0);
        JointAccountClosingWindow.add(pnlAbrMonat, gbc);
    }
}
