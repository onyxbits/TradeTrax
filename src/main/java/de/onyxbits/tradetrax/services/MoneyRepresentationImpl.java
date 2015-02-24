package de.onyxbits.tradetrax.services;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import de.onyxbits.jbee.Evaluator;

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
public class MoneyRepresentationImpl implements MoneyRepresentation {

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
	public MoneyRepresentationImpl(SettingsStore settings) {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.onyxbits.tradetrax.services.MoneyRepresention#getCurrencySymbol()
	 */
	public String getCurrencySymbol() {
		return settingsStore.get(SettingsStore.CURRENCYSYMBOL, currencySymbol);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.onyxbits.tradetrax.services.MoneyRepresention#userToDatabase(java.lang
	 * .String, int)
	 */
	public synchronized long userToDatabase(String value, int units) throws ParseException {
		if (value == null) {
			return 0;
		}
		try {
		  Evaluator e = new Evaluator();
		  BigDecimal val = e.evaluateOrThrow(value);
		  return val.divide(new BigDecimal(units)).multiply(new BigDecimal(FACTOR)).longValue();
		}
		catch (Exception e) {
			throw new ParseException(value,0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.onyxbits.tradetrax.services.MoneyRepresention#databaseToUser(long,
	 * boolean, boolean)
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
		String old = numberFormat.getMaximumFractionDigits() + "";
		String digits = settingsStore.get(SettingsStore.DECIMALS, old);
		if (!old.equals(digits)) {
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
