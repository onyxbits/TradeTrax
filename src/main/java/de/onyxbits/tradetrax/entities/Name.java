package de.onyxbits.tradetrax.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "name")
public class Name implements Serializable{
	
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
   * Human readable value.
   */
  @Column(name = "label", unique=true)
  private String label;
  
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

	/**
	 * @return the value
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param value the value to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
