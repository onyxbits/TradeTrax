package de.onyxbits.tradetrax.services;

import java.text.ParseException;

/**
 * Takes care of internal/external representation of monetary values.
 * 
 * 
 * @author patrick
 * 
 */
public interface MoneyRepresentation {

	/**
	 * Scaling factor
	 */
	public static final long FACTOR = 10 * 10 * 10 * 10;

	/**
	 * @return the currencySymbol
	 */
	public abstract String getCurrencySymbol();

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
	public abstract long userToDatabase(String value, int units) throws ParseException;

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
	public abstract String databaseToUser(long value, boolean precise, boolean addSymbol);

}