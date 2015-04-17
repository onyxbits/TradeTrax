package de.onyxbits.tradetrax.main;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.prefs.Preferences;

/**
 * Just a data capsule for configuring what to load and where to make it
 * available.
 * 
 * @author patrick
 * 
 */
public class LedgerConfig {

	/**
	 * The presence of a file by this name in the ledger toggles network
	 * accessibility.
	 */
	public static final String PUBLICACCESS = "network.cfg";

	private InetSocketAddress address;
	private File ledger;

	private Preferences prefs;

	public LedgerConfig() {
		prefs = Preferences.userNodeForPackage(PrefKeys.class);
		File tmp = new File(prefs.get(PrefKeys.LASTLEDGER, StandaloneServer.pathForLedger("default")
				.getAbsolutePath()));

		if (System.getProperty("app.ledger") != null) {
			try {
				tmp = new File(System.getProperty("app.ledger"));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		setLedger(tmp);
	}

	public boolean isPublicAccess() {
		return new File(ledger, PUBLICACCESS).exists();
	}

	public void setPublicAccess(boolean open) throws IOException {
		File f = new File(ledger, PUBLICACCESS);
		if (open) {
			f.createNewFile();
		}
		else {
			f.delete();
		}
	}

	/**
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * @return the ledger
	 */
	public File getLedger() {
		return ledger;
	}

	public void setLedger(File ledger) {
		this.ledger = ledger;
		int port = prefs.getInt(PrefKeys.PORT, 1234);
		address = new InetSocketAddress("127.0.0.1", port);

		if (isPublicAccess()) {
			try {
				// Autodiscover a public IP address by making a pseudo connection to an
				// outside host and checking which network interface the OS would like
				// to route it through. This is not guaranteed to work, but in setups
				// where it doesn't, the user probably knows why and can force an
				// address.
				DatagramSocket s = new DatagramSocket();
				s.connect(InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }), 0);
				address = new InetSocketAddress(s.getLocalAddress(), port);
				s.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
