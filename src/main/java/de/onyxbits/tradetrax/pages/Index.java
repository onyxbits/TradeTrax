package de.onyxbits.tradetrax.pages;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Session;

import de.onyxbits.tradetrax.components.Layout;
import de.onyxbits.tradetrax.entities.IdentUtil;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.remix.StockPagedGridDataSource;
import de.onyxbits.tradetrax.remix.StockState;
import de.onyxbits.tradetrax.remix.TimeSpan;
import de.onyxbits.tradetrax.remix.WrappedStock;
import de.onyxbits.tradetrax.services.EventLogger;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Start page of application tradetracker.
 */
public class Index {

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	@Property
	private String currencySymbol;

	@Property
	@Persist
	private boolean showFilter;

	@Inject
	private Session session;

	@Inject
	private Messages messages;

	@Inject
	private BeanModelSource ledgerSource;

	@Property
	@Validate("required,minlength=3")
	private String buyName;

	@Property
	private String buyVariant;

	@Property
	private String buyPrice;

	@Property
	private int buyAmount;

	@Property
	private StockPagedGridDataSource stocks;

	@Property
	private WrappedStock wrappedStock;

	@Component(id = "buyform")
	private Form buyForm;

	@Component(id = "buyName")
	private TextField buyNameField;

	@Component(id = "buyVariant")
	private TextField buyVariantField;

	@Component(id = "buyAmount")
	private TextField buyAmountField;

	@Component(id = "buyPrice")
	private TextField buyPriceField;

	@Component(id = "ledger")
	private Grid ledger;

	@Component(id = "filterForm")
	private Form filterForm;

	@Component(id = "reset")
	private Submit reset;

	@Persist
	@Property
	private String filterName;

	@Component(id = "filterName")
	private TextField filterNameField;

	@Persist
	@Property
	private String filterVariant;

	@Component(id = "filterVariant")
	private TextField filterVariantField;

	@Persist
	@Property
	private StockState filterState;

	@Component(id = "filterState")
	private Select filterStateField;

	@Persist
	@Property
	private Date filterAcquisition;

	@Component(id = "filterAcquisition")
	private DateField filterAcquisitionField;

	@Persist
	@Validate("required")
	@Property
	private TimeSpan filterAcquisitionSpan;

	@Component(id = "filterAcquisitionSpan")
	private Select filterAcquisitionSpanField;

	@Persist
	@Property
	private Date filterLiquidation;

	@Component(id = "filterLiquidation")
	private DateField filterLiquidationField;

	@Persist
	@Validate("required")
	@Property
	private TimeSpan filterLiquidationSpan;

	@Component(id = "filterLiquidationSpan")
	private Select filterLiquidationSpanField;

	@Inject
	private SettingsStore settingsStore;
	
	@Inject
	private EventLogger eventLogger;

	public void setupRender() {
		buyAmount = 1;
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		stocks = new StockPagedGridDataSource(session, mr).withName(filterName)
				.withVariant(filterVariant).withState(filterState)
				.withAcquisition(filterAcquisition, filterAcquisitionSpan)
				.withLiquidation(filterLiquidation, filterLiquidationSpan);
		currencySymbol = mr.getCurrencySymbol();
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

	public List<String> onProvideCompletionsFromBuyVariant(String partial) {
		return IdentUtil.suggestVariants(session, partial);
	}

	public List<String> onProvideCompletionsFromBuyName(String partial) {
		return IdentUtil.suggestNames(session, partial);
	}

	public void onValidateFromBuyForm() {
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		try {
			mr.userToDatabase(buyPrice, 1);
		}
		catch (ParseException e) {
			buyForm.recordError(buyPriceField, messages.get("invalid-numberformat"));
		}
	}
	
	@CommitAfter
	public Object onSuccessFromBuyForm() {
		Stock item = new Stock();
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);

		item.setName(IdentUtil.findName(session, buyName));
		item.setVariant(IdentUtil.findVariant(session, buyVariant));
		try {
			item.setBuyPrice(mr.userToDatabase(buyPrice, 1));
		}
		catch (ParseException e) {
			// We already validated this
		}
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND,0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MINUTE,0);
		now.set(Calendar.HOUR_OF_DAY,0);

		item.setUnitCount(buyAmount);
		item.setAcquired(now.getTime());
		session.persist(item);
		focusedStockId = item.getId();
		eventLogger.acquired(item);
		return Index.class;
	}

	public void onSelectedFromReset() {
		// Reset button event -> return all values to their defaults...
		filterName = null;
		filterState = null;
		filterVariant = null;
		filterAcquisition = null;
		filterLiquidation = null;
		// ... then just fall through to the success action.
	}

	public Object onSuccessFromFilterForm() {
		return Index.class;
	}

	public Index withNoFilters() {
		filterName = null;
		filterVariant = null;
		showFilter = false;
		return this;
	}

	public Index withFilterName(String name) {
		this.filterName = name;
		showFilter = true;
		return this;
	}

	public Index withFilterVariant(String name) {
		this.filterVariant = name;
		showFilter = true;
		return this;
	}

}
