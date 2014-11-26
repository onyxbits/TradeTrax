package de.onyxbits.tradetrax.components;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SymbolConstants;

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

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	/**
	 * The page title, for the <title> element and the <h1>element.
	 */
	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
	private String title;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private Block instructions;

	@Property
	private boolean hideInstructions;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Property
	private long searchId = 0;

	@Property
	private String ledgerTitle;

	@Component(id = "searchId")
	private TextField searchIdField;

	@Component(id = "searchForm")
	private Form searchForm;

	@InjectPage
	private StockEditor stockEditor;

	@Inject
	private SettingsStore settingsStore;

	protected void setupRender() {
		try {
			searchId = focusedStockId;
		}
		catch (Exception e) {
			// Yes, this can happen (due to the annotation)! It's no biggie, though
		}
		ledgerTitle = settingsStore.get(SettingsStore.LEDGERTITLE, null);
		try {
			String tmp = settingsStore.get(SettingsStore.HIDEINSTRUCTIONS, null);
			hideInstructions = Boolean.parseBoolean(tmp);
		}
		catch (Exception e) {
		}
	}

	public Object onSuccessFromSearchForm() {
		stockEditor.onActivate(searchId);
		return stockEditor;
	}
}
