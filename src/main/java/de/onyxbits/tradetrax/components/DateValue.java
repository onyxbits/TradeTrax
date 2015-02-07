package de.onyxbits.tradetrax.components;

import java.text.DateFormat;
import java.util.Date;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * A component for displaying a date object according to the user's locale
 * settings.
 * 
 * @author patrick
 * 
 */
public class DateValue {

	/**
	 * The page title, for the <title> element and the <h1>element.
	 */
	@Property
	@Parameter(required = true)
	private Date date;

	private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	public void beginRender(MarkupWriter writer) {
		if (date != null) {
			synchronized (dateFormat) {
				writer.write(dateFormat.format(date));
			}
		}
	}
}
