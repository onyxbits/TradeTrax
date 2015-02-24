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
import java.util.List;

/**
 * Transport between {@link Lexer} and {@link ExpressionParser}
 * 
 * @author patrick
 * 
 */
final class TokenValue {

	/**
	 * For identifiers
	 */
	public String sval;

	/**
	 * For numbers
	 */
	public BigDecimal nval;

	/**
	 * For argument lists
	 */
	public List<BigDecimal> lstval;

	public TokenValue(List<BigDecimal> val) {
		this.lstval = val;
	}

	public TokenValue(BigDecimal val) {
		if (val == null) {
			// Safeguard against broken MathLib implementations.
			throw new NullPointerException();
		}
		this.nval = val;
	}

	public TokenValue(String val) {
		this.sval = val;
	}

	public TokenValue() {
	}
}