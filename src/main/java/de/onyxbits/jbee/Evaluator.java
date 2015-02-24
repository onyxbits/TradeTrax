/**
 * 
 *  Copyright 2015 Patrick Ahlbrecht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.onyxbits.jbee;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * The frontend class of JBEE and potentially the only one the average
 * application developer will have to deal with. In the most basic use case,
 * evaluating an expression is a simple matter of:
 * <p>
 * 
 * <pre>
 * <code>System.out.println(new Evaluator().evaluate("3,000 * 4.0 + 0xFF"))</code>
 * </pre>
 * <p>
 * This will parse expressions using the number format of the default locale. A
 * more sophisticated example would be:
 * <p>
 * 
 * <pre>
 * <code>
 *   DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMANY);
 *   Evaluator e = new Evaluator(df, new FunctionCatalogMathLib());
 *   System.out.println(e.evaluate("3.000 * 4,0 + 0xFF - pow(2;4)"));
 * </code>
 * </pre>
 * <p>
 * This example will force the german locale for number parsing and also bind a
 * basic catalog of functions (by default, the function catalog is empty). Note
 * that functions use the semicolon instead of the comma as a list separator.
 * This is a necessity since the comma and full stop character may both appear
 * in decimal numbers.
 * 
 * @author patrick
 * 
 */
public final class Evaluator {

	private DecimalFormat format;
	private MathLib mathLib;
	private long timeout;

	/**
	 * Standard constructor, using the {@link DecimalFormat} of the default
	 * {@link Locale}, an instance of {@link DefaultMathLib} and no timeout.
	 */
	public Evaluator() {
		this(new DecimalFormat(), new DefaultMathLib());
	}

	/**
	 * Construct an Evaluator with a custom {@link DecimalFormat}, {@link MathLib}
	 * and no timeout.
	 * 
	 * @param format
	 *          the object to use for parsing decimal numbers.
	 * @param mathLib
	 *          custom {@link MathLib}
	 */
	public Evaluator(DecimalFormat format, MathLib mathLib) {
		this.format = format;
		this.mathLib = mathLib;
		if (format == null || mathLib == null) {
			throw new NullPointerException();
		}
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced in an
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, BigDecimal value) {
		mathLib.map(name, value);
		return this;
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, byte value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, short value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, int value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, long value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, double value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced inan
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, String value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced in an
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 * @return this reference for method chaining.
	 */
	public Evaluator map(String name, float value) {
		return map(name, new BigDecimal(value));
	}

	/**
	 * Bind a collection of numbers to symbols.
	 * 
	 * @param all
	 *          the symbols
	 * @return this reference for method chaining.
	 */
	public Evaluator mapAll(HashMap<String, BigDecimal> all) {
		Set<String> keys = all.keySet();
		for (String key : keys) {
			map(key, all.get(key));
		}
		return this;
	}

	/**
	 * Clear all mapped variables.
	 */
	public void clearMappings() {
		mathLib.clearMappings();
	}

	/**
	 * Convenience method for creating a symbol/value mapping from a source
	 * string. A single mapping in the source takes the format
	 * <code>symbol=number</code>. Mappings may be separated by semicolon,
	 * linebreak, semicolon and linebreak or any amount of spaces. Furthermore,
	 * the source may contain c-style line end comments, for example:
	 * 
	 * <pre>
	 * <code>
	 * // This is legal
	 * name1=1 name2=2
	 * // This is legal as well
	 * name3 = 3; name4 = 4;
	 * // No need to be consistent
	 * name5=5 name6=6; 
	 * </code>
	 * </pre>
	 * 
	 * Unlike the other map methods, this method will check for the symbol name to
	 * be legal.
	 * 
	 * @param src
	 *          string to parse
	 * @return symbol value mapping
	 * @throws ParseException
	 *           if src cannot be parsed.
	 */
	public HashMap<String, BigDecimal> createMapping(String src) throws ParseException {
		DeclarationParser p = new DeclarationParser(new Lexer(format, src));
		p.yyparse();
		return p.declarations;
	}

	/**
	 * Check if a value is bound.
	 * 
	 * @param name
	 *          name of a variable
	 * @return it's value or null if not mapped.
	 */
	public BigDecimal valueOf(String name) {
		return mathLib.onLookup(name);
	}

	/**
	 * Evaluate an expression, throw on error. This method may be called
	 * repeatedly with different expression. Note that the throws clause only
	 * lists the exceptions that can occur in the default implementation. Almost
	 * all aspects of the parser can be overriden through a custom {@link MathLib}
	 * which may or may not introduce additional/different exception. Therefore, a
	 * try/catch block around this method should always include a general clause
	 * to catch {@link RuntimeException}.
	 * 
	 * @param expression
	 *          the expression to evaluate
	 * @return the number the expression evaluates to.
	 * @throws ArithmeticException
	 *           if the expression won't evaluate (e.g. division by zero, syntax
	 *           error, ...)
	 * @throws NotDefinedException
	 *           if the expression references a function or variable that is not
	 *           defined.
	 * @throws IllegalArgumentException
	 *           if the expression won't tokenize properly.
	 * @throws TimeoutException
	 *           if evaluation takes longer than the timeout.
	 */
	public BigDecimal evaluateOrThrow(String expression) throws ArithmeticException,
			NotDefinedException, IllegalArgumentException, TimeoutException {
		Lexer l = new Lexer(format, expression);
		ExpressionParser p = new ExpressionParser(mathLib, l);

		if (timeout > 0) {
			BackgroundRunner runner = new BackgroundRunner(p);
			Thread t = new Thread(runner);
			t.start();
			try {
				t.join(timeout);
				if (!runner.finished) {
					throw new TimeoutException();
				}
				if (runner.error != null) {
					throw runner.error;
				}
			}
			catch (InterruptedException e) {
				throw new TimeoutException();
			}

		}
		else {
			p.yyparse();
		}
		return p.yyval.nval;
	}

	/**
	 * Evaluate an expression, silently fail on error. This method may be called
	 * repeatedly with different expression.
	 * 
	 * @param expression
	 *          the expression to evaluate
	 * @return The number the expression evaluates to or null on error.
	 */
	public BigDecimal evaluate(String expression) {
		try {
			return evaluateOrThrow(expression);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * How long evaluation may take at most.
	 * 
	 * @param timeout
	 *          maximum execution time in milliseconds. A value of 0 means 'no
	 *          timeout'.
	 */
	public void setTimeout(long timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("timeout cannot be negative");
		this.timeout = timeout;
	}

	/**
	 * Query how long (in milliseconds) the evaluate methods will block at most.
	 * 
	 * @return number of milliseconds or 0 for no timeout.
	 */
	public long getTimeout() {
		return timeout;
	}
}
