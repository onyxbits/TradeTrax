package de.onyxbits.tradetrax.pages.tools;

import java.text.DateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.LogEntry;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Display the log table.
 * 
 * @author patrick
 * 
 */
@Import(library = "context:js/mousetrap.min.js")
public class LedgerLog {

	@Property
	private LogEntry logEntry;

	@Property
	private LogEntry row;

	@Inject
	private Session session;

	@Inject
	private BeanModelSource logSource;

	@Inject
	private SettingsStore settingsStore;

	@Inject
	private Messages messages;

	@Property
	@Persist
	private String filter;

	@Component(id = "filter")
	private TextField filterField;

	@Component(id = "filterForm")
	private Form filterForm;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.MEDIUM);

	public String getFullTime() {
		return dateFormat.format(row.getTimestamp());
	}

	public BeanModel<LogEntry> getLogModel() {
		BeanModel<LogEntry> model = logSource.createDisplayModel(LogEntry.class, messages);
		List<String> lst = model.getPropertyNames();
		for (String s : lst) {
			try {
				PropertyModel nameColumn = model.getById(s);
				nameColumn.sortable(false);
			}
			catch (Exception e) {
			}
		}
		return model;
	}

	@SuppressWarnings("unchecked")
	public List<LogEntry> getLogentries() {
		Criteria ret = session.createCriteria(LogEntry.class).addOrder(Order.desc("timestamp"));
		if (filter != null && filter.length() != 0) {
			ret.add(Restrictions.ilike("details", "%" + filter + "%"));
		}
		return ret.list();
	}

	public LedgerLog withFilter(String filter) {
		this.filter = filter;
		return this;
	}

	public void afterRender() {
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport
				.addScript("Mousetrap.bind('esc', function() {window.history.back(); return false;});");
	}
}
