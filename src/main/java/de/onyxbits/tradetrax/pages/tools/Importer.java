package de.onyxbits.tradetrax.pages.tools;

import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.IdentUtil;
import de.onyxbits.tradetrax.entities.Name;
import de.onyxbits.tradetrax.entities.Stock;
import de.onyxbits.tradetrax.entities.Variant;
import de.onyxbits.tradetrax.pages.Index;
import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.remix.WrappedStock;
import de.onyxbits.tradetrax.services.SettingsStore;

/**
 * Simple tool for batch importing CSV assets.
 * 
 * @author patrick
 * 
 */
public class Importer {

	@Inject
	private AlertManager alertManager;

	@Inject
	private SettingsStore settingsStore;

	@Inject
	private BeanModelSource ledgerSource;

	@Inject
	private Messages messages;

	@Inject
	private Session session;

	@Component(id = "csvForm")
	private Form csvForm;

	@Component(id = "rawcsvField")
	private TextArea rawcsvField;

	@Component(id = "commitForm")
	private Form commitForm;

	@Property
	@Persist
	private String rawcsv;

	@Property
	private WrappedStock row;

	private MoneyRepresentation moneyRepresentation;
	private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	@Persist
	private List<WrappedStock> parsed;

	public List<WrappedStock> getParsed() {
		return parsed;
	}

	public String getRowBuyPrice() {
		if (row != null && moneyRepresentation != null) {
			return moneyRepresentation.databaseToUser(row.stock.getBuyPrice(), false, false);
		}
		return null;
	}

	public String getRowSellPrice() {
		if (row != null && moneyRepresentation != null) {
			return moneyRepresentation.databaseToUser(row.stock.getSellPrice(), false, false);
		}
		return null;
	}

	public BeanModel<WrappedStock> getLedgerModel() {
		BeanModel<WrappedStock> model = ledgerSource.createDisplayModel(WrappedStock.class, messages);
		List<String> lst = model.getPropertyNames();
		for (String s : lst) {
			PropertyModel nameColumn = model.getById(s);
			nameColumn.sortable(false);
		}
		return model;
	}

	public void setupRender() {
		moneyRepresentation = new MoneyRepresentation(settingsStore);
		if (rawcsv == null || rawcsv.length() == 0) {
			rawcsv = "name,variant,acquired,cost,units,liquidated,returns";
		}
	}

	public Importer onSuccessFromCsvForm() {
		moneyRepresentation = new MoneyRepresentation(settingsStore);
		CSVParser parser = null;
		parsed = new Vector<WrappedStock>();
		try {
			parser = new CSVParser(new StringReader(rawcsv), CSVFormat.EXCEL.withHeader());
			for (CSVRecord rec : parser) {
				Stock stock = recordToStock(rec);
				if (stock != null) {
					parsed.add(new WrappedStock(stock, moneyRepresentation));
				}
			}
		}
		catch (Exception e) {

		}

		try {
			parser.close();
		}
		catch (Exception e) {
		}
		return this;
	}

	@CommitAfter
	public Object onSuccessFromCommitForm() {
		if (parsed == null) {
			return this;
		}
		int count = 0;
		for (WrappedStock stock : parsed) {
			// We have to repack name/variant here because it is highly likely that
			// the CSV contains a label several times which is not yet known to the
			// database. So we need a save() after every findName() to make sure we
			// don't violate the unique constraint.
			stock.stock.setName(IdentUtil.findName(session, stock.stock.getName().getLabel()));
			Variant v = stock.stock.getVariant();
			if (v != null) {
				stock.stock.setVariant(IdentUtil.findVariant(session, stock.stock.getVariant().getLabel()));
			}
			session.save(stock.stock);
			count++;
		}
		alertManager.alert(Duration.SINGLE, Severity.SUCCESS, messages.format("success", count));
		parsed = null;
		return Index.class;
	}

	private Stock recordToStock(CSVRecord rec) {
		try {
			Stock stock = new Stock();
			// A name is a must!
			Name name = new Name();
			name.setLabel(cleanName(rec.get("name")));
			stock.setName(name);

			if (rec.isMapped("variant") && rec.get("variant").length() > 0) {
				Variant variant = new Variant();
				String tmp = rec.get("variant");
				if (tmp != null) {
					variant.setLabel(tmp);
					stock.setVariant(variant);
				}
			}

			if (rec.isMapped("acquired")) {
				stock.setAcquired(parseDate(rec.get("acquired")));
			}
			if (rec.isMapped("cost")) {
				stock.setBuyPrice(parsePrice(rec.get("cost")));
			}
			if (rec.isMapped("units")) {
				stock.setUnitCount(parseAmount(rec.get("units")));
			}
			if (rec.isMapped("liquidated")) {
				stock.setLiquidated(parseDate(rec.get("liquidated")));
			}
			if (rec.isMapped("returns")) {
				stock.setSellPrice(parsePrice(rec.get("returns")));
			}
			return stock;
		}
		catch (Exception e) {
		}
		return null;
	}

	private String cleanName(String name) {
		if (name == null) {
			return null;
		}
		// The database is ok with any string, the UI is not, so prevent the user
		// from entering what cannot be edited.
		String ret = name.trim();
		if (ret.length() == 0) {
			return messages.format("noname");
		}
		return ret.replace('\n', ' ').replace('\t', ' ');
	}

	private int parseAmount(String string) {
		try {
			return Integer.valueOf(string);
		}
		catch (Exception e) {
		}
		return 1;
	}

	private long parsePrice(String string) {
		try {
			return moneyRepresentation.userToDatabase(string, 1);
		}
		catch (Exception e) {
		}
		return 0;
	}

	private Date parseDate(String string) {
		try {
			return Timestamp.valueOf(string);
		}
		catch (Exception e) {
		}
		try {
			return dateFormat.parse(string);
		}
		catch (Exception e) {
		}
		return null;
	}

}
