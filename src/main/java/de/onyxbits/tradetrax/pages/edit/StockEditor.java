package de.onyxbits.tradetrax.pages.edit;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionAttribute;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import de.onyxbits.tradetrax.components.Layout;
import de.onyxbits.tradetrax.entities.Bookmark;
import de.onyxbits.tradetrax.entities.IdentUtil;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.services.EventLogger;
import de.onyxbits.tradetrax.services.SettingsStore;

public class StockEditor {

	@Property
	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	@Component(id = "editForm")
	private Form editForm;

	@Property
	private long stockId;

	@Property
	private Stock stock;

	@Component(id = "units")
	private TextField unitsField;

	@Property
	private int units;

	@Component(id = "name")
	private TextField nameField;

	@Property
	@Validate("required,minlength=3")
	private String name;

	@Component(id = "variant")
	private TextField variantField;

	@Property
	private String variant;

	@Component(id = "buyPrice")
	private TextField buyPriceField;

	@Property
	private String buyPrice;

	@Component(id = "sellPrice")
	private TextField sellPriceField;

	@Property
	private String sellPrice;

	@Component(id = "acquired")
	private DateField acquiredField;

	@Property
	private Date acquired;

	@Component(id = "liquidated")
	private DateField liquidatedField;

	@Property
	private Date liquidated;

	@Component(id = "comment")
	private TextArea commentField;

	@Property
	private String comment;

	@Property
	private String currencySymbol;

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

	private boolean eventDelete;

	@Property
	private String balance;

	@Property
	private String balanceClass = MoneyRepresentation.PROFITCLASS;

	@Property
	private boolean splitable;

	@Property
	private boolean liquidateable;

	@Component(id = "bookmark")
	private EventLink bookmark;

	public void onActivate(Long StockId) {
		this.stockId = StockId;
	}

	@CommitAfter
	protected void setupRender() {
		try {
			MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
			currencySymbol = mr.getCurrencySymbol();
			stock = (Stock) session.get(Stock.class, stockId);
			if (stock == null) {
				stock = new Stock();
				stock.setAcquired(new Date());
				stock.setUnitCount(1);
				if (stockId > 0) {
					alertManager.alert(Duration.SINGLE, Severity.INFO, messages.format("not-found", stockId));
					Bookmark bm = (Bookmark) session.get(Bookmark.class, stockId);
					if (bm != null) {
						// This is a failsave in case something deletes stock but not
						// bookmarks. Normally we shouldn't get here.
						session.delete(bm);
					}
				}
			}
			if (stock.getName() != null) {
				name = stock.getName().getLabel();
			}
			if (stock.getVariant() != null) {
				variant = stock.getVariant().getLabel();
			}
			units = stock.getUnitCount();
			// NOTE: fill buyPrice/sellPrice even if acquired/liquidated is null. The
			// user might have entered expected prices!
			buyPrice = mr.databaseToUser(stock.getBuyPrice(), true, false);
			sellPrice = mr.databaseToUser(stock.getSellPrice(), true, false);
			acquired = stock.getAcquired();
			liquidated = stock.getLiquidated();
			liquidateable = (liquidated == null && stock.getId() > 0);
			comment = stock.getComment();
			splitable = stock.getUnitCount() > 1;
			long bal = 0;
			if (stock.getLiquidated() != null) {
				bal = stock.getSellPrice() * stock.getUnitCount();
			}
			if (stock.getAcquired() != null) {
				bal -= stock.getBuyPrice() * stock.getUnitCount();
			}
			balance = mr.databaseToUser(bal, false, true);
			if (bal < 0) {
				balanceClass = MoneyRepresentation.LOSSCLASS;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> onProvideCompletionsFromVariant(String partial) {
		return IdentUtil.suggestVariants(session, partial);
	}

	public List<String> onProvideCompletionsFromName(String partial) {
		return IdentUtil.suggestNames(session, partial);
	}

	protected Long onPassivate() {
		return stockId;
	}

	@CommitAfter
	protected StockEditor onBookmark(long id) {
		Bookmark bm = (Bookmark) session.get(Bookmark.class, id);
		if (bm == null) {
			alertManager.alert(Duration.SINGLE, Severity.INFO, messages.get("bookmark-set"));
			bm = new Bookmark();
			bm.setId(id);
			session.save(bm);
		}
		else {
			alertManager.alert(Duration.SINGLE, Severity.INFO, messages.get("bookmark-deleted"));
			session.delete(bm);
		}
		return this;
	}

	protected void onSelectedFromDelete() {
		eventDelete = true;
	}

	protected void onSelectedFromSave() {
		eventDelete = false;
	}

	protected void onSelectedFromCreate() {
		eventDelete = false;
	}

	public void onValidateFromEditForm() {
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		try {
			mr.userToDatabase(buyPrice, 1);
		}
		catch (ParseException e) {
			editForm.recordError(buyPriceField, messages.get("nan"));
		}
		try {
			mr.userToDatabase(sellPrice, 1);
		}
		catch (ParseException e) {
			editForm.recordError(sellPriceField, messages.get("nan"));
		}
	}

	protected Object onSuccessFromEditForm() {
		if (eventDelete) {
			return doDelete();
		}
		else {
			return doSave();
		}
	}

	@CommitAfter
	private Object doSave() {
		Object ret = null;
		try {
			stock = (Stock) session.get(Stock.class, stockId);
			Stock backup = null;
			if (stock == null) {
				stock = new Stock();
			}
			else {
				backup = new Stock(stock);
			}
			MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
			stock.setName(IdentUtil.findName(session, name));
			stock.setVariant(IdentUtil.findVariant(session, variant));
			stock.setAcquired(acquired);
			stock.setLiquidated(liquidated);
			stock.setSellPrice(mr.userToDatabase(sellPrice, 1));
			stock.setBuyPrice(mr.userToDatabase(buyPrice, 1));
			stock.setUnitCount(units);
			stock.setComment(comment);
			session.saveOrUpdate(stock);
			alertManager.alert(Duration.SINGLE, Severity.SUCCESS,
					messages.format("save-success", stock.getId()));
			focusedStockId = stock.getId();
			if (backup == null) {
				eventLogger.acquired(stock);
				ret = this;
			}
			else {
				eventLogger.modified(backup);
				ret = Index.class;
			}
		}
		catch (Exception e) {
			alertManager.alert(Duration.SINGLE, Severity.ERROR,
					messages.format("exception", e.getMessage()));
		}
		return ret;
	}

	@CommitAfter
	private Object doDelete() {
		try {
			Stock bye = (Stock) session.get(Stock.class, stockId);
			long byeid = bye.getId();
			bye.setName(null);
			bye.setVariant(null);
			session.delete(bye);
			focusedStockId = 0;
			Bookmark bm = (Bookmark) session.get(Bookmark.class, byeid);
			if (bm != null) {
				session.delete(bm);
			}
			alertManager.alert(Duration.SINGLE, Severity.SUCCESS,
					messages.format("delete-success", byeid));
			eventLogger.deleted(bye);
		}
		catch (Exception e) {
			alertManager.alert(Duration.SINGLE, Severity.ERROR,
					messages.format("exception", e.getMessage()));
			// Only two ways of getting here: trying to delete something that no
			// longer exists or never existed in the first place. No need to tell the
			// user that throwing away something non-existant failed.
		}
		return Index.class;
	}

}
