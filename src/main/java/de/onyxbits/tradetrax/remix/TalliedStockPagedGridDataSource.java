package de.onyxbits.tradetrax.remix;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

public class TalliedStockPagedGridDataSource implements GridDataSource {

	/**
	 * Column
	 */
	public static final String TOTALPROFIT = "totalprofit";

	/**
	 * Column
	 */
	public static final String TOTALINVESTMENT = "totalinvestment";

	/**
	 * Column
	 */
	public static final String AMOUNT = "amount";

	/**
	 * Column
	 */
	public static final String NAME = "name";

	private List<TalliedStock> data;

	public TalliedStockPagedGridDataSource(List<TalliedStock> data) {
		this.data = data;
	}

	public int getAvailableRows() {
		return data.size();
	}

	public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {
		if (sortConstraints.size() == 1) {
			SortConstraint sc = sortConstraints.get(0);
			boolean ascending = sc.getColumnSort() == ColumnSort.ASCENDING;
			int crit = TalliedStock.ONNAME;
			PropertyModel pm = sc.getPropertyModel();
			if (TOTALPROFIT.equals(pm.getId())) {
				crit = TalliedStock.ONPROFIT;
			}
			if (TOTALINVESTMENT.equals(pm.getId())) {
				crit = TalliedStock.ONINVESTMENT;
			}
			if (AMOUNT.equals(pm.getId())) {
				crit = TalliedStock.ONAMOUNT;
			}
			for (TalliedStock ts : data) {
				ts.sortBy(crit, ascending);
			}
			Collections.sort(data);
		}
	}

	public Object getRowValue(int index) {
		return data.get(index);
	}

	public Class<?> getRowType() {
		return TalliedStock.class;
	}

}
