package de.onyxbits.tradetrax.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * An item (or stack of items) in the inventory.
 * 
 * @author patrick
 * 
 */
@Entity
@Table(name = "stock")
public class Stock implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Row index
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Optional comment (free form text).
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * How much a single unit cost when buying it
	 */
	@Column(name = "buyprice")
	private long buyPrice;

	/**
	 * How much a single unit return when selling it.
	 */
	@Column(name = "sellprice")
	private long sellPrice;

	/**
	 * How many individual items are in this stack? Stacks may only be sold as a
	 * whole. In case a partial amount is to be sold, the Asset must be split into
	 * smaller stacks first.
	 * <p>
	 * Any integer number is valid. A value of zero might mean the user is
	 * tracking stock in refillable bins, a negative value might represent stock
	 * that was bought on margins.
	 */
	@Column(name = "stacksize")
	private int unitCount = 1;

	/**
	 * Human readable name of the asset
	 */
	@ManyToOne(cascade = { CascadeType.ALL })
	private Name name;

	/**
	 * Subtype (e.g. if the asset is available in multiple colors).
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	private Variant variant;

	/**
	 * Timestamp: when the asset was bought.
	 */
	@Column
	@Type(type = "timestamp")
	private Date acquired;

	/**
	 * Timestamp: when the asset was sold
	 */
	@Column
	@Type(type = "timestamp")
	private Date liquidated;

	public Stock() {
	}

	/**
	 * Create a stock using a template
	 */
	public Stock(Stock template) {
		acquired = template.acquired;
		buyPrice = template.buyPrice;
		comment = template.comment;
		id = template.id;
		liquidated = template.liquidated;
		if (template.name != null) {
			name = new Name();
			name.setId(template.name.getId());
			name.setLabel(template.name.getLabel());
		}
		sellPrice = template.sellPrice;
		unitCount = template.unitCount;
		if (template.variant != null) {
			variant = new Variant();
			variant.setId(template.variant.getId());
			variant.setLabel(template.variant.getLabel());
		}
	}

	/**
	 * Split a new Stock off from this one.
	 * 
	 * @param amount
	 *          number of units to transfer to the split off stock
	 * @return a new instance with the specified number of units.
	 */
	public Stock splitStock(int amount) {
		Stock ret = new Stock();
		if (acquired != null) {
			ret.acquired = (Date) acquired.clone();
		}
		ret.buyPrice = buyPrice;

		if (liquidated != null) {
			ret.liquidated = (Date) liquidated.clone();
		}
		ret.name = name;
		ret.sellPrice = sellPrice;
		ret.variant = variant;
		ret.unitCount = amount;
		unitCount -= amount;

		return ret;
	}

	/**
	 * Create the criteria for finding other stock items that are allowed to merge
	 * with this one. Two items may merge if they only differ in comment,
	 * unitcount and acquisition/liquidation date (but they need to be in the same
	 * state of acquisition/liquidation).
	 * 
	 * @return Hibernate criterias
	 */
	public List<Criterion> allowedToMergeWith() {
		Vector<Criterion> ret = new Vector<Criterion>();
		ret.add(Restrictions.ne("id", id));
		ret.add(Restrictions.eq("buyPrice", buyPrice));
		ret.add(Restrictions.eq("sellPrice", sellPrice));

		if (name == null) {
			// This should never happen
			ret.add(Restrictions.isNull("name"));
		}
		else {
			ret.add(Restrictions.eq("name.id", name.getId()));
		}
		if (variant == null) {
			ret.add(Restrictions.isNull("variant"));
		}
		else {
			ret.add(Restrictions.eq("variant.id", variant.getId()));
		}
		if (acquired == null) {
			ret.add(Restrictions.isNull("acquired"));
		}
		else {
			ret.add(Restrictions.isNotNull("acquired"));
		}
		if (liquidated == null) {
			ret.add(Restrictions.isNull("liquidated"));
		}
		else {
			ret.add(Restrictions.isNotNull("liquidated"));
		}

		return ret;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *          the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *          the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the buyPrice
	 */
	public long getBuyPrice() {
		return buyPrice;
	}

	/**
	 * @param buyPrice
	 *          the buyPrice to set
	 */
	public void setBuyPrice(long buyPrice) {
		this.buyPrice = buyPrice;
	}

	/**
	 * @return the sellPrice
	 */
	public long getSellPrice() {
		return sellPrice;
	}

	/**
	 * @param sellPrice
	 *          the sellPrice to set
	 */
	public void setSellPrice(long sellPrice) {
		this.sellPrice = sellPrice;
	}

	/**
	 * @return the unitCount
	 */
	public int getUnitCount() {
		return unitCount;
	}

	/**
	 * @param unitCount
	 *          the unitCount to set
	 */
	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}

	/**
	 * @return the name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(Name name) {
		this.name = name;
	}

	/**
	 * @return the variant
	 */
	public Variant getVariant() {
		return variant;
	}

	/**
	 * @param variant
	 *          the variant to set
	 */
	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	/**
	 * @return the aquired
	 */
	public Date getAcquired() {
		return acquired;
	}

	/**
	 * @param aquired
	 *          the aquired to set
	 */
	public void setAcquired(Date aquired) {
		this.acquired = aquired;
	}

	/**
	 * @return the liquidated
	 */
	public Date getLiquidated() {
		return liquidated;
	}

	/**
	 * @param liquidated
	 *          the liquidated to set
	 */
	public void setLiquidated(Date liquidated) {
		this.liquidated = liquidated;
	}
}
