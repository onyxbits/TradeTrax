package de.onyxbits.tradetrax.remix;

import java.text.DateFormat;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.corelib.components.Grid;

import de.onyxbits.tradetrax.entities.Stock;

/**
 * A wrapper for {@link Stock} objects, so they can be easily shown in a
 * {@link Grid}.
 * 
 * @author patrick
 * 
 */
public class WrappedStock {

	@Property
	public String acquired;

	@Property
	public String liquidated;

	@Property
	public String name;

	@Property
	public String variant;

	@Property
	public String buyPrice;

	@Property
	public String sellPrice;

	@Property
	public String profit;

	@Property
	public int units;

	@NonVisual
	@Property
	public String buyPriceClass = MoneyRepresentation.PROFITCLASS;

	@NonVisual
	@Property
	public String sellPriceClass = MoneyRepresentation.PROFITCLASS;

	@NonVisual
	@Property
	public String profitClass = MoneyRepresentation.PROFITCLASS;

	@NonVisual
	@Property
	public Stock stock;

	@NonVisual
	@Property
	public Long variantId;

	@NonVisual
	@Property
	public Long nameId;
	
	@Property
	public String comment;
	
	@Property
	public String location;

	@Property
	public long id;

	/**
	 * A summary that is created dynamically in the TML. It's only declared here
	 * to not make the template engine complain about a missing field.
	 */
	@Property
	public String asset;

	private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	public WrappedStock(Stock stock, MoneyRepresentation moneyConverter) {
		this.stock = stock;
		id = stock.getId();
		units = stock.getUnitCount();

		if (stock.getName() != null) {
			name = stock.getName().getLabel();
			nameId = stock.getName().getId();
		}
		if (stock.getVariant() != null) {
			variant = stock.getVariant().getLabel();
			variantId = stock.getVariant().getId();
		}
		if (stock.getAcquired() != null) {
			buyPrice = moneyConverter.databaseToUser(stock.getBuyPrice(), false, false);
			acquired = dateFormat.format(stock.getAcquired());
			if (stock.getBuyPrice() < 0) {
				buyPriceClass = MoneyRepresentation.LOSSCLASS;
			}
		}
		if (stock.getLiquidated() != null) {
			sellPrice = moneyConverter.databaseToUser(stock.getSellPrice(), false, false);
			liquidated = dateFormat.format(stock.getLiquidated());
			if (stock.getSellPrice() < 0) {
				sellPriceClass = MoneyRepresentation.LOSSCLASS;
			}
		}
		if (acquired != null && liquidated != null) {
			long amount = (stock.getSellPrice() - stock.getBuyPrice()) * stock.getUnitCount();
			profit = moneyConverter.databaseToUser(amount, false, false);
			if (amount < 0) {
				profitClass = MoneyRepresentation.LOSSCLASS;
			}
		}
		comment = stock.getComment();
		location = stock.getLocation();
	}
}
