package de.onyxbits.tradetrax.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

@Entity
@Table(name = "variant")
public class Variant implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Row index
	 */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NonVisual
  private Long id;
  
  /**
   * Human readable value.
   */
  @Column(name = "label", unique=true)
  @Validate("required")
  private String label;
  
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
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
