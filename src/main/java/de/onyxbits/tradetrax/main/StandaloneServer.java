package de.onyxbits.tradetrax.main;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * A simple launcher for running a bundled web app without the need of an
 * external servlet container. This class will:
 * <ul>
 * </li>start an embedded Jetty server and bind the bundled app (exact location
 * can be configured by passing an URI)</li>
 * <li>start a mininmal Swing UI from which the user can access/destroy the
 * server.</li>
 * </ul>
 * <p>
 * A number of system properties are recognized: </dl>
 * <dt>app.background</
 * <dt>
 * <dd>Start only the server, but not the GUI, don't open a webbrowser</dd>
 * <dt>app.datadir</dt>
 * <dd>A directory in which to keep mutable files. This defaults to
 * $HOME/$APPNAME. Note: This the server is webapp agnostic and doesn't enforce
 * this directory in any way.</dd>
 * <dt>app.address</dt>
 * <dd>The URL under which the webapp can be reached. This defaults to
 * http://localhost:9090/ and should be overwritten with care.</dd>
 * <dt>tapestry.hmac-passphrase</dt>
 * <dd>Should be specified when when serving on the network.</dd> </dl>
 * 
 * @author patrick
 * 
 */
public class StandaloneServer extends JFrame implements Runnable, WindowListener, HyperlinkListener {

	private static final long serialVersionUID = 1L;
	private static ResourceBundle i18n = ResourceBundle.getBundle("StandaloneServer");

	private URI mainPage;
	private JEditorPane about;
	private Server server;
	private boolean running;

	private static final String[] ICONRESOURCES = {
			"appicon-16.png",
			"appicon-24.png",
			"appicon-32.png",
			"appicon-48.png",
			"appicon-64.png",
			"appicon-96.png",
			"appicon-128.png" };

	private StandaloneServer(URI mainPage) {
		// Build a minimal control UI.
		this.mainPage = mainPage;
		setState(Frame.ICONIFIED);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Object[] args = { i18n.getString("appname"), mainPage, mainPage };
		setTitle(args[0].toString());
		about = new HypertextPane(new MessageFormat(i18n.getString("body")).format(args));
		about.setMargin(new Insets(10, 10, 10, 10));
		setContentPane(new JScrollPane(about));
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

		// Init the server
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setHost(mainPage.getHost());
		connector.setPort(mainPage.getPort());
		server.addConnector(connector);
		WebAppContext root = new WebAppContext();
		SessionHandler sessionHandler = new SessionHandler();
		root.setSessionHandler(sessionHandler);
		root.setContextPath(mainPage.getPath());
		// DIRTY HACK: Compute a resource path that works with files on disk (in the
		// IDE) as well as in a jar.
		String dirtyhack = getClass().getResource("/WEB-INF/web.xml").toString();
		dirtyhack = dirtyhack.substring(0, dirtyhack.length() - "/WEB-INF/web.xml".length() + 1);
		root.setDescriptor(dirtyhack + "WEB-INF/web.xml");
		root.setResourceBase(dirtyhack);
		root.setParentLoaderPriority(true);
		server.setHandler(root);
	}

	private static void openInBrowser(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		}
		catch (Exception e) {
			// Shouldn't happen on any modern system, but if it does, at least tell
			// the user how to connect manually.
			JOptionPane.showMessageDialog(null, uri, i18n.getString("nobrowser"),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void run() {
		if (running) {
			try {
				// Called from the shutdown hook -> make sure to exit gracefully so
				// databases can be flushed if needed.
				server.stop();
				server.destroy();
				// no need to call System.exit() here
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// Called from main after starting the server. Check if the user actually
			// wants an UI.
			if (System.getProperty("app.background") == null) {
				addWindowListener(this);
				about.addHyperlinkListener(this);
				pack();
				setSize(300, 200);
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (dim.width - getWidth()) / 2;
				int y = (dim.height - getHeight()) / 2;
				setLocation(x, y);
				setVisible(true);
				openInBrowser(mainPage);
			}
			running = true;
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			openInBrowser(mainPage);
			setState(Frame.ICONIFIED);
		}
	}

	public void windowClosing(WindowEvent arg0) {
		setVisible(false);
		try {
			server.stop();
			server.destroy();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
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
		// Unless overriden by -Duser.datadir, use ${HOME}/${APPNAME} as datadir.
		// NOTE: this is a service for the webapp. It may ignore this property
		// altogether.
		File myDir = new File(System.getProperty("user.home"), i18n.getString("appname"));
		String dataDir = System.getProperty("app.datadir", myDir.getAbsolutePath());
		System.setProperty("app.datadir", dataDir);
		String address = System.getProperty("app.address", "http://localhost:9090");
		URI addr = null;
		try {
			addr = new URI(address);
		}
		catch (URISyntaxException e1) {
			JOptionPane.showMessageDialog(null, address, i18n.getString("badaddress"),
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			System.exit(1);
		}

		StandaloneServer ss = new StandaloneServer(addr);

		// Get rid of the hmac-alert. As long as only localhost connections are
		// served, we can do with a hardcoded string. If -Daddr is changed, this
		// must be customized as well.
		String hmac = System.getProperty("tapestry.hmac-passphrase", "secret string");
		System.setProperty("tapestry.hmac-passphrase", hmac);
		try {
			ss.server.start();
			SwingUtilities.invokeAndWait(ss);
			Runtime.getRuntime().addShutdownHook(new Thread(ss));
			ss.server.join();
		}
		catch (BindException e) {
			// Just assume that the port is blocked by another instance and the user
			// tried to access it by starting a new instance.
			openInBrowser(ss.mainPage);
		}
		catch (Exception e) {
			// In most cases, the user supplied something nonsensical via -Daddress
			JOptionPane.showMessageDialog(null, address, i18n.getString("cantrun"),
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
