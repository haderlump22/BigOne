package de.rachel.bigone;

import static de.rachel.bigone.DatabaseConstants.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class TankDialog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2262653597062890751L;
	private JLabel lblKm, lblLt, lblKfz, lblTreibstoff;
	private JFormattedTextField txtKm, txtLt;
	private JComboBox cmbKfz, cmbTreibstoff;
	private JButton btnTaSa;
	private Font fontTxtFields, fontCmbBoxes;
	
	TankDialog(JFrame dialogOwner, String strBetrag){
		final JDialog dialog = new JDialog(dialogOwner, "Tankdaten: " + strBetrag, true);
		dialog.setSize(220,250);
		dialog.setLayout(null);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		//schriftenfestlegungen
		fontTxtFields = new Font("Arial",Font.PLAIN,16);
		fontCmbBoxes = new  Font("Arial",Font.PLAIN,16);
		
		lblKm = new JLabel("Kilometer");
		lblKm.setBounds(10,10,60,25);
		
		txtKm = new JFormattedTextField();
		txtKm.setFont(fontTxtFields);
		txtKm.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtKm.setText("0");
		txtKm.addFocusListener( new FocusListener() {
			public void focusLost( FocusEvent fe ) {
		    	 
		        }
			public void focusGained(FocusEvent fe) {
				txtKm.selectAll();
			}
		});
		txtKm.setBounds(80,10,60,25);
		
		lblLt = new JLabel("Liter");
		lblLt.setBounds(10,40,60,25);
		
		txtLt = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#,##0.00")));
		txtLt.setFont(fontTxtFields);
		txtLt.setHorizontalAlignment(JFormattedTextField.RIGHT);
		txtLt.setText("0,00");
		txtLt.addFocusListener( new FocusListener() {
			public void focusLost( FocusEvent fe ) {
		    	 
		        }
			public void focusGained(FocusEvent fe) {
				txtLt.selectAll();
			}
		});
		txtLt.setBounds(80,40,60,25);
		
		lblKfz = new JLabel("Kfz");
		lblKfz.setBounds(10,70,60,25);
		
		cmbKfz = new JComboBox();
		cmbKfz.setFont(fontCmbBoxes);
		cmbKfz.setBounds(80,70,100,25);
		fill_cmbKfz();
		
		lblTreibstoff = new JLabel("Treibstoff");
		lblTreibstoff.setBounds(10,100,60,25);
		
		cmbTreibstoff = new JComboBox();
		cmbTreibstoff.setFont(fontCmbBoxes);
		cmbTreibstoff.setBounds(80,100,100,25);
		fill_cmbTreibstoff();
		
		btnTaSa = new JButton("Speichern");
		btnTaSa.setBounds(40,130,100,30);
		btnTaSa.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent ae){
            	dialog.setVisible(false);
            	dialog.dispose();
            	
            }

        });
		
		dialog.add(lblKm);
		dialog.add(txtKm);
		dialog.add(lblLt);
		dialog.add(txtLt);
		dialog.add(lblKfz);
		dialog.add(cmbKfz);
		dialog.add(lblTreibstoff);
		dialog.add(cmbTreibstoff);
		dialog.add(btnTaSa);
		
		dialog.setVisible(true);
	}
	private void fill_cmbKfz() {
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT typ, kfz_id FROM kfz order by 1;" );
	      while ( rs.next() )
	        cmbKfz.addItem(rs.getString(1) + " (" + rs.getString(2) + ")");
	      rs.close();
	      stmt.close();
	      
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
		
	}
	private void fill_cmbTreibstoff() {
		try
		{
			Class.forName(DRIVER);
		}
		catch ( ClassNotFoundException e )
	    {
	      System.err.println( "Keine Treiber-Klasse!" );
	      return;
	    }
		Connection con = null;
	    try
	    {
	      con = DriverManager.getConnection( URL, USER, PASS );
	      Statement stmt = con.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT bez, kraftstoff_id FROM kraftstoffe order by 1;" );
	      while ( rs.next() )
	        cmbTreibstoff.addItem(rs.getString(1) + " (" + rs.getString(2) + ")");
	      rs.close();
	      stmt.close();
	      
	    }
	    catch ( SQLException e )
	    {
	      e.printStackTrace();
	      return;
	    }
	    finally
	    {
	      if ( con != null )
	        try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); }
	    }
	}
	public String get_km() {
		return txtKm.getText();
	}
	public String get_liter() {
		return txtLt.getText().replace(',','.');
	}
	public String get_kfz_id() {
		String strKfz;
		strKfz = cmbKfz.getSelectedItem().toString();
		return strKfz.substring(strKfz.indexOf('(') + 1, strKfz.indexOf(')'));
	}
	public String get_treibstoff_id() {
		String strTreibstoff;
		strTreibstoff = cmbTreibstoff.getSelectedItem().toString();
		return strTreibstoff.substring(strTreibstoff.indexOf('(') + 1, strTreibstoff.indexOf(')'));
	}
}
