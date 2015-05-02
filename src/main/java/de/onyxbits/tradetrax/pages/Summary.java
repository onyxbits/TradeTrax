package de.onyxbits.tradetrax.pages;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.TalliedStock;
import de.onyxbits.tradetrax.remix.TalliedStockPagedGridDataSource;
import de.onyxbits.tradetrax.services.SettingsStore;

@Import(library = "context:js/mousetrap.min.js")
public class Summary {

	@Property
	private long totalInvestment;

	@Property
	private long totalProfit;

	@Property
	private long expectedProfit;

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
	
	@Property
	private int assetsOnHand;
	
	@Property
	private int itemsOnHand;

	private HashMap<String, TalliedStock> tallied;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Inject
	private PageRenderLinkSource linkSource;

	public BeanModel<Object> getTallyModel() {
		BeanModel<Object> model = tallySource.createDisplayModel(Object.class, messages);
		model.addEmpty(TalliedStockPagedGridDataSource.NAME).sortable(true);
		model.addEmpty(TalliedStockPagedGridDataSource.AMOUNT).sortable(true);
		model.addEmpty(TalliedStockPagedGridDataSource.TOTALINVESTMENT).sortable(true);
		model.addEmpty(TalliedStockPagedGridDataSource.TOTALPROFIT).sortable(true);
		return model;
	}

	public TalliedStockPagedGridDataSource getData() {
		return new TalliedStockPagedGridDataSource(usage);
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
		@SuppressWarnings("unchecked")
		List<Stock> lst = session.createCriteria(Stock.class).list();
		for (Stock stock : lst) {
			if (stock.getAcquired() != null) {
				TalliedStock ts = getCounterFor(stock);
				long buyPrice = stock.getBuyPrice();
				long sellPrice = stock.getSellPrice();
				int unitCount = stock.getUnitCount();
				if (stock.getLiquidated() == null) {
					long inv = buyPrice * unitCount;
					totalInvestment += inv;
					assetsOnHand++;
					itemsOnHand += unitCount;
					if (sellPrice != 0) {
						expectedProfit += (sellPrice - buyPrice) * unitCount;
					}
					ts.assetCount++;
					ts.totalUnits += unitCount;
					ts.totalInvestment += inv;
				}
				else {
					long pro = (sellPrice - buyPrice) * unitCount;
					totalProfit += pro;
					ts.totalProfit += pro;
				}
			}
		}
		Iterator<String> it = tallied.keySet().iterator();
		while (it.hasNext()) {
			TalliedStock ts = tallied.get(it.next());
			usage.add(ts);
		}
		Collections.sort(usage);
	}
	
	public void afterRender() {
		Link link = linkSource.createPageRenderLink(Index.class);
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport
				.addScript("Mousetrap.bind('esc', function() {window.location='"+link.toAbsoluteURI()+"'; return false;});");
	}
}
