package de.onyxbits.tradetrax.components;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.EventLink;

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.pages.Index;

/**
 * Prints a summary of a {@link WrappedStock}.
 * 
 * @author patrick
 * 
 */
public class SummaryValue {
	
	@Component(id = "filterByName")
	private EventLink filterByNameLink;

	@Component(id = "filterByVariant")
	private EventLink filterByVariantLink;

	@Property
	@Parameter(required=true)
	private Stock stock;
	
	@InjectPage
	private Index index;
	

	public Object onFilterByVariant(String name) {
		if (name==null || name.equals(index.hasFilterVariant())) {
			return index.withFilterVariant(null);
		}
		else {
			return index.withFilterVariant(name);
		}
	}

	public Object onFilterByName(String name) {
		if (name==null || name.equals(index.hasFilterName())) {
			return index.withFilterName(null);
		}
		else {
			return index.withFilterName(name);
		}
	}
	
	
}