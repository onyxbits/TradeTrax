package de.onyxbits.tradetrax.remix;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import de.onyxbits.tradetrax.entities.Stock;

/**
 * For displaying the stocks table in a paged grid.
 * 
 * @author patrick
 * 
 */
public class StockPagedGridDataSource implements GridDataSource {

	private Session session;

	private List<Stock> preparedResults;

	private int startIndex;

	private Criterion nameRestriction;
	private Criterion variantRestriction;
	private LogicalExpression stateRestriction;
	private SimpleExpression acquisitionRestriction;
	private SimpleExpression liquidationRestriction;

	private Criterion locationRestriction;

	private Criterion commentRestriction;

	/**
	 * Construct a new object
	 * 
	 * @param session
	 *          database session from which to load objects.
	 * @param moneyConverter
	 *          for formatting money values.
	 */
	public StockPagedGridDataSource(Session session) {
		this.session = session;
		preparedResults = new Vector<Stock>();
	}

	/**
	 * Add a name restriction
	 * 
	 * @param label
	 *          (sub)string to match or null to disable name filtering.
	 * @return this reference for method chaining.
	 */
	public StockPagedGridDataSource withName(String label) {
		if (label != null) {
			// nameRestriction = Restrictions.like("name.label", "%"+label+"%");
			nameRestriction = Restrictions.ilike("name.label", "%" + label + "%");
		}
		else {
			nameRestriction = null;
		}
		return this;
	}

	/**
	 * Add a state restriction
	 * 
	 * @param filterState
	 *          the state or null to disable statefiltering
	 * @return this reference for method chaining.
	 */
	public StockPagedGridDataSource withState(StockState state) {
		if (state != null) {
			switch (state) {
				case ACQUIRED: {
					stateRestriction = Restrictions.and(Restrictions.isNotNull("acquired"),
							Restrictions.isNull("liquidated"));
					break;
				}
				case LIQUIDATED: {
					stateRestriction = Restrictions.and(Restrictions.isNull("acquired"),
							Restrictions.isNotNull("liquidated"));
					break;
				}
				case PREBOOKED: {
					stateRestriction = Restrictions.and(Restrictions.isNull("acquired"),
							Restrictions.isNull("liquidated"));
					break;
				}
				case FINALIZED: {
					stateRestriction = Restrictions.and(Restrictions.isNotNull("acquired"),
							Restrictions.isNotNull("liquidated"));
					break;
				}
			}
		}
		else {
			stateRestriction = null;
		}
		return this;
	}

	/**
	 * Add a variant restriction
	 * 
	 * @param label
	 *          (sub)string to match or null to disable variant filtering.
	 * @return this reference for method chaining.
	 */
	public StockPagedGridDataSource withVariant(String label) {
		if (label != null) {
			variantRestriction = Restrictions.ilike("variant.label", "%" + label + "%");
		}
		else {
			variantRestriction = null;
		}
		return this;
	}

	/**
	 * Add a comment restriction
	 * 
	 * @param comment
	 *          (sub) string to match or null to disable comment filtering.
	 * @return this reference for method chaining.
	 */
	public StockPagedGridDataSource withComment(String comment) {
		if (comment != null) {
			commentRestriction = Restrictions.ilike("comment", "%" + comment + "%");
		}
		else {
			commentRestriction = null;
		}
		return this;
	}

	/**
	 * Add a location restriction
	 * 
	 * @param filterLocation
	 *          (sub)string to match or null to disable location filtering.
	 * @return this reference for method chaining.
	 */
	public StockPagedGridDataSource withLocation(String location) {
		if (location != null) {
			locationRestriction = Restrictions.ilike("location", "%" + location + "%");
		}
		else {
			locationRestriction = null;
		}
		return this;
	}

	public StockPagedGridDataSource withAcquisition(Date filterAcquisition,
			TimeSpan filterAcquisitionSpan) {
		if (filterAcquisition == null || filterAcquisitionSpan == null) {
			acquisitionRestriction = null;
		}
		else {
			if (filterAcquisitionSpan == TimeSpan.BEFORE) {
				acquisitionRestriction = Restrictions.le("acquired", filterAcquisition);
			}
			else {
				acquisitionRestriction = Restrictions.ge("acquired", filterAcquisition);
			}
		}
		return this;
	}

	public StockPagedGridDataSource withLiquidation(Date filterLiquidation,
			TimeSpan filterLiquidationSpan) {
		if (filterLiquidation == null || filterLiquidationSpan == null) {
			liquidationRestriction = null;
		}
		else {
			if (filterLiquidationSpan == TimeSpan.BEFORE) {
				liquidationRestriction = Restrictions.le("liquidated", filterLiquidation);
			}
			else {
				liquidationRestriction = Restrictions.ge("liquidated", filterLiquidation);
			}
		}
		return this;
	}

	public int getAvailableRows() {
		Criteria crit = session.createCriteria(Stock.class);
		if (nameRestriction != null) {
			crit.createAlias("name", "name");
			crit.add(nameRestriction);
		}
		if (variantRestriction != null) {
			crit.createAlias("variant", "variant");
			crit.add(variantRestriction);
		}
		if (stateRestriction != null) {
			crit.add(stateRestriction);
		}
		if (acquisitionRestriction != null) {
			crit.add(acquisitionRestriction);
		}
		if (liquidationRestriction != null) {
			crit.add(liquidationRestriction);
		}
		if (locationRestriction != null) {
			crit.add(locationRestriction);
		}
		if (commentRestriction != null) {
			crit.add(commentRestriction);
		}
		return ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	private void attachOrder(Criteria crit, SortConstraint sc) {
		// NOTE: the logic of the columns we allow to be sort is inverse of the
		// database logic, hence asc becomes desc.
		switch (sc.getColumnSort()) {
			case ASCENDING: {
				crit.addOrder(Order.desc(sc.getPropertyModel().getId()));
				break;
			}
			case DESCENDING: {
				crit.addOrder(Order.asc(sc.getPropertyModel().getId()));
				break;
			}
			default: {
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {
		Criteria crit = session.createCriteria(Stock.class).setFirstResult(startIndex)
				.setMaxResults(endIndex - startIndex + 1);
		for (SortConstraint sc : sortConstraints) {
			if ("buyPrice".equals(sc.getPropertyModel().getId())) {
				attachOrder(crit, sc);
			}
			if ("acquired".equals(sc.getPropertyModel().getId())) {
				attachOrder(crit, sc);
			}
			if ("liquidated".equals(sc.getPropertyModel().getId())) {
				attachOrder(crit, sc);
			}
			if ("sellPrice".equals(sc.getPropertyModel().getId())) {
				attachOrder(crit, sc);
			}
		}
		if (sortConstraints.size() == 0) {
			crit.addOrder(Order.desc("acquired"));
		}
		crit.addOrder(Order.desc("id"));
		if (nameRestriction != null) {
			crit.createAlias("name", "name");
			crit.add(nameRestriction);
		}
		if (variantRestriction != null) {
			crit.createAlias("variant", "variant");
			crit.add(variantRestriction);
		}
		if (stateRestriction != null) {
			crit.add(stateRestriction);
		}
		if (acquisitionRestriction != null) {
			crit.add(acquisitionRestriction);
		}
		if (liquidationRestriction != null) {
			crit.add(liquidationRestriction);
		}
		if (locationRestriction != null) {
			crit.add(locationRestriction);
		}
		if (commentRestriction != null) {
			crit.add(commentRestriction);
		}
		this.preparedResults= crit.list();
		this.startIndex = startIndex;
	}

	public Object getRowValue(int index) {
		try {
			return preparedResults.get(index - startIndex);
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Class<Stock> getRowType() {
		return Stock.class;
	}
}
