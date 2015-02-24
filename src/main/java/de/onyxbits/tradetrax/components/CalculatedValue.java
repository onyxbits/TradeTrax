package de.onyxbits.tradetrax.components;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import de.onyxbits.jbee.Evaluator;

/**
 * Prints a mathematical expression and what it evaluates to.
 * 
 * @author patrick
 * 
 */
public class CalculatedValue {

	@Property
	@Parameter(required = true)
	private String input;

	@Property
	private String expression;

	@Property
	private String result;
	
	@Inject
	private Messages messages;

	public void beginRender() {
		expression = input;
		try {
			BigDecimal tmp = new Evaluator().evaluateOrThrow(input);
			result = DecimalFormat.getInstance().format(tmp);
		}
		catch (Exception e) {
			result = messages.get("error");
		}
	}
}