package de.onyxbits.tradetrax.pages.tools;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.Name;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.entities.Variant;
import de.onyxbits.tradetrax.remix.LabelActions;
import de.onyxbits.tradetrax.services.EventLogger;

/**
 * An index of all labels in the system with the option to make them canonical
 * 
 * @author patrick
 * 
 */
public class LabelManager {

	@Inject
	private Session session;
	
	@Inject
	private AlertManager alertManager;
	
	@Inject
	private Messages messages;

	@Inject
	private EventLogger eventLogger;

	@Property
	private Name name;

	@Property
	private Variant variant;

	@Property
	private LabelActions actions;

	@Property
	private boolean applyToNames;

	@Property
	private boolean applyToVariants;

	@Component(id = "applyToNames")
	private Checkbox applyToNamesField;

	@Component(id = "applyToVariants")
	private Checkbox applyToVariantsField;

	@Component(id = "actionForm")
	private Form actionForm;

	@Property
	private int affected;

	@SuppressWarnings("unchecked")
	public List<Name> getNames() {
		return session.createCriteria(Name.class).addOrder(Order.asc("label")).list();
	}

	@SuppressWarnings("unchecked")
	public List<Variant> getVariants() {
		return session.createCriteria(Variant.class).addOrder(Order.asc("label")).list();
	}

	public void setupRender() {
		affected=0;
	}

	@CommitAfter
	public LabelManager onSuccessFromActionForm() {
		if (actions == LabelActions.TRIM) {
			trimLabels();
			alertManager.alert(Duration.SINGLE,Severity.INFO,messages.format("deleted",affected));
		}
		else {
			sanitizeLabels();
			alertManager.alert(Duration.SINGLE,Severity.INFO,messages.format("updated",affected));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	private void sanitizeLabels() {
		if (applyToNames) {
			List<Name> lst = session.createCriteria(Name.class).list();
			for (Name n : lst) {
				switch (actions) {
					case UPPERCASE: {
						String s = n.getLabel().toUpperCase().trim();
						if (!s.equals(n.getLabel())) {
							eventLogger.rename(n.getLabel(), s);
							n.setLabel(s);
							affected++;
						}
						break;
					}
					case LOWERCASE: {
						String s = n.getLabel().toLowerCase().trim();
						if (!s.equals(n.getLabel())) {
							eventLogger.rename(n.getLabel(), s);
							n.setLabel(s);
							affected++;
						}
						break;
					}
					case CAPITALIZE: {
						String s = n.getLabel().toLowerCase().trim();
						if (s.length() >= 1) {
							s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
						}
						if (!s.equals(n.getLabel())) {
							eventLogger.rename(n.getLabel(), s);
							n.setLabel(s);
							affected++;
						}
					}
					default:
						break;
				}
				session.update(n);
			}
		}
		if (applyToVariants) {
			List<Variant> lst = session.createCriteria(Variant.class).list();
			for (Variant v : lst) {
				switch (actions) {
					case UPPERCASE: {
						String s = v.getLabel().toUpperCase().trim();
						if (!s.equals(v.getLabel())) {
							eventLogger.rename(v.getLabel(), s);
							v.setLabel(s);
							affected++;
						}
						break;
					}
					case LOWERCASE: {
						String s = v.getLabel().toLowerCase().trim();
						if (!s.equals(v.getLabel())) {
							eventLogger.rename(v.getLabel(), s);
							v.setLabel(s);
							affected++;
						}
						break;
					}
					case CAPITALIZE: {
						String s = v.getLabel().toLowerCase().trim();
						if (s.length() >= 1) {
							s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
						}
						if (!s.equals(v.getLabel())) {
							eventLogger.rename(v.getLabel(), s);
							v.setLabel(s);
							affected++;
						}
					}
					default:
						break;
				}
				session.update(v);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void trimLabels() {
		// Yes, this is super inefficient, but these tables are not that big
		if (applyToNames) {
			List<Name> lst = session.createCriteria(Name.class).list();
			for (Name n : lst) {
				long count = (Long) session.createCriteria(Stock.class)
						.add(Restrictions.eq("name.id", n.getId())).setProjection(Projections.rowCount())
						.uniqueResult();
				if (count < 1) {
					session.delete(n);
					eventLogger.deleted(n.getLabel());
					affected++;
				}
			}
		}
		if (applyToVariants) {
			List<Variant> lst = session.createCriteria(Variant.class).list();
			for (Variant v : lst) {
				long count = (Long) session.createCriteria(Stock.class)
						.add(Restrictions.eq("variant.id", v.getId())).setProjection(Projections.rowCount())
						.uniqueResult();
				if (count < 1) {
					session.delete(v);
					eventLogger.deleted(v.getLabel());
					affected++;
				}
			}
		}
	}
}
