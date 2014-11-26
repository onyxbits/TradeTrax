package de.onyxbits.tradetrax.pages.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Prints a simple pricelist consisting of asset name, variant (if available)
 * and expected retail price (stock.sellprice). Only "on hand" items (aquired,
 * but not liquidated) are listed. Since the list is meant to be shared with
 * buyers via email or other text only communication channels, unitcounts are
 * not included (wouldn't be meaningful to tell buyer A and B that X units are
 * available if they buy independently).
 * 
 * @author patrick
 * 
 */
public class Pricelist {

	@Property
	@Persist
	private String pricelist;

	@Inject
	private Session session;

	@Inject
	private SettingsStore settingsStore;

	@Inject
	private Messages messages;
	
	@Component(id="print")
	private EventLink print;
	
	public StreamResponse onPrint() {
		if (pricelist == null) {
			// e.g. because the user had this bookmarked
			setupRender();
		}
		return new TextStreamResponse("text/plain",pricelist);
	}

	public void setupRender() {

		@SuppressWarnings("unchecked")
		List<Stock> lst = session.createCriteria(Stock.class).add(Restrictions.isNotNull("acquired"))
				.add(Restrictions.isNull("liquidated")).add(Restrictions.ge("unitCount",1)).list();
		if (lst.size() == 0) {
			pricelist = messages.get("empty");
			return;
		}

		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		HashMap<String, Long> seen = new HashMap<String, Long>();
		StringBuilder sb = new StringBuilder();
		int longestEntry = 0;
		int longestPrice = 0;

		for (Stock stock : lst) {
			sb.setLength(0);
			sb.append(stock.getName().getLabel());
			if (stock.getVariant() != null) {
				sb.append(", ");
				sb.append(stock.getVariant().getLabel());
			}
			// Figure out how much padding we need
			if (sb.length() > longestEntry) {
				longestEntry = sb.length();
			}
			String tmp = mr.databaseToUser(stock.getSellPrice(), false, true);
			if (tmp.length() > longestPrice) {
				longestPrice = tmp.length();
			}
			// It is entirely possible that we have the same asset with multiple sell
			// prices in the database. In that case, we should only list it once and
			// with the highest offer.
			Long price = seen.get(sb.toString());
			if (price == null) {
				seen.put(sb.toString(), stock.getSellPrice());
			}
			else {
				if (stock.getSellPrice() > price.longValue()) {
					seen.put(sb.toString(), stock.getSellPrice());
				}
			}
		}

		sb.setLength(0);
		String[] keys = seen.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		for (String key : keys) {
			String tmp = mr.databaseToUser(seen.get(key), false, true);
			int pad = longestEntry - key.length() + longestPrice - tmp.length() + 2;
			sb.append(key);
			for (int i = 0; i < pad; i++) {
				sb.append(" ");
			}
			sb.append(tmp);
			sb.append("\n");
		}

		pricelist = sb.toString();
	}

}
