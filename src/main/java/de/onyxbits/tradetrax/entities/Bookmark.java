package de.onyxbits.tradetrax.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Allows for setting a market on a {@link Stock}
 * 
 * @author patrick
 * 
 */
@Entity
@Table(name = "bookmarks")
public class Bookmark implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * Reference to {@link Stock} id.
	 */
	@Id
	@Column(name = "id", unique = true)
	private long id;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
