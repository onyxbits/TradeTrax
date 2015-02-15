package de.onyxbits.tradetrax.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.onyxbits.tradetrax.services.MoneyRepresentation;

/**
 * Just print the ledger's currency symbol.
 * 
 * @author patrick
 * 
 */
public class CurrencysymbolValue {

	@Inject
	private MoneyRepresentation moneyRepresentation;

	public void beginRender(MarkupWriter writer) {
		writer.write(moneyRepresentation.getCurrencySymbol());
	}
}