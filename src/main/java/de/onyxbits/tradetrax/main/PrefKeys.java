package de.onyxbits.tradetrax.main;

/**
 * Central registry for preference storage keys.
 * 
 * @author patrick
 * 
 */
public class PrefKeys {

	/**
	 * The last ledger that was opened
	 */
	public static final String LASTLEDGER = "lastledger";

	/**
	 * HMAC passphrase - needed by tapestry to encrypt data that is stored
	 * clientside.
	 */
	public static final String HMACPASSPHRASE = "hmacpassphrase";

	/**
	 * Prefix for the recently opened ledger list. The actual key must have a
	 * number appended (starting at 0).
	 */
	public static final String RECENT = "recent.";

	/**
	 * Network port to listen on
	 */
	public static final String PORT = "port";

}
