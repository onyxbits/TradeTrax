package de.onyxbits.tradetrax.pages.tools;

import java.sql.Timestamp;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.LogEntry;
import de.onyxbits.tradetrax.remix.LogEntryPagedGridDataSource;
import de.onyxbits.tradetrax.remix.PurgeType;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Display the log table.
 * 
 * @author patrick
 * 
 */
@Import(library = "context:js/mousetrap.min.js")
public class LedgerLog {

	private static final long DAY = 1000l * 60l * 60l * 24l;

	@Property
	private LogEntry logEntry;

	@Property
	private LogEntry row;

	@Property
	private PurgeType purgeType;

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

	@Property
	private boolean showPurgeForm;

	@Component(id = "filter")
	private TextField filterField;

	@Component(id = "filterForm")
	private Form filterForm;

	@Component(id = "purgeForm")
	private Form purgeForm;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private AlertManager alertManager;

	@Property
	private LogEntryPagedGridDataSource logs;

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

	public LedgerLog withFilter(String filter) {
		this.filter = filter;
		return this;
	}

	public void setupRender() {
		logs = new LogEntryPagedGridDataSource(session, filter);
		showPurgeForm = logs.getAvailableRows() > 0;
	}

	public void afterRender() {
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport
				.addScript("Mousetrap.bind('esc', function() {window.history.back(); return false;});");
	}

	@CommitAfter
	public void onSuccessFromPurgeForm() {
		if (purgeType == null) {
			return;
		}

		Criteria crit = session.createCriteria(LogEntry.class);

		switch (purgeType) {
			case SHOWING: {
				if (filter != null) {
					crit.add(Restrictions.ilike("details", "%" + filter + "%"));
				}
				break;
			}
			case YEAR: {
				Timestamp ts = new Timestamp(System.currentTimeMillis() - DAY * 365l);
				crit.add(Restrictions.lt("timestamp", ts)).list();
				break;
			}
			case MONTH: {
				Timestamp ts = new Timestamp(System.currentTimeMillis() - DAY * 30l);
				crit.add(Restrictions.lt("timestamp", ts)).list();
				break;
			}
			case WEEK: {
				Timestamp ts = new Timestamp(System.currentTimeMillis() - DAY * 7l);
				crit.add(Restrictions.lt("timestamp", ts)).list();
				break;
			}
		}

		@SuppressWarnings("unchecked")
		List<LogEntry> lst = crit.list();
		if (lst.size() > 0) {
			for (LogEntry e : lst) {
				session.delete(e);
			}
		}
		String[] s = {
				messages.get("feedback.0"),
				messages.get("feedback.1"),
				messages.getFormatter("feedback.x").format(lst.size())
		};
		double[] limits = {0,1,2};
		alertManager.alert(Duration.SINGLE, Severity.INFO,new ChoiceFormat(limits,s).format(lst.size()));

	}
}
