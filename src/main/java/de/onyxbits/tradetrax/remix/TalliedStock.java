package de.onyxbits.tradetrax.remix;

import org.apache.tapestry5.annotations.Property;

/**
 * Sums up how many assets and units there are of a given type.
 * 
 * @author patrick
 * 
 */
public class TalliedStock implements Comparable<TalliedStock> {

	/**
	 * Sorting criteria
	 */
	public static final int ONNAME = 0;

	/**
	 * Sorting criteria
	 */
	public static final int ONAMOUNT = 1;

	/**
	 * Sorting criteria
	 */
	public static final int ONPROFIT = 2;

	/**
	 * Sorting criteria
	 */
	public static final int ONINVESTMENT = 3;

	@Property
	public String name;

	@Property
	public int assetCount;

	@Property
	public int totalUnits;

	@Property
	public long totalInvestment;
	
	@Property
	public long totalProfit;

	private boolean ascending;

	private int criteria;

	public int compareTo(TalliedStock other) {
		int ret = 0;
		switch (criteria) {
			case ONAMOUNT: {
				if (ascending) {
					ret = compare(other.totalUnits, totalUnits);
				}
				else {
					ret = compare(totalUnits, other.totalUnits);
				}
				break;
			}
			case ONINVESTMENT: {
				if (ascending) {
					ret = compare(other.totalInvestment, totalInvestment);
				}
				else {
					ret = compare(totalInvestment, other.totalInvestment);
				}
				break;
			}
			case ONPROFIT: {
				if (ascending) {
					ret = compare(other.totalProfit, totalProfit);
				}
				else {
					ret = compare(totalProfit, other.totalProfit);
				}
				break;
			}
			default: {
				if (ascending) {
					ret = other.name.compareTo(name);
				}
				else {
					ret = name.compareTo(other.name);
				}
			}
		}
		return ret;
	}

	public void sortBy(int criteria, boolean ascending) {
		this.criteria = criteria;
		this.ascending = ascending;
	}

	/**
	 * Compares two {@code long} values.
	 * 
	 * @return 0 if lhs = rhs, less than 0 if lhs &lt; rhs, and greater than 0 if
	 *         lhs &gt; rhs.
	 */
	public static int compare(long lhs, long rhs) {
		return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
	}

}
