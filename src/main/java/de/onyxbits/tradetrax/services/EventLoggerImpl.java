package de.onyxbits.tradetrax.services;

import java.text.DateFormat;
import java.util.Date;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.Messages;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.LogEntry;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;

public class EventLoggerImpl implements EventLogger {

	private Messages messages;
	private Session session;
	private MoneyRepresentation moneyRepresentation;

	public EventLoggerImpl(HibernateSessionSource source, Messages messages, SettingsStore settings) {
		this.messages = messages;
		this.session = source.create();
		this.moneyRepresentation = new MoneyRepresentation(settings);
	}
	
	public void liquidated(Stock stock) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(messages.format("log-event-liquidated-details", stock.getId()));
		e.setWhat(messages.get("log-event-liquidated"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void acquired(Stock stock) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(messages.format("log-event-acquired-details", stock.getId()));
		e.setWhat(messages.get("log-event-acquired"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void split(Stock parent, Stock offspring) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(messages.format("log-event-split-details", offspring.getUnitCount(),
				parent.getId(), offspring.getId()));
		e.setWhat(messages.get("log-event-split"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void merged(Stock accumulator, Stock collected) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(messages.format("log-event-merged-details", collected.getUnitCount(),
				collected.getId(), accumulator.getId()));
		e.setWhat(messages.get("log-event-merged"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void deleted(Stock stock) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(format(stock));
		e.setWhat(messages.get("log-event-deleted"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void deleted(String name) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(name);
		e.setWhat(messages.get("log-event-deleted"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void modified(Stock orig) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(format(orig));
		e.setWhat(messages.get("log-event-modified"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	public void rename(String orig, String now) {
		LogEntry e = new LogEntry();
		e.setTimestamp(new Date());
		e.setDetails(messages.format("log-event-rename-details", orig, now));
		e.setWhat(messages.get("log-event-rename"));
		session.beginTransaction();
		session.save(e);
		session.getTransaction().commit();
	}

	private String format(Stock stock) {
		String name = "";
		if (stock.getName() != null) {
			name = stock.getName().getLabel();
		}
		String variant = "";
		if (stock.getVariant() != null) {
			variant = stock.getVariant().getLabel();
		}
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String acquisition = "";
		if (stock.getAcquired() != null) {
			acquisition = df.format(stock.getAcquired());
		}
		String liquidation = "";
		if (stock.getLiquidated() != null) {
			liquidation = df.format(stock.getLiquidated());
		}
		String comment = stock.getComment();
		if (comment == null) {
			comment = "";
		}
		return messages.format("log-stock", stock.getId(), name, variant, acquisition,
				moneyRepresentation.databaseToUser(stock.getBuyPrice(), true, true), stock.getUnitCount(),
				liquidation, moneyRepresentation.databaseToUser(stock.getSellPrice(), true, true), comment);
	}
	

}
