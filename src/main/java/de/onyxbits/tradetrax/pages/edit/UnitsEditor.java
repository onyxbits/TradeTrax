package de.onyxbits.tradetrax.pages.edit;

import java.util.List;
import java.util.Vector;

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
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.remix.WrappedStock;
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
	private WrappedStock wrappedStock;
	
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
	private SettingsStore settingsStore;
	
	public List<WrappedStock> getStocks() {
		Vector<WrappedStock> ret = new Vector<WrappedStock>();
		MoneyRepresentation mr =  new MoneyRepresentation(settingsStore);
		try {
			Criteria crit = session.createCriteria(Stock.class);
			List<Criterion> lst = stock.allowedToMergeWith();
			for (Criterion c : lst) {
				crit.add(c);
			}
			@SuppressWarnings("unchecked")
			List<Stock> it = crit.list();
			for (Stock s : it) {
				ret.add(new WrappedStock(s,mr));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	protected void onActivate(Long StockId) {
		this.stockId = StockId;
		stock = (Stock) session.get(Stock.class, stockId);
		if (stock!=null) {
			size= stock.getUnitCount()/2;
		}
	}
	
	public BeanModel<WrappedStock> getLedgerModel() {
		BeanModel<WrappedStock> model = ledgerSource.createDisplayModel(WrappedStock.class, messages);
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
			session.save(stock.splitStock(size));
			session.update(stock);
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
			// Hibernate doesn't like an update and a delete in the same transaction
			// when this leads to the same row in a OneToMany mapping. Since we are
			// deleting "m" anyways, we can stop the cascade simply by setting the
			// references to null.
			m.setName(null);
			m.setVariant(null);
			stock.setUnitCount(stock.getUnitCount() + m.getUnitCount());
			session.update(stock);
			session.delete(m);
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
