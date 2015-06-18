/**
 *
 */
package ctlodoo;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.SpringLayout;
import javax.swing.JSpinner;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author adhocine
 *
 */

public class config_frm extends JDialog  {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame frmconfig_frm;
	private static JTextField odoo_sn;
	private static JTextField odoo_dir;
	private static JTextField db_dir;
	private static JTextField db_sn;
	private static JSpinner nbl = new JSpinner();
	static readIni iniFilec = new readIni();



	/**
	 * Launch the application.


	/**
	 * Create the application.
	 */

	public config_frm(JFrame parent, String title, String message) {

		System.out.println(ConfigData.odooDirectory);

		setFrmconfig_frm(new JFrame());
		getFrmconfig_frm().setType(Type.UTILITY);
		setResizable(false);
		setTitle("Odoo 8 Server Control");
		getFrmconfig_frm().setBounds(200, 0, 521, 276);
		getFrmconfig_frm().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(10, 34, 485, 92);
		getContentPane().add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);

		JLabel lblOdooRoot = new JLabel("Odoo 8 Root Directory");
		sl_panel.putConstraint(SpringLayout.NORTH, lblOdooRoot, 10, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, lblOdooRoot, 10, SpringLayout.WEST, panel);
		panel.add(lblOdooRoot);

		odoo_sn = new JTextField();
		sl_panel.putConstraint(SpringLayout.EAST, odoo_sn, -172, SpringLayout.EAST, panel);
		odoo_sn.setColumns(30);
		panel.add(odoo_sn);

		odoo_dir = new JTextField();
		sl_panel.putConstraint(SpringLayout.WEST, odoo_sn, 0, SpringLayout.WEST, odoo_dir);
		sl_panel.putConstraint(SpringLayout.NORTH, odoo_dir, -3, SpringLayout.NORTH, lblOdooRoot);
		sl_panel.putConstraint(SpringLayout.WEST, odoo_dir, 6, SpringLayout.EAST, lblOdooRoot);
		sl_panel.putConstraint(SpringLayout.EAST, odoo_dir, 300, SpringLayout.EAST, lblOdooRoot);
		odoo_dir.setColumns(15);
		panel.add(odoo_dir);

		JLabel label_1 = new JLabel("Odoo Service Name");
		sl_panel.putConstraint(SpringLayout.NORTH, label_1, 14, SpringLayout.SOUTH, lblOdooRoot);
		sl_panel.putConstraint(SpringLayout.NORTH, odoo_sn, -3, SpringLayout.NORTH, label_1);
		sl_panel.putConstraint(SpringLayout.WEST, label_1, 0, SpringLayout.WEST, lblOdooRoot);
		panel.add(label_1);

		JLabel lblNbLineTo = new JLabel("Nb Lines To Show");
		sl_panel.putConstraint(SpringLayout.WEST, lblNbLineTo, 0, SpringLayout.WEST, lblOdooRoot);
		panel.add(lblNbLineTo);


		sl_panel.putConstraint(SpringLayout.NORTH, lblNbLineTo, 3, SpringLayout.NORTH, nbl);
		sl_panel.putConstraint(SpringLayout.NORTH, nbl, 6, SpringLayout.SOUTH, odoo_sn);
		sl_panel.putConstraint(SpringLayout.WEST, nbl, 0, SpringLayout.WEST, odoo_sn);
		sl_panel.putConstraint(SpringLayout.EAST, nbl, -262, SpringLayout.EAST, panel);
		panel.add(nbl);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 127, 485, 65);
		getContentPane().add(panel_1);
		SpringLayout sl_panel_1 = new SpringLayout();
		panel_1.setLayout(sl_panel_1);

		db_dir = new JTextField();
		db_dir.setColumns(15);
		panel_1.add(db_dir);

		JLabel lblPostgresRootDirectory = new JLabel("PostgreSQL Root Directory");
		sl_panel_1.putConstraint(SpringLayout.NORTH, db_dir, -3, SpringLayout.NORTH, lblPostgresRootDirectory);
		sl_panel_1.putConstraint(SpringLayout.WEST, db_dir, 6, SpringLayout.EAST, lblPostgresRootDirectory);
		sl_panel_1.putConstraint(SpringLayout.EAST, db_dir, 315, SpringLayout.EAST, lblPostgresRootDirectory);
		sl_panel_1.putConstraint(SpringLayout.NORTH, lblPostgresRootDirectory, 10, SpringLayout.NORTH, panel_1);
		sl_panel_1.putConstraint(SpringLayout.WEST, lblPostgresRootDirectory, 10, SpringLayout.WEST, panel_1);
		panel_1.add(lblPostgresRootDirectory);

		JLabel lblPostgresqlServiceName = new JLabel("PostgreSQL Service Name");
		sl_panel_1.putConstraint(SpringLayout.NORTH, lblPostgresqlServiceName, 12, SpringLayout.SOUTH, lblPostgresRootDirectory);
		sl_panel_1.putConstraint(SpringLayout.WEST, lblPostgresqlServiceName, 0, SpringLayout.WEST, lblPostgresRootDirectory);
		panel_1.add(lblPostgresqlServiceName);

		db_sn = new JTextField();
		sl_panel_1.putConstraint(SpringLayout.EAST, db_sn, 156, SpringLayout.WEST, db_dir);
		sl_panel_1.putConstraint(SpringLayout.NORTH, db_sn, 6, SpringLayout.SOUTH, db_dir);
		sl_panel_1.putConstraint(SpringLayout.WEST, db_sn, 0, SpringLayout.WEST, db_dir);
		db_sn.setColumns(30);
		panel_1.add(db_sn);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 203, 485, 33);
		getContentPane().add(panel_2);

		JButton btnNewButton = new JButton("   Ok   ");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int mc = JOptionPane.QUESTION_MESSAGE;
				int bc = JOptionPane.YES_NO_OPTION;

				int ch = JOptionPane.showConfirmDialog  (null, "Confirm Data ?", "Confirm", bc, mc);
				if ( ch == JOptionPane.YES_OPTION) {
					setData();
					new writIni(readIni.data);
					writIni.setproperty();
					setVisible(false);
					 dispose();
				}
			}
		});
		panel_2.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				 dispose();
			}
		});
		panel_2.add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("Configurations");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(10, 0, 350, 33);
		getContentPane().add(lblNewLabel);

		getData();


		 //pack();
	}


	static void getData() {
		odoo_dir.setText(ConfigData.odooDirectory);
		odoo_sn.setText(ConfigData.odooServiceName);
		db_dir.setText(ConfigData.dbDirectory);
		db_sn.setText(ConfigData.dbServiceName);
		nbl.setValue(ConfigData.nbLineToShow);
	}



	static void setData() {
		ConfigData.odooDirectory 	= odoo_dir.getText();
		ConfigData.odooServiceName 	= odoo_sn.getText();
		ConfigData.dbDirectory 		= db_dir.getText();
		ConfigData.dbServiceName 	= db_sn.getText();
		ConfigData.nbLineToShow 		= (int) nbl.getValue();
	}

	final JOptionPane optionPane = new JOptionPane(
		    "The only way to close this dialog is by\n"
		    + "pressing one of the following buttons.\n"
		    + "Do you understand?",
		    JOptionPane.QUESTION_MESSAGE,
		    JOptionPane.YES_NO_OPTION);

	public static JFrame getFrmconfig_frm() {
		return frmconfig_frm;
	}

	public static void setFrmconfig_frm(JFrame frmconfig_frm) {
		config_frm.frmconfig_frm = frmconfig_frm;
	}
}

