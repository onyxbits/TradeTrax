package de.onyxbits.tradetrax.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Stores user preferences in the database
 * 
 * @author patrick
 * 
 */
@Entity
@Table(name = "settings")
public class Setting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "name", unique = true)
	private String name;

	@Column(name = "value") 
	private String value;

	/**
	 * @return the key
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param key
	 *          the key to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *          the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
