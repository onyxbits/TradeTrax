package de.onyxbits.tradetrax.pages.edit;

import java.text.ParseException;
import java.util.Date;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionAttribute;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Session;

import de.onyxbits.tradetrax.components.Layout;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.remix.Payment;
import de.onyxbits.tradetrax.services.EventLogger;
import de.onyxbits.tradetrax.services.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

@Import(library = "context:js/mousetrap.min.js")
public class LiquidateEditor {

	@SessionAttribute(Layout.FOCUSID)
	private long focusedStockId;

	@Validate("required")
	@Property
	private Payment method;

	@Property
	private String sellPrice;

	@Property
	private long stockId;

	@Property
	private Stock stock;

	@Property
	private String status;

	@Component(id = "sellForm")
	private Form sellForm;

	@Inject
	private Session session;

	@Inject
	private EventLogger eventLogger;
	
	@Inject
	private MoneyRepresentation moneyRepresentation;

	@Inject
	private SettingsStore settingsStore;

	@Inject
	private AlertManager alertManager;

	@Inject
	private Messages messages;

	@Property
	@Persist
	@Validate("required")
	private Payment methods;


	@Property
	private boolean splitable;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;

	protected void onActivate(Long stockId) {
		this.stockId = stockId;
	}

	protected void setupRender() {
		stock = (Stock) session.get(Stock.class, stockId);
		if (stock != null) {
			sellPrice = moneyRepresentation.databaseToUser(stock.getSellPrice() * stock.getUnitCount(), false, false);
			splitable = stock.getUnitCount() > 1;
		}
	}

	protected Long onPassivate() {
		return stockId;
	}

	public void onValidateFromSellForm() {
		try {
			moneyRepresentation.userToDatabase(sellPrice, 1);
		}
		catch (ParseException e) {
			sellForm.recordError(messages.get("nan"));
		}
	}

	@CommitAfter
	protected Object onSuccessFromSellForm() {
		try {
			Stock s = (Stock) session.load(Stock.class, stockId);
			long sp = 0;
			if (methods == Payment.PERUNIT) {
				sp = moneyRepresentation.userToDatabase(sellPrice, 1);
			}
			else {
				sp = moneyRepresentation.userToDatabase(sellPrice, s.getUnitCount());
			}
			s.setLiquidated(new Date());
			s.setSellPrice(sp);
			focusedStockId = s.getId();
			String profit = moneyRepresentation.databaseToUser((s.getSellPrice() - s.getBuyPrice()) * s.getUnitCount(),
					false, true);
			alertManager.alert(Duration.SINGLE, Severity.SUCCESS,
					messages.format("liquidate-success", stockId, profit));
			eventLogger.liquidated(s);
		}
		catch (Exception e) {
			// TODO: Figure out how we got here and give the user better feedback
			e.printStackTrace();
			alertManager.alert(Duration.SINGLE, Severity.WARN, messages.format("internal error"));
			return null;
		}
		return Index.class;
	}
	
	public void afterRender() {
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport.addScript("Mousetrap.bind('esc', function() {window.history.back(); return false;});");
	}
}
