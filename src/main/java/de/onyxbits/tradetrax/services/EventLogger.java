package de.onyxbits.tradetrax.services;

import de.onyxbits.tradetrax.entities.Stock;

/**
 * Keeps a record of what happened when in the ledger
 * 
 * @author patrick
 * 
 */
public interface EventLogger {

	/**
	 * Call this after selling off an asset.
	 * 
	 * @param stock
	 *          the stock after saving it to the database.
	 */
	public void liquidated(Stock stock);

	/**
	 * Call this after acquiring an asset.
	 * 
	 * @param stock
	 *          the stock after saving it to the database
	 */
	public void acquired(Stock stock);

	/**
	 * Call this after splitting a stock in two.
	 * 
	 * @param parent
	 *          the stock from which units were split off (after saving it to the
	 *          database)
	 * @param offspring
	 *          the newly created stock (after saving it to the database).
	 */
	public void split(Stock parent, Stock offspring);

	/**
	 * Call this after merging two assets
	 * 
	 * @param accumulator
	 *          the stock into which was merged ( after being saved to the
	 *          database).
	 * @param collected
	 *          the stock that was assimilated (after being saved to the
	 *          database).
	 */
	public void merged(Stock accumulator, Stock collected);

	/**
	 * Call this after a deleting a stock from the ledger
	 * 
	 * @param stock
	 *          the stock after it has been deleted.
	 */
	public void deleted(Stock stock);

	/**
	 * Call this after the user edited a stock
	 * 
	 * @param orig
	 *          the original state of the stock.
	 */
	public void modified(Stock orig);

	/**
	 * Rename a label
	 * 
	 * @param orig
	 *          old name
	 * @param now
	 *          new name
	 */
	public void rename(String orig, String now);

	/**
	 * Delete a label
	 * 
	 * @param name
	 *          the label to delete
	 */
	public void deleted(String name);

	/**
	 * Create a filter expression for finding log entries that concern a specific
	 * stock item.
	 * 
	 * @param stock
	 *          the stock to search the log for
	 * @return a string for grepping in the details messages.
	 */
	public String grep(Stock stock);

}
