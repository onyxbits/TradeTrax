package de.onyxbits.tradetrax.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Logs system events
 * 
 * @author patrick
 * 
 */
@Entity
@Table(name = "log")
public class LogEntry implements Serializable {

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
	 * What happened? This should be a category label (e.g. "ASSET BOUGHT").
	 */
	private String what;

	/**
	 * When did it happen?
	 */
	@Type(type = "timestamp")
	private Date timestamp;

	/**
	 * Detail message. This should tell the user what actually happened.
	 */
	private String details;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the what
	 */
	public String getWhat() {
		return what;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param what the what to set
	 */
	public void setWhat(String what) {
		this.what = what;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}


}
