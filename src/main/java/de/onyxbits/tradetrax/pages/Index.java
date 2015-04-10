package de.onyxbits.tradetrax.pages;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Session;

import de.onyxbits.tradetrax.components.Layout;
import de.onyxbits.tradetrax.entities.IdentUtil;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.AcquisitionFields;
import de.onyxbits.tradetrax.remix.LedgerColumns;
import de.onyxbits.tradetrax.remix.StockPagedGridDataSource;
import de.onyxbits.tradetrax.remix.StockState;
import de.onyxbits.tradetrax.remix.TimeSpan;
import de.onyxbits.tradetrax.services.EventLogger;
import de.onyxbits.tradetrax.services.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Start page of application tradetracker.
 */
@Import(library = "context:js/mousetrap.min.js")
public class Index {

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	@Property
	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

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
	private String buyCost;

	@Property
	private String buyReturns;

	@Property
	private String buyLocation;

	@Property
	private int buyAmount = 1;

	@Property
	private StockPagedGridDataSource stocks;

	@Property
	private Stock row;

	@Component(id = "buyform")
	private Form buyForm;

	@Component(id = "buyName")
	private TextField buyNameField;

	@Component(id = "buyVariant")
	private TextField buyVariantField;

	@Component(id = "buyAmount")
	private TextField buyAmountField;

	@Component(id = "buyCost")
	private TextField buyCostField;

	@Component(id = "buyReturns")
	private TextField buyReturnsField;

	@Component(id = "buyLocation")
	private TextField buyLocationFiels;

	@Component(id = "ledger")
	private Grid ledger;

	@Component(id = "filterForm")
	private Form filterForm;

	@Component(id = "reset")
	private Submit reset;

	@Persist
	@Property
	private String filterName;

	@Persist
	@Property
	private String filterLocation;

	@Component(id = "filterLocation")
	private TextField filterLocationField;

	@Persist
	@Property
	private String filterComment;

	@Component(id = "filterComment")
	private TextField filterCommentField;

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

	@Inject
	private MoneyRepresentation moneyRepresentation;

	@Property
	private String matches;

	@Inject
	private Block acquisitionblock;

	@Inject
	private Block filterblock;

	@InjectComponent
	private Zone flipview;

	@Property
	@Persist
	private boolean showFilterForm;

	@Property
	private long matchingItemCount;

	@Property
	@Persist
	private boolean autofocusBuyForm;

	@Property
	private int matchingAssetCount;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	public String styleFor(String tag) {
		String tmp = settingsStore.get(SettingsStore.TCACFIELDS, AcquisitionFields.DEFAULT);
		if (!tmp.contains(tag)) {
			return "display:none;";
		}
		return "";
	}

	public Block getActiveForm() {
		if (showFilterForm) {
			return filterblock;
		}
		else {
			return acquisitionblock;
		}
	}

	public void setupRender() {
		stocks = new StockPagedGridDataSource(session).withName(filterName).withVariant(filterVariant)
				.withState(filterState).withLocation(filterLocation).withComment(filterComment)
				.withAcquisition(filterAcquisition, filterAcquisitionSpan)
				.withLiquidation(filterLiquidation, filterLiquidationSpan);
		matchingAssetCount = stocks.getAvailableRows();
		matchingItemCount = stocks.getItemCount();
	}

	public void afterRender() {
		autofocusBuyForm = false;

		// Let the Escape key toggle the forms. It is slightly messy to do it this
		// way. Using getElementById() would be preferable, but the id is assigned
		// dynamically.
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport
				.addScript("Mousetrap.bind('esc', function() {document.getElementsByClassName('formtoggler')[0].click();});");
	}

	public BeanModel<Object> getLedgerModel() {
		BeanModel<Object> model = ledgerSource.createDisplayModel(Object.class, messages);
		List<LedgerColumns> tmp = LedgerColumns.fromCsv(settingsStore.get(SettingsStore.TCLCOLUMNS,
				LedgerColumns.DEFAULT));
		for (LedgerColumns col : tmp) {
			model.addEmpty(col.getName()).sortable(
					LedgerColumns.BUYPRICE.getName().equals(col.getName())
							|| LedgerColumns.SELLPRICE.getName().equals(col.getName())
							|| LedgerColumns.LIQUIDATED.getName().equals(col.getName())
							|| LedgerColumns.ACQUIRED.getName().equals(col.getName()));
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
		try {
			moneyRepresentation.userToDatabase(buyCost, 1);
		}
		catch (ParseException e) {
			buyForm.recordError(buyCostField, messages.get("invalid-numberformat"));
		}
		try {
			moneyRepresentation.userToDatabase(buyReturns, 1);
		}
		catch (ParseException e) {
			buyForm.recordError(buyReturnsField, messages.get("invalid-numberformat"));
		}
	}

	public Object onToggleForm() {
		showFilterForm = !showFilterForm;
		return flipview;
	}

	@CommitAfter
	public Object onSuccessFromBuyForm() {
		Stock item = new Stock();

		item.setName(IdentUtil.findName(session, buyName));
		item.setVariant(IdentUtil.findVariant(session, buyVariant));
		try {
			item.setBuyPrice(moneyRepresentation.userToDatabase(buyCost, 1));
			item.setSellPrice(moneyRepresentation.userToDatabase(buyReturns, 1));
		}
		catch (ParseException e) {
			// We already validated this
		}
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.HOUR_OF_DAY, 0);

		item.setLocation(buyLocation);
		item.setUnitCount(buyAmount);
		item.setAcquired(now.getTime());
		session.persist(item);
		focusedStockId = item.getId();
		eventLogger.acquired(item);
		withNoFilters();
		ledger.reset();
		autofocusBuyForm = true;
		return Index.class;
	}

	public void onSelectedFromReset() {
		// Reset button event -> return all values to their defaults...
		filterName = null;
		filterState = null;
		filterVariant = null;
		filterAcquisition = null;
		filterLiquidation = null;
		filterLocation = null;
		filterComment = null;
		ledger.reset();
		// ... then just fall through to the success action.
	}

	public Object onSuccessFromFilterForm() {
		return Index.class;
	}

	public Index withNoFilters() {
		filterName = null;
		filterState = null;
		filterVariant = null;
		filterAcquisition = null;
		filterLiquidation = null;
		filterLocation = null;
		filterComment = null;
		showFilterForm = false;
		return this;
	}

	public Index withFilterName(String name) {
		this.filterName = name;
		showFilterForm = true;
		return this;
	}

	public Index withFilterVariant(String name) {
		this.filterVariant = name;
		showFilterForm = true;
		return this;
	}

	public String hasFilterName() {
		return filterName;
	}

	public String hasFilterVariant() {
		return filterVariant;
	}

}
