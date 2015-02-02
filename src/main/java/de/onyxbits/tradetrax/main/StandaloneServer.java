package de.onyxbits.tradetrax.main;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * A simple launcher for running a bundled web app without the need of an
 * external servlet container. This class will:
 * <ul>
 * </li>start an embedded Jetty server and bind the bundled app</li>
 * <li>start a mininmal Swing UI from which the user can access/destroy the
 * server or change the ledger.</li>
 * </ul>
 * <p>
 * A number of system properties are recognized: </dl>
 * <dt>app.skipwindow</dt>
 * <dd>Keep the application window minimized. Clicking the icon in the taskbar
 * directly opens the browser.</dd>
 * <dt>app.nowindow
 * <dt>
 * <dd>Start only the server, but not the GUI</dd>
 * <dt>app.ledger</dt>
 * <dd>Directory to pass to the webapp as the ledger</dd> </dl>
 * <dt>app.homedir</dt>
 * <dd>Root directory for the tradetrax application.</dd>
 * 
 * @author patrick
 * 
 */
public class StandaloneServer extends JFrame implements Runnable, WindowListener,
		HyperlinkListener, ActionListener {

	private static final String LOADING_CARD = "loading";
	private static final String RUNNING_CARD = "running";

	private static final long serialVersionUID = 1L;
	private static ResourceBundle i18n = ResourceBundle.getBundle("StandaloneServer");

	private JEditorPane about;
	private JProgressBar loading;
	private Server server;
	private JMenuItem addLedger;
	private JMenuItem openLedger;
	private JCheckBoxMenuItem openPublic;
	private JMenuItem quit;
	private JMenuItem[] recent = new JMenuItem[5];

	private static final String[] ICONRESOURCES = {
			"appicon-16.png",
			"appicon-24.png",
			"appicon-32.png",
			"appicon-48.png",
			"appicon-64.png",
			"appicon-96.png",
			"appicon-128.png" };

	private StandaloneServer() {
		// Build a minimal control UI.
		JPanel content = new JPanel(new CardLayout());
		loading = new JProgressBar();
		loading.setString("");
		loading.setStringPainted(true);
		loading.setIndeterminate(true);
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new GridBagLayout());
		wrapper.add(loading);
		content.add(wrapper, LOADING_CARD);
		setTitle(i18n.getString("appname"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		about = new HypertextPane("");
		about.setMargin(new Insets(10, 10, 10, 10));
		content.add(new JScrollPane(about), RUNNING_CARD);
		setContentPane(content);

		JMenuBar mbar = new JMenuBar();

		JMenu file = new JMenu(i18n.getString("file"));
		file.setMnemonic(KeyStroke.getKeyStroke(i18n.getString("file_mnemonic")).getKeyCode());
		addLedger = new JMenuItem(i18n.getString("addledger"), KeyStroke.getKeyStroke(
				i18n.getString("addledger_mnemonic")).getKeyCode());
		addLedger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));
		openLedger = new JMenuItem(i18n.getString("openledger"), KeyStroke.getKeyStroke(
				i18n.getString("openledger_mnemonic")).getKeyCode());
		openLedger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));
		quit = new JMenuItem(i18n.getString("quit"), KeyStroke.getKeyStroke(
				i18n.getString("quit_mnemonic")).getKeyCode());
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));
		openPublic = new JCheckBoxMenuItem(i18n.getString("opentopublic"), false);

		file.add(addLedger);
		file.add(openLedger);
		file.add(new JSeparator());
		for (int i = 0; i < recent.length; i++) {
			recent[i] = new JMenuItem((i + 1) + ": ");
			file.add(recent[i]);
			configureRecentListEntry(i,null);
		}
		file.add(new JSeparator());
		file.add(openPublic);
		file.add(new JSeparator());
		file.add(quit);
		mbar.add(file);
		setJMenuBar(mbar);

		List<Image> icons = new Vector<Image>();
		for (String ico : ICONRESOURCES) {
			try {
				icons.add(new ImageIcon(ClassLoader.getSystemResource(ico), "").getImage());
			}
			catch (Exception e) {
				// No need to make a fuss if we don't have all possible resolutions.
			}
		}
		setIconImages(icons);
	}

	/**
	 * This must be called through a {@link OpenLedgerWorker} in order not to
	 * block the UI!
	 * 
	 * @param cfg
	 *          config data.
	 * @throws Exception
	 *           whatever server.start() chokes up
	 */
	protected void openLedger(LedgerConfig cfg) throws Exception {

		if (server != null && server.isRunning()) {
			try {
				server.stop();
				server.destroy();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		server = new Server(cfg.getAddress());
		server.setStopAtShutdown(true);
		WebAppContext app = new WebAppContext();
		app.setContextPath("/");
		app.setSessionHandler(new SessionHandler());
		app.setInitParameter("ledger", cfg.getLedger().getAbsolutePath());
		// DIRTY HACK: Compute a resource path that works with files on disk (in the
		// IDE) as well as in a jar.
		String dirtyhack = getClass().getResource("/WEB-INF/web.xml").toString();
		dirtyhack = dirtyhack.substring(0, dirtyhack.length() - "/WEB-INF/web.xml".length() + 1);
		app.setDescriptor(dirtyhack + "WEB-INF/web.xml");
		app.setResourceBase(dirtyhack);

		server.setHandler(app);
		server.start();
		openInBrowser();
	}

	/**
	 * Put a new file on top of the recently loaded ledger list
	 * 
	 * @param ledger
	 *          file to push into the list (will only be pushed if it is not yet
	 *          in there).
	 */
	public void pushRecentList(File ledger) {
		Vector<String> list = new Vector<String>();
		Preferences prefs = Preferences.userNodeForPackage(PrefKeys.class);

		for (int i = 0; i < recent.length; i++) {
			String tmp = prefs.get(PrefKeys.RECENT + i, null);
			if (ledger.getAbsolutePath().equals(tmp)) {
				return; // It's in the list -> don't change it.
			}
			list.add(tmp);
		}
		list.add(0, ledger.getAbsolutePath());
		for (int i = 0; i < recent.length; i++) {
			if (list.get(i) == null) {
				prefs.remove(PrefKeys.RECENT + i);
			}
			else {
				prefs.put(PrefKeys.RECENT + i, list.get(i));
			}
		}
	}

	/**
	 * (Re)Load the list of recent ledgers in the file menu.
	 */
	protected void loadRecentList() {
		for (int i = 0; i < recent.length; i++) {
			Preferences prefs = Preferences.userNodeForPackage(PrefKeys.class);
			String fname = prefs.get(PrefKeys.RECENT + i, null);
			if (fname == null) {
				configureRecentListEntry(i,null);
			}
			else {
				configureRecentListEntry(i,new File(fname));
			}
		}
	}

	/**
	 * (Re-)Configure an item in the recent ledgers list
	 * 
	 * @param pos
	 *          array index
	 * @param file
	 *          the file to add at the position or null to clear the position.
	 */
	private void configureRecentListEntry(int pos, File file) {
		recent[pos].setEnabled(file != null);
		recent[pos].setMnemonic(KeyStroke.getKeyStroke("" + (pos + 1)).getKeyCode());
		if (file == null) {
			recent[pos].setText((pos + 1) + ": ");
			recent[pos].setToolTipText("");
		}
		else {
			recent[pos].setText((pos + 1) + ": " + file.getName());
			// NOTE: It is meaningful to show the pathname as tooltip, so we get a
			// free ride here, abusing the tooltip as a datastorage to get the path to
			// the eventhandler. It's not clean, but it saves us some code.
			recent[pos].setToolTipText(file.getAbsolutePath());
		}
	}

	protected void setLoadingName(String name) {
		loading.setString(name);
	}

	/**
	 * Switch between info and loading screen.
	 * 
	 * @param busy
	 *          true to show the loading bar.
	 */
	protected void setLoading(boolean busy) {
		quit.setEnabled(!busy);
		addLedger.setEnabled(!busy);
		openLedger.setEnabled(!busy);
		openPublic.setEnabled(!busy);
		JPanel content = (JPanel) getContentPane();
		CardLayout cl = (CardLayout) content.getLayout();
		if (busy) {
			cl.show(content, LOADING_CARD);
		}
		else {
			cl.show(content, RUNNING_CARD);
			String message = MessageFormat.format(i18n.getString("readymessage"), server.getURI(),
					server.getURI());
			about.setText(message);
			WebAppContext wac = (WebAppContext) server.getHandler();
			LedgerConfig cfg = new LedgerConfig();
			File f = new File(wac.getInitParameter("ledger"));
			cfg.setLedger(f);
			setTitle("TradeTrax: " + f.getName());
			openPublic.setSelected(cfg.isPublicAccess());
		}
	}

	/**
	 * Create a file object for a ledger in the standard location.
	 * 
	 * @param name
	 *          ledger name (folder name)
	 * @return the directory object of the leder.
	 */
	protected static File pathForLedger(String name) {
		File f = new File(System.getProperty("app.homedir"), "ledgers");
		return new File(f, name);
	}

	/**
	 * Tell the system to open the ledger page in a webbrowser.
	 */
	private void openInBrowser() {
		try {
			Desktop.getDesktop().browse(server.getURI());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use SwingUtilities.invokeLater() to actually bring up the UI.
	 */
	public void run() {
		if (System.getProperty("app.skipwindow") != null) {
			setState(JFrame.ICONIFIED);
		}
		addWindowListener(this);
		about.addHyperlinkListener(this);
		quit.addActionListener(this);
		addLedger.addActionListener(this);
		openLedger.addActionListener(this);
		openPublic.addActionListener(this);
		for (JMenuItem mi : recent) {
			mi.addActionListener(this);
			mi.setEnabled(false);
		}
		pack();
		setSize(300, 200);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width - getWidth()) / 2;
		int y = (dim.height - getHeight()) / 2;
		setLocation(x, y);
		if (System.getProperty("app.nowindow") == null) {
			setVisible(true);
		}
		new OpenLedgerWorker(this, new LedgerConfig()).execute();
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			openInBrowser();
			setState(Frame.ICONIFIED);
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		Preferences prefs = Preferences.userNodeForPackage(PrefKeys.class);

		if (src == quit) {
			setVisible(false);
			System.exit(0);
		}

		if (src == addLedger) {
			String res = JOptionPane.showInputDialog(this, i18n.getString("addledger_message"),
					i18n.getString("addledger_title"), JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				LedgerConfig lc = new LedgerConfig();
				lc.setLedger(pathForLedger(res));
				new OpenLedgerWorker(this, lc).execute();
			}
		}

		if (src == openPublic) {
			WebAppContext wac = (WebAppContext) server.getHandler();
			LedgerConfig lc = new LedgerConfig();
			lc.setLedger(new File(wac.getInitParameter("ledger")));
			try {
				lc.setPublicAccess(openPublic.isSelected());
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			new OpenLedgerWorker(this, new LedgerConfig()).execute();
		}

		if (src == openLedger) {
			File last = new File(prefs.get(PrefKeys.LASTLEDGER, null));
			JFileChooser jfc = new JFileChooser(last);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				LedgerConfig cfg = new LedgerConfig();
				cfg.setLedger(jfc.getSelectedFile());
				new OpenLedgerWorker(this, cfg).execute();
			}
		}

		for (JMenuItem mi : recent) {
			if (mi == src) {
				LedgerConfig cfg = new LedgerConfig();
				cfg.setLedger(new File(mi.getToolTipText()));
				new OpenLedgerWorker(this, cfg).execute();
			}
		}
	}

	public void windowClosing(WindowEvent arg0) {
		setVisible(false);
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {

	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public void windowActivated(WindowEvent arg0) {
		if (System.getProperty("app.skipwindow") != null) {
			setState(Frame.ICONIFIED);
			openInBrowser();
		}
	}

	public void windowClosed(WindowEvent arg0) {
	}

	/**
	 * Start the application
	 * 
	 * @param args
	 *          do not care
	 */
	public static void main(String[] args) {
		File homedir = new File(System.getProperty("user.home"), "TradeTrax");
		System.setProperty("app.homedir", System.getProperty("app.homedir", homedir.getAbsolutePath()));
		Preferences prefs = Preferences.userNodeForPackage(PrefKeys.class);
		String randhmac = new BigInteger(130, new SecureRandom()).toString(32);
		String hmac = prefs.get(PrefKeys.HMACPASSPHRASE, randhmac);
		prefs.put(PrefKeys.HMACPASSPHRASE, hmac);
		System.setProperty("tapestry.hmac-passphrase", hmac);
		SwingUtilities.invokeLater(new StandaloneServer());
	}
}
