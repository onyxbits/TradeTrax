package de.onyxbits.tradetrax.remix;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Handles internal and external representation of monetary values as well as
 * parsing user input.
 * <p>
 * Since floating point numbers are prone to loss of precision, all money values
 * are stored as integers in the database. Fractional values are dealt with by
 * scaling the money value according to the number of fractional digits of the
 * desired currency.
 * 
 * @author patrick
 * 
 */
public class MoneyRepresentation {

	/**
	 * CSS class to use for formating a loss
	 */
	public static final String LOSSCLASS = "lossformat";

	/**
	 * CSS class to use for formating a profit
	 */
	public static final String PROFITCLASS = "profitformat";

	/**
	 * Scaling factor
	 */
	public static final long FACTOR = 10 * 10 * 10 * 10;

	private NumberFormat numberFormat;

	private NumberFormat numberFormatPrecise;
	private String currencySymbol;

	private SettingsStore settingsStore;

	/**
	 * Create a new converter
	 * 
	 * @param decimals
	 *          number of digits after the decimal point.
	 * 
	 * @param symbol
	 *          currency symbol to use
	 * 
	 * @param suffixSymbol
	 *          whether to append or prepend the currency symbol.
	 */
	public MoneyRepresentation(SettingsStore settings) {

		this.settingsStore = settings;
		Locale locale = Locale.getDefault();
		Currency c = Currency.getInstance(locale);
		numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(c.getDefaultFractionDigits());
		numberFormat.setMaximumFractionDigits(c.getDefaultFractionDigits());

		numberFormatPrecise = NumberFormat.getInstance(locale);
		numberFormatPrecise.setMinimumFractionDigits(c.getDefaultFractionDigits());
		numberFormatPrecise.setMaximumFractionDigits(4);
		currencySymbol = c.getSymbol();
	}

	/**
	 * @return the currencySymbol
	 */
	public synchronized String getCurrencySymbol() {
		return settingsStore.get(SettingsStore.CURRENCYSYMBOL, currencySymbol);
	}

	/**
	 * Converts a user submitted value to internal representation
	 * 
	 * @param value
	 *          a string such as "2.99", "4,99" or "-1"
	 * @param units
	 *          unitcount (in case the user is submitting the price for a stack of
	 *          items). No safety checks are performed. Especially not for
	 *          division by zero.
	 * @return the value as an integer
	 * @throws ParseException
	 */
	public synchronized long userToDatabase(String value, int units) throws ParseException {
		if (value == null) {
			return 0;
		}
		// FIXME: don't use double here!
		double val = numberFormatPrecise.parse(value).doubleValue();
		return (long) ((val / units) * FACTOR);
	}

	/**
	 * Convert from internal representation to human readable
	 * 
	 * @param value
	 *          an integer such as 499
	 * @param precise
	 *          false to clip digits, true to print the full value.
	 * @param addSymbol
	 *          true to add the currencysymbol
	 * @return a string such as $4.99
	 */
	public synchronized String databaseToUser(long value, boolean precise, boolean addSymbol) {
		String ret = "BUG!";
		checkSettings();
		if (precise) {
			ret = numberFormatPrecise.format(((double) value) / FACTOR);
		}
		else {
			ret = numberFormat.format(((double) value) / FACTOR);
		}
		if (addSymbol) {
			// FIXME: This is wrong! Some currencies put the symbol between number and
			// sign.
			ret += " " + getCurrencySymbol();
		}
		return ret;
	}

	private void checkSettings() {
		String digits = settingsStore.get(SettingsStore.DECIMALS, null);
		if (digits != null) {
			try {
				int count = Integer.parseInt(digits);
				numberFormat.setMaximumFractionDigits(count);
				numberFormat.setMinimumFractionDigits(count);
				numberFormatPrecise.setMinimumFractionDigits(count);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
