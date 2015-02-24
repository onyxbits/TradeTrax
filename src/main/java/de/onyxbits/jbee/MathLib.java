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
import java.util.List;

/**
 * Declares hooks into the parser that are called in various stages of parsing.
 * The general contract is: the parser knows "when" to do something while the
 * mathlib knows "how" to do it.
 * <p>
 * The most common reason for implementing this interface is adding a custom
 * catalog of mathematical functions. In that case, however, it is best to
 * subclass {@link ReflectedMathLib} instead, as this interface is a) complex
 * and b) likely to get new methods when new features are added to the parser.
 * <p>
 * Implementors are strongly discouraged from keeping any global state that can
 * be modified via expressions (in particular, mapping/clearing names from
 * within custom functions). MathLib is designed for being reusable and
 * expressions should evaluate independant of each other.
 */
public interface MathLib {

	/**
	 * Get the {@link MathContext} in use by the {@link MathLib}.
	 * 
	 * @return the {@link MathContext} or null if none is used.
	 */
	public MathContext getMathContext();

	/**
	 * Bind a number to a symbolic name, so it can be referenced in an expression.
	 * NOTE: this method should never be called from within an implementation of
	 * this interface.
	 * 
	 * @param name
	 *          the symbolic name by which the value may be referenced in an
	 *          expression.
	 * @param value
	 *          number to associate with the symbol or null to clear it.
	 */
	public void map(String name, BigDecimal value);

	/**
	 * Clear all mapped constants. NOTE: this method should never be called from
	 * within an implementation of this interface.
	 */
	public void clearMappings();

	/**
	 * Called on evaluating a function.
	 * 
	 * @param name
	 *          identifier of the requested function. It is legal to "generate"
	 *          functions on the fly, based on the name.
	 * @param args
	 *          submitted parameters (never null, but potentially empty).
	 * @return the number, the requested function evaluates to. May not be null.
	 * 
	 * @throws NotDefinedException
	 *           must be thrown if no matching function exists.
	 * @throws ArithmeticException
	 *           should be thrown when a function cannot be evaluated.
	 */
	public BigDecimal onCall(String name, List<BigDecimal> args) throws NotDefinedException,
			ArithmeticException;

	/**
	 * Called when the input string won't tokenize properly. Tokenize errors are
	 * always fatal and cannot be recovered from. This method must always throw an
	 * exception.
	 * 
	 * @param input
	 *          the character sequence that failed to tokenize
	 * @param offset
	 *          position of the error.
	 */
	public void onTokenizeError(final char[] input, int offset);

	/**
	 * Called on Addition
	 * 
	 * @param augend
	 *          lhs number
	 * @param addend
	 *          rhs number
	 * @return the sum of augend and addend
	 */
	public BigDecimal onAddition(BigDecimal augend, BigDecimal addend);

	/**
	 * Called on Addition
	 * 
	 * @param minuend
	 *          lhs number
	 * @param subtrahend
	 *          rhs number
	 * @return the difference.
	 */
	public BigDecimal onSubtraction(BigDecimal minuend, BigDecimal subtrahend);

	/**
	 * Called on multiplication
	 * 
	 * @param multiplicant
	 *          lhs number
	 * @param multiplier
	 *          rhs number
	 * @return product
	 */
	public BigDecimal onMultiplication(BigDecimal multiplicant, BigDecimal multiplier);

	/**
	 * Called on division
	 * 
	 * @param dividend
	 *          lhs number
	 * @param divisor
	 *          rhs number
	 * @return quotient
	 */
	public BigDecimal onDivision(BigDecimal dividend, BigDecimal divisor);

	/**
	 * Called on negation
	 * 
	 * @param num
	 *          number to negate
	 * @return number * -1
	 */
	public BigDecimal onNegation(BigDecimal num);

	/**
	 * Called on modulation
	 * 
	 * @param dividend
	 *          lhs number
	 * @param moddivisor
	 *          rhs number
	 * @return remainder
	 */
	public BigDecimal onModulation(BigDecimal dividend, BigDecimal moddivisor);

	/**
	 * Called on adding a percentage
	 * 
	 * @param base
	 *          base number
	 * @param percent
	 *          percentage
	 * @return sum/product
	 */
	public BigDecimal onPercentAddition(BigDecimal base, BigDecimal percent);

	/**
	 * Called on subtracting a percentage
	 * 
	 * @param base
	 *          base number
	 * @param percent
	 *          percentage
	 * @return difference/product
	 */
	public BigDecimal onPercentSubtraction(BigDecimal base, BigDecimal percent);

	/**
	 * Called on moving the decimal mark
	 * 
	 * @param num
	 *          base number
	 * @param dir
	 *          direction (expressed by the sign) and amount to move.
	 * @return scaled number
	 */
	public BigDecimal onMovePoint(BigDecimal num, BigDecimal dir);

	/**
	 * Called on exponentiation
	 * 
	 * @param base
	 *          the base
	 * @param exponent
	 *          the exponent
	 * @return the power
	 */
	public BigDecimal onExponentiation(BigDecimal base, BigDecimal exponent);

	/**
	 * Called when the expression to evaluate is empty.
	 * 
	 * @return the number, the empty expression should evaluate to. NOTE: Instead
	 *         of returning a number, it is usually more meaningful to throw an
	 *         exception.
	 */
	public BigDecimal onEmptyExpression();

	/**
	 * Called on the first syntax error. This method should not return, but throw
	 * an exception instead.
	 * 
	 * @param pos
	 *          start position of the troublesome token.
	 * @param token
	 *          the string representation of the troublesome token.
	 */
	public void onSyntaxError(int pos, String token);

	/**
	 * Called when a symbol needs to be resolved.
	 * 
	 * @param name
	 *          name of the symbol
	 * @return the value of the requested symbol or null if it is not defined.
	 * @throws NotDefinedException
	 *           if the named symbol does not exist.
	 */
	public BigDecimal onLookup(String name) throws NotDefinedException;

	/**
	 * Called on bitwise not
	 * 
	 * @param num
	 *          number to negate
	 * @return negated number
	 * @throws ArithmeticException
	 *           if num cannot be converted to an integer
	 */
	public BigDecimal onBitwiseNot(BigDecimal num) throws ArithmeticException;

	/**
	 * Called on bitwise AND
	 * 
	 * @param lhs
	 *          left hand side
	 * @param rhs
	 *          right hand side
	 * @return result
	 * @throws ArithmeticException
	 *           if either parameter cannot be converted to an integer
	 */
	public BigDecimal onBitwiseAnd(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException;

	/**
	 * Called on bitwise OR
	 * 
	 * @param lhs
	 *          left hand side
	 * @param rhs
	 *          right hand side
	 * @return result
	 * @throws ArithmeticException
	 *           if either parameter cannot be converted to an integer
	 */
	public BigDecimal onBitwiseOr(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException;

	/**
	 * Called on bitwise XOR
	 * 
	 * @param lhs
	 *          left hand side
	 * @param rhs
	 *          right hand side
	 * @return result
	 * @throws ArithmeticException
	 *           if either parameter cannot be converted to an integer
	 */
	public BigDecimal onBitwiseXor(BigDecimal lhs, BigDecimal rhs) throws ArithmeticException;

	/**
	 * Called on bitshift left
	 * 
	 * @param num
	 *          number to operate on
	 * @param amount
	 *          shift amount
	 * @return result
	 * @throws ArithmeticException
	 *           if either parameter cannot be converted to an integer
	 */
	public BigDecimal onBitshiftLeft(BigDecimal num, BigDecimal amount) throws ArithmeticException;

	/**
	 * Called on bitshift right
	 * 
	 * @param num
	 *          number to operate on
	 * @param amount
	 *          shift amount
	 * @return result
	 * @throws ArithmeticException
	 *           if either parameter cannot be converted to an integer
	 */
	public BigDecimal onBitshiftRight(BigDecimal num, BigDecimal amount) throws ArithmeticException;
}