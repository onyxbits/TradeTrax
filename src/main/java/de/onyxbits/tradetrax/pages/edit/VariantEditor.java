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

import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.entities.Variant;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.services.EventLogger;

public class VariantEditor {

	@Property
	private long variantId;

	@Property
	@Validate("required")
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
	private EventLogger eventLogger;

	@Inject
	private AlertManager alertManager;

	@Inject
	private Messages messages;

	private boolean eventDelete;
	
	@InjectPage
	private Index index;
	
	@Component(id="show")
  private EventLink show;
	
	protected Object onShow() {
		Variant v = (Variant) session.get(Variant.class, variantId);
		if (v != null) {
			index.withNoFilters().withFilterVariant(v.getLabel());
		}
		return index;
	}
	
	protected void onActivate(Long variantId) {
		this.variantId = variantId;
	}
	
	protected void setupRender() {
		Variant v = (Variant) session.get(Variant.class, variantId);
		if (v != null) {
			name = v.getLabel();
			status = messages.format("status-count",
					session.createCriteria(Stock.class).add(Restrictions.eq("variant.id", this.variantId))
							.setProjection(Projections.rowCount()).uniqueResult());
		}
	}

	protected Long onPassivate() {
		return variantId;
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
	
	protected void doSave() {
		try {
			Variant variant = (Variant) session.get(Variant.class, variantId);
			String s = variant.getLabel();
			variant.setLabel(name);
			session.update(variant);
			alertManager.alert(Duration.UNTIL_DISMISSED, Severity.INFO, messages.format("renamed",s,name));
			eventLogger.rename(s,name);
		}
		catch (Exception e) {
			alertManager.alert(Duration.SINGLE, Severity.ERROR, messages.format("exception",e.getMessage()));
		}
	}

	protected void doDelete() {
		try {
			Variant bye = (Variant)session.get(Variant.class, variantId);
			@SuppressWarnings("unchecked")
			List<Stock> lst = session.createCriteria(Stock.class).add(Restrictions.eq("variant", bye)).list();
			for (Stock s : lst) {
				s.setVariant(null);
				session.update(s);
			}
			session.delete(bye);
			eventLogger.deleted(bye.getLabel());
		}
		catch (Exception e) {
			// Only two ways of getting here: trying to delete something that no
			// longer exists or never existed in the first place. No need to tell the
			// user that throwing away something non-existant failed.
		}
	}

}
