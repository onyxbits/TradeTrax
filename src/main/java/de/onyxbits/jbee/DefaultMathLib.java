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
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;

/**
 * Default implementation of the {@link MathLib}. In most cases it is best to
 * subclass this class and call super() on overidden methods instead of
 * implementing {@link MathLib} from scratch.
 */
public class DefaultMathLib implements MathLib {

	private static final BigDecimal HUNDRET = new BigDecimal(100);

	private MathContext mathContext;
	private HashMap<String, BigDecimal> constants = new HashMap<String, BigDecimal>();

	/**
	 * {@inheritDoc} The default implementation does not provide any functions and
	 * will always throw.
	 */
	public BigDecimal onCall(String name, List<BigDecimal> args) throws NotDefinedException {

		throw new NotDefinedException(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public MathContext getMathContext() {
		return mathContext;
	}

	/**
	 * Set the {@link MathContext} to use from now on.
	 * 
	 * @param m
	 *          new {@link MathContext} or null.
	 */
	public void setMathContext(MathContext m) {
		this.mathContext = m;
	}

	/**
	 * {@inheritDoc}
	 */
	public void map(String name, BigDecimal value) {
		if (value == null) {
			constants.remove(name);
		}
		else {
			constants.put(name, value);
		}
	}

	public void clearMappings() {
		constants.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onLookup(String name) throws NotDefinedException {
		return constants.get(name); // Null check is in the parser, don't double it.
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The default implementation throws an {@link IllegalArgumentException}
	 */
	public BigDecimal onEmptyExpression() {
		throw new IllegalArgumentException("empty expression");
	}

	/**
	 * {@inheritDoc} The default implementation throws an
	 * {@link ArithmeticException}
	 */
	public void onSyntaxError(int pos, String token) {
		throw new ArithmeticException("Syntax error: \'" + token + "\' at position " + pos);
	}

	/**
	 * {@inheritDoc} The default implementation throws an
	 * {@link IllegalArgumentException}
	 */
	public void onTokenizeError(final char[] input, int offset) {
		throw new IllegalArgumentException("Illegal character at position " + offset + " in '"
				+ new String(input) + "'");
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onAddition(BigDecimal augend, BigDecimal addend) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return augend.add(addend);
		}
		else {
			return augend.add(addend, mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onSubtraction(BigDecimal minuend, BigDecimal subtrahend) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return minuend.subtract(subtrahend);
		}
		else {
			return minuend.subtract(subtrahend, mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onMultiplication(BigDecimal multiplicant, BigDecimal multiplier) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return multiplicant.multiply(multiplier);
		}
		else {
			return multiplicant.multiply(multiplier, mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onDivision(BigDecimal dividend, BigDecimal divisor) {
		MathContext mc = getMathContext();
		if (mc == null) {
			// As far as design goes, this is incorrect, but rational numbers such as
			// 1/3 won't terminate after a finite number of digits and the user
			// probably doesn't want an ArithmeticException in that case. If the
			// Exception is truly desired, then this method must be overriden in a
			// subclass.
			return dividend.divide(divisor, MathContext.DECIMAL64);
		}
		else {
			return dividend.divide(divisor, mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onNegation(BigDecimal num) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return num.negate();
		}
		else {
			return num.negate(mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onModulation(BigDecimal dividend, BigDecimal moddivisor) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return dividend.remainder(moddivisor);
		}
		else {
			return dividend.remainder(moddivisor, mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onPercentAddition(BigDecimal base, BigDecimal percent) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return base.add(base.multiply(percent).divide(HUNDRET));
		}
		else {
			return base.add(base.multiply(percent, mc).divide(HUNDRET, mc), mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onPercentSubtraction(BigDecimal base, BigDecimal percent) {
		MathContext mc = getMathContext();
		if (mc == null) {
			return base.subtract(base.multiply(percent).divide(HUNDRET));
		}
		else {
			return base.subtract(base.multiply(percent, mc).divide(HUNDRET, mc), mc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onMovePoint(BigDecimal num, BigDecimal dir) {
		return num.movePointRight(dir.intValueExact());
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitwiseNot(BigDecimal num) throws ArithmeticException {
		return new BigDecimal(num.toBigIntegerExact().not());
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitwiseAnd(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException {
		return new BigDecimal(lhs.toBigIntegerExact().and(rhs.toBigIntegerExact()));
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitwiseOr(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException {
		return new BigDecimal(lhs.toBigIntegerExact().or(rhs.toBigIntegerExact()));
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitwiseXor(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException {
		return new BigDecimal(lhs.toBigIntegerExact().xor(rhs.toBigIntegerExact()));
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitshiftLeft(BigDecimal num, BigDecimal amount) throws ArithmeticException {
		return new BigDecimal(num.toBigIntegerExact().shiftLeft(amount.toBigIntegerExact().intValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal onBitshiftRight(BigDecimal num, BigDecimal amount) throws ArithmeticException {
		return new BigDecimal(num.toBigIntegerExact().shiftRight(amount.toBigIntegerExact().intValue()));
	}
}