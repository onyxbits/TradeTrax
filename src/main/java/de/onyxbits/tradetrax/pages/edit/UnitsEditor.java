package de.onyxbits.tradetrax.pages.edit;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.services.EventLogger;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * An editor for splitting and merging {@link Stock}.
 * 
 * @author patrick
 * 
 */
public class UnitsEditor {

	@Property
	private long stockId;

	@Property
	private Stock stock;

	@Property
	private Stock row;

	@Inject
	private BeanModelSource ledgerSource;

	@Component(id = "splitForm")
	private Form splitform;

	@Property
	private int size;

	@Component(id = "sizeField")
	private TextField sizeField;

	@Inject
	private Session session;

	@Inject
	private AlertManager alertManager;

	@Inject
	private Messages messages;

	@Inject
	private EventLogger eventLogger;

	@Inject
	private SettingsStore settingsStore;

	@SuppressWarnings("unchecked")
	public List<Stock> getStocks() {
		Criteria crit = session.createCriteria(Stock.class);
		List<Criterion> lst = stock.allowedToMergeWith();
		for (Criterion c : lst) {
			crit.add(c);
		}
		return crit.list();
	}

	protected void onActivate(Long StockId) {
		this.stockId = StockId;
		stock = (Stock) session.get(Stock.class, stockId);
		size = 1;
	}

	public BeanModel<Stock> getLedgerModel() {
		BeanModel<Stock> model = ledgerSource.createDisplayModel(Stock.class, messages);
		List<String> lst = model.getPropertyNames();
		for (String s : lst) {
			PropertyModel nameColumn = model.getById(s);
			nameColumn.sortable(false);
		}
		return model;
	}

	@CommitAfter
	protected Object onSuccess() {
		try {
			Stock offspring = stock.splitStock(size);
			session.save(offspring);
			session.update(stock);
			eventLogger.split(stock, offspring);
		}
		catch (Exception e) {
			alertManager.alert(Duration.SINGLE, Severity.ERROR,
					messages.format("exception", e.getMessage()));
			e.printStackTrace();
		}
		return getClass();
	}

	@CommitAfter
	protected void onMerge(long id) {
		try {
			Stock m = (Stock) session.load(Stock.class, id);
			Stock backup = (Stock) session.load(Stock.class, id);

			// Hibernate doesn't like an update and a delete in the same transaction
			// when this leads to the same row in a OneToMany mapping. Since we are
			// deleting "m" anyways, we can stop the cascade simply by setting the
			// references to null.
			m.setName(null);
			m.setVariant(null);
			stock.setUnitCount(stock.getUnitCount() + m.getUnitCount());
			session.update(stock);
			session.delete(m);
			eventLogger.merged(stock, backup);
		}
		catch (Exception e) {
			e.printStackTrace();
			alertManager.alert(Duration.SINGLE, Severity.ERROR,
					messages.format("exception", e.getMessage()));
		}
	}

	protected Long onPassivate() {
		return stockId;
	}

}
