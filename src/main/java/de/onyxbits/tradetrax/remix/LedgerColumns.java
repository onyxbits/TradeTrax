package de.onyxbits.tradetrax.remix;

import java.util.List;
import java.util.Vector;

/**
 * Used for configuring which stock columns to show in a ledger view.
 * 
 * @author patrick
 * 
 */
public enum LedgerColumns {

	ASSET("asset"), NAME("name"), VARIANT("variant"), ID("id"), ACQUIRED("acquired"), BUYPRICE(
			"buyPrice"), UNITS("units"), LIQUIDATED("liquidated"), SELLPRICE("sellPrice"), PROFIT(
			"profit"), COMMENT("comment"), LOCATION("location");

	public static final String DEFAULT = "asset, id, acquired, buyPrice, units, liquidated, sellPrice, profit";

	private String name;

	private LedgerColumns(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Dump a list of column names in csv format
	 * 
	 * @param lst
	 *          the columns
	 * @return a CSV string, suitable for serializing
	 */
	public static String toCsv(List<LedgerColumns> lst) {
		StringBuffer sb = new StringBuffer();
		for (LedgerColumns lc : lst) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(lc.getName());
		}
		return sb.toString();
	}

	/**
	 * Parse a csv string containing column names
	 * 
	 * @param csv
	 *          the string
	 * @return the columns specified in the string.
	 */
	public static List<LedgerColumns> fromCsv(String csv) {
		String[] tmp = csv.split(" *, *");
		Vector<LedgerColumns> ret = new Vector<LedgerColumns>();
		for (int i = 0; i < tmp.length; i++) {
			for (LedgerColumns lc : LedgerColumns.values()) {
				if (tmp[i].equals(lc.getName())) {
					ret.add(lc);
				}
			}
		}
		return ret;
	}
}
