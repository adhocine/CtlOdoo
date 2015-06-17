package ctlodoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ctlodoo {

	private static boolean isAutoScroll = true;

	static Timer timer 	= new Timer(true);
	private static int _Error 	= 1;
	private static int _Waring 	= 2;

	private static String odooDirectory = new String();
	private static String odooServerLog = new String();
	private static String dbDirectory;

	private static long nbLineToShow = 0;

	public static int _Info = 3;
	protected static String odooServiceName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ctlodoo window = new ctlodoo();
					window.frmOdooServerMonitor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public ctlodoo() {
		initialize();
	}


	@SuppressWarnings("deprecation")
	public static String addToLog(String args) {
		Date date = new Date();
		new Timestamp(date.getDate());
		return date.toLocaleString() + " - " + args;
	}


	public static boolean isAdmin() {
		Preferences prefs = Preferences.systemRoot();
		try {
			prefs.put("foo", "bar"); // SecurityException on Windows
			prefs.remove("foo");
			prefs.flush(); // BackingStoreException on Linux
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean isServiceRunning(String serviceName) {
		try {

			File file = File.createTempFile("odooRunService", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set sh = CreateObject(\"Shell.Application\") \n"
					+ "If sh.IsServiceRunning(\"" + serviceName + "\") Then \n"
					+ "   wscript.Quit(1) \n" + "End If \n"
					+ "wscript.Quit(0) \n";
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("wscript " + file.getPath());
			p.waitFor();
			return (p.exitValue() == 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void msgBox(String msg) {
		javax.swing.JOptionPane.showConfirmDialog((java.awt.Component) null,
				msg, "Odoo 8 Server Control",
				javax.swing.JOptionPane.DEFAULT_OPTION);
	}

	private JFrame frmOdooServerMonitor;

	private JTextField efileName;

	private void append(String s, SimpleAttributeSet attributes,
			JEditorPane textPane) {
		Document d = textPane.getDocument();
		try {

			d.insertString(d.getLength(), s,
					attributes);
		} catch (BadLocationException ble) {
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		final JSpinner snbLigneToShow = new JSpinner();
		final JTextArea actFram = new JTextArea();

		 DefaultCaret caret = (DefaultCaret)actFram.getCaret();
		 caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		if (!ctlodoo.isAdmin()) {
			msgBox("Please run this application as Administrator !");
		}

		readIni iniFile = new readIni();
		iniFile.getproperty();

		actFram.append(addToLog("Get ini file\n"));

		ctlodoo.odooDirectory = readIni.odooDirectory;
		ctlodoo.dbDirectory	 = readIni.dbDirectory;
		ctlodoo.odooServerLog = odooDirectory + "\\server\\openerp-server.log";
		// ************************
		final String startServer = odooDirectory + "\\service\\start.bat";
		final String stopServer = odooDirectory + "\\service\\stop.bat";
		final String dbServerStart = dbDirectory + "\\bin\\pg_ctl";
		final String dbDataDir = dbDirectory + "\\data";
		// ************************
		ctlodoo.odooServiceName = readIni.odooServiceName;
		// ******************************

		frmOdooServerMonitor = new JFrame();
		frmOdooServerMonitor.setTitle("Odoo 8 Server Control");
		frmOdooServerMonitor.setBounds(200, 0, 1024, 720);

		frmOdooServerMonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmOdooServerMonitor.setJMenuBar(menuBar);

		JMenu mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);

		JMenuItem mntmconfigurations = new JMenuItem("Configurations");
		mnConfig.add(mntmconfigurations);

		JMenuItem mntmQuitter = new JMenuItem("Quitter");
		mntmQuitter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnConfig.add(mntmQuitter);

		// **************************************************

		JToolBar toolBar = new JToolBar();
		frmOdooServerMonitor.getContentPane().add(toolBar, BorderLayout.NORTH);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.3);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmOdooServerMonitor.getContentPane().add(splitPane,
				BorderLayout.CENTER);



		final JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setRightComponent(scrollPane);

		JToolBar toolBar_2 = new JToolBar();
		scrollPane.setColumnHeaderView(toolBar_2);


						efileName = new JTextField();
						toolBar_2.add(efileName);
						efileName.setColumns(10);
						efileName.setText(odooServerLog);

		final JTextPane logFram = new JTextPane();

		JButton btnLogClear = new JButton("     Log Clear     ");
		btnLogClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logFram.setText("");
				ctlodoo.nbLineToShow = 0;
			}
		});
		toolBar_2.add(btnLogClear);


				scrollPane.setViewportView(logFram);
				logFram.setAutoscrolls(true);
				logFram.setEditable(false);

						DefaultCaret caret1 = (DefaultCaret)logFram.getCaret();
		 caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		final JButton btnStartServer = new JButton("Start Odoo Server");

		btnStartServer.setEnabled(isAdmin());

		final JButton btnRestart = new JButton("Restart Server");
		btnRestart.setEnabled(isAdmin() && isServiceRunning(ctlodoo.odooServiceName));

		btnStartServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnStartServer.getText() == "Start Odoo Server") {
					actFram.append(addToLog("Odoo Server Starting...  " + "\n"));
					try {
						Runtime runtime = Runtime.getRuntime();
						String[] args = { "cmd.exe", "/C", startServer };
						runtime.exec(args);
						btnStartServer.setText("Stop Odoo Server");
						actFram.append(addToLog("Odoo Server Started  " + "\n"));
						btnRestart.setEnabled(true);
					} catch (IOException e) {
						actFram.append(addToLog(e.getMessage()));
					}
				} else {
					actFram.append(addToLog("Odoo Server Stopping  " + "\n"));
					try {
						Runtime runtime = Runtime.getRuntime();
						String[] args = { "cmd.exe", "/C", stopServer };
						runtime.exec(args);
						actFram.append(addToLog("Odoo Server Stoped  " + "\n"));
						btnStartServer.setText("Start Odoo Server");
						btnRestart.setEnabled(false);
					} catch (IOException e) {
						actFram.append(addToLog(e.getMessage()));
					}
				}
			}
		});

		toolBar.add(btnStartServer);
		if (isServiceRunning(ctlodoo.odooServiceName))
			btnStartServer.setText("Stop Odoo Server");
		// ***************************************
		btnRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actFram.append(addToLog("Odoo Server restarting...  " + "\n"));
				try {
					Runtime runtime = Runtime.getRuntime();
					String[] args = { "cmd.exe", "/C", stopServer };
					runtime.exec(args);
					/*********/
					for (int i = 1; i <= 5000; i++)
						;
					/*********/
					Runtime runtime1 = Runtime.getRuntime();
					String[] args1 = { "cmd.exe", "/C", startServer };
					runtime1.exec(args1);
					actFram.append(addToLog("Odoo Server restarted " + "\n"));
				} catch (IOException e) {
					actFram.append(addToLog(e.getMessage()));
				}/**/
			}
		});
		toolBar.add(btnRestart);

		btnRestart.setEnabled(isAdmin() && isServiceRunning(ctlodoo.odooServiceName));

		final JCheckBox chckbxAutoScroll = new JCheckBox("Auto Scroll");
		chckbxAutoScroll.setSelected(true);


		chckbxAutoScroll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (chckbxAutoScroll.isSelected()) {
					ctlodoo.isAutoScroll = true;
					actFram.append(addToLog("AutoScroll Started" + "\n"));

				} else {
					ctlodoo.isAutoScroll = false;
					actFram.append(addToLog("AutoScroll Stoped" + "\n"));
				}
			}
		});

		toolBar.add(chckbxAutoScroll);

		final JCheckBox chckbxAlwaysOntop = new JCheckBox("Always onTop");
		chckbxAlwaysOntop.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {

				Boolean t = chckbxAlwaysOntop.isSelected();
				frmOdooServerMonitor.setAlwaysOnTop(t);

			}
		});

		chckbxAlwaysOntop.setHorizontalAlignment(SwingConstants.LEFT);
		toolBar.add(chckbxAlwaysOntop);

		JButton btnLoadLog = new JButton("Load log");
		btnLoadLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				logFram.setText("");
				try {
					String odooDirectory = new String();
					String odooServerLog = new String();

					odooDirectory = readIni.odooDirectory;
					odooServerLog = odooDirectory
							+ "\\server\\openerp-server.log";

					FileReader fr = new FileReader(odooServerLog);
					int nbLineToShow = (int) snbLigneToShow.getValue();

					@SuppressWarnings("resource")
					BufferedReader reader1 = new BufferedReader(fr);

					while ((reader1.readLine()) != null) {
						List<String> tmp = new ArrayList<String>();

						String ch;
						do {
							ch = reader1.readLine();
							tmp.add(ch);

						} while (ch != null);
						int i;
						int mTypeMsg = 0;
						for (i = tmp.size() - nbLineToShow; i < tmp.size() - 1; i++) {
							mTypeMsg = 0;
							String vsTmp = tmp.get(i).toUpperCase();
							// ****************************************************
							Pattern expERROR = Pattern.compile("(ERROR)");
							Pattern expINFO = Pattern.compile("(INFO)");
							Pattern.compile("(ERROR)");
							Matcher matcher = expERROR.matcher(vsTmp);
							if (matcher.find())
								mTypeMsg = ctlodoo._Error;

							// ****************************************************
							matcher = expINFO.matcher(vsTmp);
							if (matcher.find())
								mTypeMsg = ctlodoo._Info;

							Styling(logFram, tmp.get(i) + "\n", mTypeMsg);
						}
					}
				} catch (IOException ioe) {
					System.err.println(ioe);
					System.exit(1);
				}/**/
			}
		});

		JSeparator separator_2 = new JSeparator();


		toolBar.add(separator_2);
		toolBar.add(btnLoadLog);

		JSeparator separator_3 = new JSeparator();
		toolBar.add(separator_3);

		final JButton btnDbStart = new JButton("DB Server Start");
		btnDbStart.setEnabled(isAdmin());
		btnDbStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				if (btnDbStart.getText() == "DB Server Start") {
					try {
						actFram.append(addToLog("DB Server Started at "
								+ dbDataDir + "\n"));
						btnDbStart.setText("DB Server Stop");
						Runtime runtime = Runtime.getRuntime();
						String[] args = {
								"cmd.exe",
								"/C",
								"\"" + dbServerStart + "\" start -D \""
										+ dbDataDir + "\" -w" };
						runtime.exec(args);

					} catch (IOException e) {
						actFram.append(addToLog(e.getMessage()));
					}
				} else {
					try {
						actFram.append(addToLog("DB Server Stoped " + dbDataDir
								+ "\n"));
						btnDbStart.setText("DB Server Start");
						Runtime runtime = Runtime.getRuntime();
						String[] args = {
								"cmd.exe",
								"/C",
								"\"" + dbServerStart + "\" stop -D \""
										+ dbDataDir + "\" -m fast" };
						runtime.exec(args);
						actFram.append(addToLog("\"" + dbServerStart + "\" stop -D \""
								+ dbDataDir + "\" -m fast"));

					} catch (IOException e) {
						actFram.append(addToLog(e.getMessage()));
					}
				}
			}
		});
		toolBar.add(btnDbStart);

		JSeparator separator_1 = new JSeparator();
		toolBar.add(separator_1);

		JSeparator separator = new JSeparator();
		toolBar.add(separator);

		JLabel lblNbLineTo = new JLabel("   NB Line to Show   ");
		lblNbLineTo.setBackground(new Color(240, 240, 240));
		lblNbLineTo.setHorizontalAlignment(SwingConstants.RIGHT);
		toolBar.add(lblNbLineTo);

		snbLigneToShow.setModel(new SpinnerNumberModel(new Integer(1000), null, null, new Integer(1)));
		lblNbLineTo.setLabelFor(snbLigneToShow);
		toolBar.add(snbLigneToShow);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		scrollPane_1.setAutoscrolls(true);

		scrollPane_1.setViewportView(actFram);

		JToolBar toolBar_3 = new JToolBar();
		scrollPane_1.setColumnHeaderView(toolBar_3);

				JSeparator separator_4 = new JSeparator();
				toolBar_3.add(separator_4);

				final JButton btnClean = new JButton("     Log Clear     ");
				toolBar_3.add(btnClean);
				btnClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					actFram.setText("");
				}

				});

						JTextPane textPane = new JTextPane();
						frmOdooServerMonitor.getContentPane().add(textPane, BorderLayout.EAST);

						JToolBar toolBar_1 = new JToolBar();
						frmOdooServerMonitor.getContentPane().add(toolBar_1, BorderLayout.SOUTH);

		final TimerTask task = new TimerTask() {
			@Override
			public void run() {

					if (ctlodoo.isAutoScroll) {
						try {
							 Reader fileReader = new FileReader(ctlodoo.odooServerLog);
						        BufferedReader input = new BufferedReader(fileReader);
						        String line = null;
						        while (true) {
						            if ((line = input.readLine()) != null) {
						                int mTypeMsg = 0;
										String vsTmp = line.toUpperCase();
										// ****************************************************
										Pattern expERROR = Pattern.compile("(ERROR)");
										Pattern expINFO = Pattern.compile("(INFO)");
										Pattern expWAR = Pattern.compile("(WARNING)");
										// ************
										Pattern.compile("(ERROR)");
										Matcher matcher = expINFO.matcher(vsTmp);
										// ****************************************************
										if (matcher.find())
											mTypeMsg = ctlodoo._Info;
										matcher = expWAR.matcher(vsTmp);
										if (matcher.find())
											mTypeMsg = ctlodoo._Waring;
										matcher = expERROR.matcher(vsTmp);
										if (matcher.find())
											mTypeMsg = ctlodoo._Error;
										// ************
										ctlodoo.nbLineToShow++;
										@SuppressWarnings("unused")
										String nbLineToShow = Long.toString(ctlodoo.nbLineToShow);
										Long t = Long.valueOf(snbLigneToShow.getValue().toString());
										if (ctlodoo.nbLineToShow == t) {ctlodoo.nbLineToShow = 0; logFram.setText("");}
										//***************************
										Styling(logFram, line + "\n", mTypeMsg);
						                continue;
						            }
						            try {
						                Thread.sleep(1000L);
						            } catch (InterruptedException x) {
						                Thread.currentThread().interrupt();
						                break;
						            }
						        }
						        input.close();
						    } catch (IOException ioe) {
								System.err.println(ioe);
								System.exit(1);
							}/**/
					} // end condition
			}
		}; // End Task function

		if (chckbxAutoScroll.isSelected())
			timer.schedule(task, 0, 3000);
	}

	void Styling(JEditorPane logFram, String vStr, int vType) {

		SimpleAttributeSet red = new SimpleAttributeSet();
		StyleConstants.setBold(red, true);
		StyleConstants.setForeground(red, Color.red);

		SimpleAttributeSet blue = new SimpleAttributeSet();
		StyleConstants.setForeground(blue, Color.blue);

		SimpleAttributeSet orange = new SimpleAttributeSet();
		StyleConstants.setForeground(orange, Color.orange);

		switch (vType) {
		case 1: {
			append(vStr, red, logFram);
			break;
		}
		case 2: {
			append(vStr, orange, logFram);
			break;
		}
		case 3: {
			append(vStr, blue, logFram);
			break;
		}
		default:
			append(vStr, null, logFram);
			break;
		}
	}
}

class readIni {

	public static String odooDirectory;

	protected static String nbLineToShow = "";
	protected static String dbDirectory= "";
	protected static String odooServiceName= "";
	protected static void main(String args[]) {
		readIni ini = new readIni();
		ini.getproperty();
	}

	public static void msgBox(Exception e) {
		javax.swing.JOptionPane
				.showConfirmDialog((java.awt.Component) null, e,
						"Odoo 8 Server Control",
						javax.swing.JOptionPane.DEFAULT_OPTION);
	}

	public void getproperty() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("c:\\ctlodoo.ini"));
			readIni.odooDirectory 	= p.getProperty("OdooDirectory");
			readIni.dbDirectory 	= p.getProperty("dbDirectory");
			readIni.nbLineToShow 	= p.getProperty("nbLineToShow");
			readIni.odooServiceName = p.getProperty("odooServiceName");
		} catch (Exception e) {
			msgBox(e);
		}
	}
}
