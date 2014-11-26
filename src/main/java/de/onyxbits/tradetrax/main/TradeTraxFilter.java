package de.onyxbits.tradetrax.main;

import java.sql.Timestamp;

import javax.servlet.ServletException;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Servlet Bootup class
 * @author patrick
 *
 */
public class TradeTraxFilter extends TapestryFilter {

	protected void init(Registry registry) throws ServletException {
		
		SettingsStore ss = registry.getService(SettingsStore.class);
		if (ss.get(SettingsStore.CREATED,null)==null) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ss.set(SettingsStore.CREATED,ts.toString());
		}
	}
}
