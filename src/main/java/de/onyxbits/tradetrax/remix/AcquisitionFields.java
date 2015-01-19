package de.onyxbits.tradetrax.remix;

import java.util.List;
import java.util.Vector;

public enum AcquisitionFields {
			
	BUYVARIANT("buyVariant"), BUYLOCATION("buyLocation"), BUYAMOUNT("buyAmount"), BUYCOST("buyCost"), BUYRETURNS(
			"buyReturns"), BUYADVANCED("buyAdvanced");

	public static final String DEFAULT = "buyVariant, buyAmount, buyCost"; 
	
	private String name;

	private AcquisitionFields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static String toCsv(List<AcquisitionFields> lst) {
		StringBuffer sb = new StringBuffer();
		for (AcquisitionFields lc : lst) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(lc.getName());
		}
		return sb.toString();
	}

	public static List<AcquisitionFields> fromCsv(String csv) {
		String[] tmp = csv.split(" *, *");
		Vector<AcquisitionFields> ret = new Vector<AcquisitionFields>();
		for (int i = 0; i < tmp.length; i++) {
			for (AcquisitionFields lc : AcquisitionFields.values()) {
				if (tmp[i].equals(lc.getName())) {
					ret.add(lc);
				}
			}
		}
		return ret;
	}
}
