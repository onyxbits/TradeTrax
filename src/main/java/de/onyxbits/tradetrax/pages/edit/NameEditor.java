package de.onyxbits.tradetrax.pages.edit;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.Name;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.services.EventLogger;

/**
 * A simple editor page for changing or deleting a {@link Name} object and
 * returning to the main page afterwards.
 * 
 * @author patrick
 * 
 */
public class NameEditor {

	@Property
	private long nameId;

	@Property
	@Validate("required,minlength=3")
	private String name;

	@Property
	private String status;

	@Component(id = "nameField")
	private TextField nameField;

	@Component(id = "editForm")
	private Form form;

	@Inject
	private Session session;

	@Inject
	private AlertManager alertManager;

	@Inject
	private Messages messages;
	
	@Inject
	private EventLogger eventLogger;
	
	@InjectPage
	private Index index;

	private boolean eventDelete;
	
	@Component(id="show")
  private EventLink show;
	
	protected Object onShow() {
		Name n = (Name) session.get(Name.class, nameId);
		if (n != null) {
			index.withNoFilters().withFilterName(n.getLabel());
		}
		return index;
	}
	
	protected void onActivate(Long nameId) {
		this.nameId = nameId;
	}
	
	protected void setupRender() {
		Name n = (Name) session.get(Name.class, nameId);
		if (n != null) {
			name = n.getLabel();
			status = messages.format("status-count",
					session.createCriteria(Stock.class).add(Restrictions.eq("name.id", this.nameId))
							.setProjection(Projections.rowCount()).uniqueResult());
		}
	}

	protected Long onPassivate() {
		return nameId;
	}

	public void onValidateFromEditForm() {
		if (name == null) {
			form.recordError(messages.get("validate-not-empty"));
		}
		else {
			if (name.trim().length() < name.length()) {
				form.recordError(messages.get("validate-not-trimmed"));
			}
		}
	}
	
	public void onSelectedFromDelete() {
		eventDelete=true;
	}
	
	public void onSelectedFromSave() {
		eventDelete=false;
	}
	
	@CommitAfter
	public Object onSuccess() {
		if (eventDelete) {
			doDelete();
		}
		else {
			doSave();
		}
		return Index.class;
	}

	private void doSave() {
		try {
			Name n = (Name) session.load(Name.class, nameId);
			String s = n.getLabel();
			n.setLabel(name);
			session.update(n);
			alertManager.alert(Duration.SINGLE, Severity.INFO, messages.format("renamed", s, name));
			eventLogger.rename(s,name);
		}
		catch (Exception e) {
			// TODO: Figure out how we got here and give the user better feedback
			alertManager.alert(Duration.SINGLE, Severity.WARN, messages.format("exception", e));
		}
	}
	

	private void doDelete() {
		try {
			Name bye = (Name) session.load(Name.class, nameId);
			@SuppressWarnings("unchecked")
			List<Stock> lst = session.createCriteria(Stock.class).add(Restrictions.eq("name", bye)).list();
			for (Stock s : lst) {
				s.setVariant(null);
				session.delete(s);
				eventLogger.deleted(s);
			}
			session.delete(bye);
			eventLogger.deleted(bye.getLabel());
			alertManager
					.alert(Duration.SINGLE, Severity.INFO, messages.format("deleted", bye.getLabel()));
		}
		catch (Exception e) {
			alertManager.alert(Duration.SINGLE, Severity.WARN, messages.format("exception", e));
		}
	}
}
