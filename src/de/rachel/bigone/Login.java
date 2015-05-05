package de.rachel.bigone;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Login {
	
	private JDialog login;
	private int Logincount = 0;
	private JLabel lblUeberschrift, lblName, lblPW;
	private JTextField txtBenutzer;
	private JPasswordField txtPW;
	private JButton btnLogin;
	private String strB, strPW;
	private String strDbDrv="org.gjt.mm.mysql.Driver";
	private String strDbUrl="jdbc:mysql://localhost:3306/bigone";
	private Connection cn = null;
		
	public Login(JFrame dialogOwner) {
		login = new JDialog(dialogOwner,"LOGIN",true);
		login.setSize(290,200);
		login.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		login.setLayout(null);
		login.getContentPane().setBackground(Color.white);
		
		
		lblUeberschrift = new JLabel("Login to Budget");
   		lblUeberschrift.setBounds(10,10,270,25);

   		lblName = new JLabel("Benutzername");
   		lblName.setBounds(10,40,100,25);

   		lblPW = new JLabel("Passwort");
   		lblPW.setBounds(10,70,100,25);
   		
   		txtBenutzer = new JTextField();
   		txtBenutzer.setBounds(120,40,120,25);

   		txtPW = new JPasswordField("");
   		txtPW.setBounds(120,70,120,25);
   		
   		btnLogin = new JButton("Login");
   		btnLogin.setBounds(100,100,90,25);
   		btnLogin.addActionListener(new ActionListener() {
   			public void actionPerformed(ActionEvent e) {
  		  
   				//zuweisung der Textfeldwerte an die lokalen Varablen
   				strB = txtBenutzer.getText();
   				strPW = new String(txtPW.getPassword());
  	        	
   				try {
   					// Select fitting database driver and connect:
   					Class.forName( strDbDrv );
   					cn = DriverManager.getConnection( strDbUrl, strB, strPW );
   					login.dispose();
   				} 
   				catch( Exception ex ) {
   					//ausnahme beschreibung auf der konsole ausgeben
   					txtPW.setText("");
   					txtPW.requestFocus();
   					Logincount++;
   					if(Logincount == 3) {
   						JOptionPane.showMessageDialog(null, "maximale Anzahl der Loginversuche überschritten", "Achtung", JOptionPane.INFORMATION_MESSAGE);
   						login.dispose();
   					}
   					//System.out.println(ex.toString());
   				} 
   				
   			}
   		});
  		
   		login.add(lblUeberschrift);
   		login.add(lblName);
   		login.add(lblPW);
   		login.add(txtBenutzer);
   		login.add(txtPW);
   		login.add(btnLogin);
   		login.validate();
   		login.repaint();
   		login.setVisible(true);
   		
   		txtBenutzer.requestFocus();
    }
	public Connection getConnection() {
		return cn;
	}
	public String getUser() {
		return txtBenutzer.getText();
	}
	public int getLogincount() {
		return Logincount;
	}
}
