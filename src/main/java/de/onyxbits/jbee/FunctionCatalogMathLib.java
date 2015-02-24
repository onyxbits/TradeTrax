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

/**
 * A catalog of common mathematical functions. Note: this class only implements
 * functions that can be computed without precision loss.
 * 
 * @author patrick
 * 
 */
public class FunctionCatalogMathLib extends ReflectedMathLib {

	/**
	 * Calculate the absolute value of a number
	 * 
	 * @param arg
	 *          the number
	 * @return the absolute value.
	 */
	public BigDecimal abs(BigDecimal arg) {
		MathContext mc = getMathContext();
		if (mc != null) {
			return arg.abs(mc);
		}
		else {
			return arg.abs();
		}
	}

	/**
	 * Calculate the signum
	 * 
	 * @param arg
	 *          the number to get the sign from
	 * @return -1, 0 or 1
	 */
	public BigDecimal signum(BigDecimal arg) {
		return new BigDecimal(arg.signum());
	}

	/**
	 * Calculate the power of two numbers
	 * 
	 * @param base
	 *          base
	 * @param exponent
	 *          exponent
	 * @return the power
	 * @throws ArithmeticException
	 *           if the exponent is not an integer or absolute value exceeds
	 *           999999999.
	 */
	public BigDecimal pow(BigDecimal base, BigDecimal exponent) throws ArithmeticException {
		return onExponentiation(base,exponent);
	}
}
