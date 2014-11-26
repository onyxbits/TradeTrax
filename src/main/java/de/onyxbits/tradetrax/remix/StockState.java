package de.onyxbits.tradetrax.remix;

/**
 * For filtering stock according to it's inventory status.
 * @author patrick
 *
 */
public enum StockState {

	/**
	 * Neither aquired nor liquidated ("on the wishlist") 
	 */
	PREBOOKED, 
	
	/**
	 * Aquired, but not liquidated ("waiting to be sold")
	 */
	ACQUIRED, 
	
	/**
	 * Liquidated, but NOT aquired (to support things like 'futures')
	 */
	LIQUIDATED, 
	
	/**
	 * Aquired and Liquidated (archieved).
	 */
	FINALIZED
}
