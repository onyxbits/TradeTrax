package de.onyxbits.tradetrax.remix;

import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.onyxbits.tradetrax.entities.LogEntry;

public class LogEntryPagedGridDataSource implements GridDataSource {

	private String filter;
	private Session session;
	private List<LogEntry> preparedResults;
	private int startIndex;

	public LogEntryPagedGridDataSource(Session session, String filter) {
		this.filter = filter;
		this.session = session;
	}

	public int getAvailableRows() {
		return ((Number) createCriteria().setProjection(Projections.rowCount()).uniqueResult())
				.intValue();
	}

	@SuppressWarnings("unchecked")
	public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {
		Criteria crit = createCriteria().addOrder(Order.desc("timestamp")).setFirstResult(startIndex)
				.setMaxResults(endIndex - startIndex + 1);
		this.preparedResults = crit.list();
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

	public Class<LogEntry> getRowType() {
		return LogEntry.class;
	}

	private Criteria createCriteria() {
		Criteria crit = session.createCriteria(LogEntry.class);
		if (filter != null) {
			crit.add(Restrictions.ilike("details", "%" + filter + "%"));
		}
		return crit;
	}

}
