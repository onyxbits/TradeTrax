package de.onyxbits.tradetrax.services;

import java.sql.Timestamp;
import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.LogEntry;

/**
 * Periodically removes old log entries from the ledger log.
 * 
 * @author patrick
 * 
 */
public class LogPurger implements Runnable {

	private int retention = 1000 * 60 * 60 * 24 * 30;
	
	private HibernateSessionSource source;

	public LogPurger(HibernateSessionSource source) {
		this.source = source;
	}

	public void run() {
		Session session = source.create();
		session.clear();
		Timestamp ts = new Timestamp(System.currentTimeMillis() - retention);
		@SuppressWarnings("unchecked")
		List<LogEntry> lst = session.createCriteria(LogEntry.class).add(Restrictions.le("timestamp",ts)).list();
		if (lst.size() > 0) {
			session.beginTransaction();
			for (LogEntry e : lst) {
				session.delete(e);
			}
			session.getTransaction().commit();
		}
		session.disconnect();
		session.close();
	}
}
