package de.onyxbits.tradetrax.services;

/**
 * Wraps a key/value store that is backed by the database.
 * 
 * @author patrick
 * 
 */
public interface SettingsStore {

	/**
	 * The (visual) string that represents the currency of the ledger.
	 */
	public static final String CURRENCYSYMBOL = "currencysymbol";

	/**
	 * How many digits to display by default.
	 */
	public static final String DECIMALS = "decimals";

	/**
	 * Human readable name of the ledger
	 */
	public static final String LEDGERTITLE = "ledgertitle";

	/**
	 * Whether or not to show the instructionsblock in the sidebar.
	 */
	public static final String HIDEINSTRUCTIONS = "hideinstructions";
	
	/**
	 * Whether or not to show the calculator in the sidebar
	 */
	public static final String SHOWCALCULATOR = "showcalculator";
	
	/**
	 * When the ledger was created
	 */
	public static final String CREATED = "created";

	/**
	 * Get a setting
	 * 
	 * @param key
	 *          the kay of the setting
	 * @param value
	 *          default value if key does not exist
	 * @return the value
	 */
	public String get(String key, String value);

	/**
	 * Enter a setting into the storage
	 * 
	 * @param key
	 *          key name
	 * @param value
	 *          value
	 */
	public void set(String key, String value);
}
