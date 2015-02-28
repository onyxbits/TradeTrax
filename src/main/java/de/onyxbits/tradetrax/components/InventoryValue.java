package de.onyxbits.tradetrax.components;

import java.text.ChoiceFormat;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Print an inventory count consisting of assets and items.
 * 
 * @author patrick
 * 
 */
public class InventoryValue {

	@Property
	@Parameter(required = true)
	private int assetCount;

	@Property
	@Parameter(required = true)
	private int itemCount;
	
	@Property
	@Parameter
	private String none;

	@Inject
	private Messages messages;

	public void beginRender(MarkupWriter writer) {
		if (assetCount > 0) {
			double[] limits = { 1, 2 };
			String[] assets = {
					messages.get("assets.one"),
					messages.format("assets.multiple", assetCount) };
			String[] items = { messages.get("items.one"), messages.format("items.multiple", itemCount) };
			ChoiceFormat cfa = new ChoiceFormat(limits, assets);
			ChoiceFormat cfi = new ChoiceFormat(limits, items);
			writer.write(messages.format("in-stock", cfa.format(assetCount), cfi.format(itemCount)));
		}
		else {
			writer.write(none);
		}
	}
}