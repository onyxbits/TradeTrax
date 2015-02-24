package de.onyxbits.tradetrax.components;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SymbolConstants;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.onyxbits.jbee.Evaluator;
import de.onyxbits.tradetrax.entities.Bookmark;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.pages.edit.StockEditor;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Layout component for pages of application tradetracker.
 */
@Import(stylesheet = "context:layout/layout.css")
public class Layout {

	/**
	 * Use with @SessionAttribute to set the default value of the searchbox. This
	 * suggestion should be changed whenever the user is saving a stock related
	 * form that redirects to another page from which the id of the edited stock
	 * is no longer obtainable.
	 */
	public static final String FOCUSID = "focusedStockId";

	public static final String CALCRESULT = "calcresult";

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	@Inject
	private Session session;

	@Inject
	private Messages messages;

	/**
	 * The page title, for the <title> element and the <h1>element.
	 */
	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String title;

	/**
	 * A url to be used in the instructions block for getting more information.
	 */
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String helpurl;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private Block instructions;

	@Property
	private boolean hideInstructions;

	@Property
	private boolean showCalculator;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Property
	private String search = "0";

	@Property
	private String ledgerTitle;

	@Component(id = "search")
	private TextField searchField;

	@Component(id = "searchForm")
	private Form searchForm;

	@InjectPage
	private StockEditor stockEditor;

	@InjectPage
	private Index index;

	@Inject
	private SettingsStore settingsStore;

	@Property
	private Bookmark bookmark;

	@Property
	@Persist
	private String expression;

	@Property
	@SessionAttribute(Layout.CALCRESULT)
	private Vector<String> results;

	@Property
	private String result;

	@Component(id = "expression")
	private TextField expressionField;

	@Component(id = "calculatorform")
	private Form calculator;

	public List<Bookmark> getBookmarks() {
		return session.createCriteria(Bookmark.class).addOrder(Order.asc("id")).list();
	}

	protected void setupRender() {
		try {
			long l = focusedStockId; // make sure that we throw
			search = l + "";
		}
		catch (Exception e) {
			// Yes, this can happen (due to the annotation)! It's no biggie, though
			search = "0";
		}
		ledgerTitle = settingsStore.get(SettingsStore.LEDGERTITLE, null);
		try {
			String tmp = settingsStore.get(SettingsStore.HIDEINSTRUCTIONS, null);
			hideInstructions = Boolean.parseBoolean(tmp);
		}
		catch (Exception e) {
		}
		try {
			String tmp = settingsStore.get(SettingsStore.SHOWCALCULATOR, null);
			showCalculator = Boolean.parseBoolean(tmp);
		}
		catch (Exception e) {
		}
	}

	public Object onSuccessFromSearchForm() {
		try {
			stockEditor.onActivate(Long.parseLong(search));
			return stockEditor;
		}
		catch (Exception e) {
			index.withNoFilters();
			if (search != null) {
				index.withFilterName(search.trim());
			}
		}
		return index;
	}

	public void onSuccessFromCalculatorForm() {
		if (results == null) {
			results = new Vector<String>();
		}
		if (expression == null) {
			// People might want to clear the result list, but I don't want to clutter
			// the form with more controls than necessary. So we creatively interpret
			// an empty input as a wish to empty the list. This way we get around a
			// lot of extra code as well.
			results.clear();
			return;
		}
		try {
			BigDecimal result = new Evaluator().evaluateOrThrow(expression);
			while (results.size() >= 5) {
				results.remove(results.size() - 1);
			}
			results.add(0,
					messages.format("evaluated", expression, DecimalFormat.getInstance().format(result)));
		}
		catch (Exception exp) {
			results.add(0, messages.format("error", expression));
		}
	}
}
