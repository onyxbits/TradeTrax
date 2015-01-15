package de.onyxbits.tradetrax.main;

/**
 * Central registry for preference storage keys.
 * @author patrick
 *
 */
public class PrefKeys {
	
	/**
	 * The last ledger that was opened
	 */
	public static final String LASTLEDGER = "lastledger";
	
	/**
	 * HMAC passphrase - needed by tapestry to encrypt data that is stored clientside.
	 */
	public static final String HMACPASSPHRASE = "hmacpassphrase";
	
	/**
	 * Network port to listen on
	 */
	public static final String PORT = "port";

}
