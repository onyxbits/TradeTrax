package de.onyxbits.tradetrax.main;

/**
 * Central place for storing the version information
 * 
 * @author patrick
 * 
 */
public class AppConstants {

	/**
	 * The human readable version name without any prefixes (e.g. "v") or suffixes
	 * (e.g. "-SNAPSHOT-DEV")
	 */
	public static final String VERSION;

	/**
	 * Init Parameter Name "Ledger": the parameter name by which to configure the
	 * ledger path in web.xml
	 */
	public static final String IPNLEDGERPATH = "ledger";

	static {
		String tmp = "UNKNOWN";
		try {
			// TODO: Ideally this would somehow come from pom.xml, but there doesn't
			// seem to be a good way to transport it from there that works for the JAR
			// as well as the WAR version.
			tmp = "1.4";
		}
		catch (Exception e) {
		}
		VERSION = tmp;
	}
}
