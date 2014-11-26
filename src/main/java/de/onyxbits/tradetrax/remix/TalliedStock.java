package de.onyxbits.tradetrax.remix;

import org.apache.tapestry5.annotations.Property;

/**
 * Sums up how many assets and units there are of a given type.
 * 
 * @author patrick
 * 
 */
public class TalliedStock implements Comparable<TalliedStock>{

	@Property
	public String name;

	@Property
	public int assetCount;

	@Property
	public int totalUnits;

	@Property
	public String totalInvestment;
	
	@Property
	public String totalProfit;

	public long totalInvestmentCounter;
	
	public long totalProfitCounter;

	public int compareTo(TalliedStock arg0) {
		return name.compareTo(arg0.name);
	}

}
