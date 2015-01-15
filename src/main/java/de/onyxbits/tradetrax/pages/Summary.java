package de.onyxbits.tradetrax.pages;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.remix.TalliedStock;
import de.onyxbits.tradetrax.services.SettingsStore;

public class Summary {

	@Property
	private String totalInvestment;

	@Property
	private String totalProfit;

	@Property
	private String expectedProfit;

	@Property
	private int assetsOnHand;

	@Property
	private int itemsOnHand;

	@Inject
	private Session session;

	@Inject
	private Messages messages;

	@Inject
	private SettingsStore settingsStore;

	@Property
	private List<TalliedStock> usage;

	@Inject
	private BeanModelSource tallySource;

	@InjectPage
	private Index index;

	@Property
	private TalliedStock row;

	@Component(id = "show")
	private EventLink show;

	private HashMap<String, TalliedStock> tallied;

	public BeanModel<TalliedStock> getTallyModel() {
		BeanModel<TalliedStock> model = tallySource.createDisplayModel(TalliedStock.class, messages);
		List<String> lst = model.getPropertyNames();
		for (String s : lst) {
			PropertyModel nameColumn = model.getById(s);
			nameColumn.sortable(false);
		}
		return model;
	}

	protected Object onShow(String name) {
		if (name != null) {
			index.withNoFilters().withFilterName(name);
		}
		return index;
	}

	public String getCreated() {
		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
			return (df.format(Timestamp.valueOf(settingsStore.get(SettingsStore.CREATED, null))));
		}
		catch (Exception e) {

		}
		return "";
	}

	private TalliedStock getCounterFor(Stock s) {
		String tmp = s.getName().getLabel();
		TalliedStock ret = tallied.get(tmp);
		if (ret == null) {
			ret = new TalliedStock();
			ret.name = tmp;
			tallied.put(tmp, ret);
		}
		return ret;
	}

	public void setupRender() {
		// TODO: This is a rather heavy piece of code. It may be necessary to run it
		// in a background service and cache the results.
		usage = new Vector<TalliedStock>();
		tallied = new HashMap<String, TalliedStock>();
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		List<Stock> lst = session.createCriteria(Stock.class).list();
		long profit = 0;
		long investment = 0;
		long futureProfit = 0;
		for (Stock stock : lst) {
			if (stock.getAcquired() != null) {
				TalliedStock ts = getCounterFor(stock);
				if (stock.getLiquidated() == null) {
					long inv = stock.getBuyPrice() * stock.getUnitCount();
					investment += inv;
					assetsOnHand++;
					itemsOnHand += stock.getUnitCount();
					if (stock.getSellPrice() != 0) {
						futureProfit += (stock.getSellPrice() - stock.getBuyPrice()) * stock.getUnitCount();
					}
					ts.assetCount++;
					ts.totalUnits += stock.getUnitCount();
					ts.totalInvestmentCounter += inv;
				}
				else {
					long pro = (stock.getSellPrice() - stock.getBuyPrice()) * stock.getUnitCount();
					profit += pro;
					ts.totalProfitCounter += pro;
				}
			}
		}
		totalInvestment = mr.databaseToUser(investment, false, true);
		totalProfit = mr.databaseToUser(profit, false, true);
		expectedProfit = mr.databaseToUser(futureProfit, false, true);
		Iterator<String> it = tallied.keySet().iterator();
		while (it.hasNext()) {
			TalliedStock ts = tallied.get(it.next());
			ts.totalInvestment = mr.databaseToUser(ts.totalInvestmentCounter, false, true);
			ts.totalProfit = mr.databaseToUser(ts.totalProfitCounter, false, true);
			usage.add(ts);
		}
		Collections.sort(usage);

	}
}
