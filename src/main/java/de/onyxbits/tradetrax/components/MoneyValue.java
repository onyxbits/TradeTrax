package de.onyxbits.tradetrax.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Convert a monetary value from database to user format and wrap it in a
 * loss/profit CSS container before printing it.
 * 
 * @author patrick
 * 
 */
public class MoneyValue {

	/**
	 * Monetary value in database format.
	 */
	@Parameter(required = true)
	private long amount;

	/**
	 * Include the currency symbol when printing?
	 */
	@Parameter
	private boolean addSymbol = true;

	/**
	 * Cut off decimals to get a fixed length fraction?
	 */
	@Parameter
	private boolean precise;

	@Property
	private String value;

	@Property
	private boolean loss;

	@Inject
	private SettingsStore settings;

	protected void beginRender() {
		MoneyRepresentation mr = new MoneyRepresentation(settings);
		value = mr.databaseToUser(amount, precise, addSymbol);
		loss = amount < 0;
	}
}