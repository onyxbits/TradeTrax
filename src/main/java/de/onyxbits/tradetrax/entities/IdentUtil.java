package de.onyxbits.tradetrax.entities;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;


/**
 * Database utility functions for the {@link Name} and {@link Variant} classes.
 * @author patrick
 *
 */
public class IdentUtil {

	/**
	 * For autocompletion using the name table
	 * 
	 * @param session
	 *          database session to use
	 * @param partial
	 *          what the user entered so far
	 * @return list of matching labels
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<String> suggestNames(Session session, String partial) {
		Vector<String> ret = new Vector<String>();
		Criterion r = Restrictions.ilike("label", "%" + partial + "%");
		Iterator<Name> it = session.createCriteria(Name.class).add(r).list().iterator();
		while (it.hasNext()) {
			ret.add(it.next().getLabel());
		}
		return ret;
	}

	/**
	 * For autocompletion using the variant table
	 * 
	 * @param session
	 *          database session to use
	 * @param partial
	 *          what the user entered so far
	 * @return list of matching labels
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<String> suggestVariants(Session session, String partial) {
		Vector<String> ret = new Vector<String>();
		Criterion r = Restrictions.ilike("label", "%" + partial + "%");
		Iterator<Variant> it = session.createCriteria(Variant.class).add(r).list().iterator();
		while (it.hasNext()) {
			ret.add(it.next().getLabel());
		}
		return ret;
	}

	/**
	 * Fetch a {@link Name} object from the database. Create it if necessary.
	 * 
	 * @param session
	 *          database session
	 * @param label
	 *          the label of the {@link Name}
	 * @return a valid object for the given label or null if the label was null
	 */
	public static Name findName(Session session, String label) {
		if (label == null) {
			return null;
		}
		Name ret = (Name) session.createCriteria(Name.class).add(Restrictions.eq("label", label))
				.uniqueResult();
		if (ret == null) {
			ret = new Name();
			ret.setLabel(label);
		}
		return ret;
	}

	/**
	 * Fetch a {@link Variant} object from the database. Create it if necessary.
	 * 
	 * @param session
	 *          database session
	 * @param label
	 *          the label of the {@link Variant}
	 * @return a valid object for the given label or null if the label was null
	 */
	public static Variant findVariant(Session session, String label) {
		if (label == null) {
			return null;
		}
		Variant ret = (Variant) session.createCriteria(Variant.class)
				.add(Restrictions.eq("label", label)).uniqueResult();
		if (ret == null) {
			ret = new Variant();
			ret.setLabel(label);
		}
		return ret;
	}
}
