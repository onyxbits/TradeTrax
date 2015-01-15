package de.onyxbits.tradetrax.main;

import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * A swingworker for starting a webserver on a specific ledger.
 * 
 * @author patrick
 * 
 */
public class OpenLedgerWorker extends SwingWorker<Exception, Object> {

	private StandaloneServer standaloneServer;
	private LedgerConfig ledgerConfig;

	public OpenLedgerWorker(StandaloneServer standaloneServer, LedgerConfig ledgerConfig) {
		this.standaloneServer = standaloneServer;
		this.ledgerConfig = ledgerConfig;
	}

	protected void process(List<Object> dontcare) {
		standaloneServer.setLoading(true);
		standaloneServer.setLoadingName(ledgerConfig.getLedger().getName());
	}

	protected void done() {
		try {
			Exception e = get();
			if (e != null) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				System.exit(1);
			}
		}
		catch (Exception ignore) {
		}
		standaloneServer.setLoading(false);
	}

	@Override
	protected Exception doInBackground() throws Exception {
		publish(new Object());
		try {
			standaloneServer.openLedger(ledgerConfig);
			Preferences prefs = Preferences.userNodeForPackage(PrefKeys.class);
			prefs.put(PrefKeys.LASTLEDGER, ledgerConfig.getLedger().getAbsolutePath());
		}
		catch (Exception e) {
			return e;
		}
		return null;
	}
}
