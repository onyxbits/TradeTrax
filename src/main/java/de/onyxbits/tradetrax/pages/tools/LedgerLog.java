package de.onyxbits.tradetrax.pages.tools;

import java.text.DateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.onyxbits.tradetrax.entities.LogEntry;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Display the log table.
 * 
 * @author patrick
 * 
 */
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
	
	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	private DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	
	public String getFullTime() {
		return dateFormat.format(row.getTimestamp())+ " "+timeFormat.format(row.getTimestamp());
	}
	
	public BeanModel<LogEntry> getLogModel() {
		BeanModel<LogEntry> model = logSource.createDisplayModel(LogEntry.class, messages);
		List<String> lst = model.getPropertyNames();
		for (String s : lst) {
			try {
			PropertyModel nameColumn = model.getById(s);
			nameColumn.sortable(false);
			}
			catch (Exception e){}
		}
		return model;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LogEntry> getLogentries() {
		return session.createCriteria(LogEntry.class).addOrder(Order.desc("timestamp")).list();
	}
}
