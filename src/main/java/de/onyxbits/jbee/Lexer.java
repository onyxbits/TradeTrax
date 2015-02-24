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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * The Tokenizer for the Expression- and Declaration Parser.
 */
final class Lexer {

	/**
	 * Contains the content value of a token (if needed)
	 */
	protected TokenValue value;

	private int idx;
	private int prevIdx;
	protected final char[] inp;
	private char dSep;
	private char gSep;
	private DecimalFormat format;

	/**
	 * Construct a new Lexer with a given locale
	 * 
	 * @param format
	 * @param expr
	 *          the expression to tokenize
	 */
	public Lexer(DecimalFormat format, String expr) {
		this.format = format;
		DecimalFormatSymbols syms = format.getDecimalFormatSymbols();
		this.dSep = syms.getDecimalSeparator();
		this.gSep = syms.getGroupingSeparator();
		this.inp = expr.toCharArray();
		format.setParseBigDecimal(true);
	}

	/**
	 * Query the current cursor position
	 * 
	 * @return index into the input string
	 */
	protected int getPosition() {
		return idx;
	}

	/**
	 * Call after nextToken() to get the exact character sequence that was
	 * matched.
	 * 
	 * @return raw character sequence (not trimmed).
	 */
	protected String lastMatch() {
		return new String(inp, prevIdx, idx - prevIdx);
	}

	/**
	 * Read the next token from the input, advance the cursor.
	 * 
	 * @return token type
	 */
	protected int nextExpressionToken() throws ParseException {
		// NOTE: Since separator characters are not required and whitespace is
		// ignored, every token must start with a unique character(sequence). Input
		// is then matched till the next unique character(sequence), an unknown
		// character or EOL is encountered.

		prevIdx = idx;

		while (idx < inp.length && (inp[idx] == ' ' || inp[idx] == '\t')) {
			// Ignore spaces
			idx++;
		}

		if (idx >= inp.length) {
			// We are done
			return 0;
		}

		// Hex number?
		if (idx < inp.length - 1 && inp[idx] == '\\' && inp[idx + 1] == 'x') {
			hex();
			return ExpressionParserTokens.NUM;
		}

		// Binary number?
		if (idx < inp.length - 1 && inp[idx] == '\\' && inp[idx + 1] == 'b') {
			bin();
			return ExpressionParserTokens.NUM;
		}

		// Decimal number?
		if (idx < inp.length && inp[idx] >= '0' && inp[idx] <= '9') {
			dec();
			return ExpressionParserTokens.NUM;
		}

		// Anything starting with a letter or an underscore is an identifier
		if ((idx < inp.length && inp[idx] >= 'a' && inp[idx] <= 'z')
				|| (idx < inp.length && inp[idx] >= 'Z' && inp[idx] <= 'Z') || inp[idx] == '_') {
			ident();
			return ExpressionParserTokens.IDENT;
		}

		// From here on it's either an operator or something that can't be matched.
		switch (inp[idx]) {
			case '+': {
				if (idx < inp.length - 1 && inp[idx + 1] == '%') {
					idx += 2;
					return ExpressionParserTokens.PLUSPERCENT;
				}
				else {
					idx++;
					return '+';
				}
			}
			case '-': {
				if (idx < inp.length - 1 && inp[idx + 1] == '%') {
					idx += 2;
					return ExpressionParserTokens.MINUSPERCENT;
				}
				else {
					idx++;
					return '-';
				}
			}
			case '*': {
				idx++;
				return '*';
			}
			case '/': {
				idx++;
				return '/';
			}
			case '(': {
				idx++;
				return '(';
			}
			case ';': {
				idx++;
				return ExpressionParserTokens.LSTSEP;
			}
			case ')': {
				idx++;
				return ')';
			}
			case ':': {
				idx++;
				return ':';
			}
			case '%': {
				idx++;
				return '%';
			}
			case '~': {
				idx++;
				return '~';
			}
			case '&': {
				idx++;
				return '&';
			}
			case '|': {
				idx++;
				return '|';
			}
			case '^': {
				idx++;
				return '^';
			}
			case '>': {
				if (idx < inp.length - 1 && inp[idx + 1] == '>') {
					idx += 2;
					return ExpressionParserTokens.BSHIFTR;
				}
			}
			case '<': {
				if (idx < inp.length - 1 && inp[idx + 1] == '<') {
					idx += 2;
					return ExpressionParserTokens.BSHIFTL;
				}
			}
			default: {
				throw new ParseException("" + inp[idx], idx + 1);
			}
		}
	}

	/**
	 * Read the next token from the input, advance the cursor.
	 * 
	 * @return token type
	 */
	protected int nextDeclarationToken() throws ParseException {
		prevIdx = idx;

		while (idx < inp.length && (inp[idx] == ' ' || inp[idx] == '\t')) {
			// Ignore white space
			idx++;
		}

		while (idx < inp.length - 1 && inp[idx] == '/' && inp[idx + 1] == '/') {
			// Skip over comments
			idx += 2;
			while (idx < inp.length && inp[idx] != '\n') {
				idx++;
			}
			idx++;
		}

		if (idx >= inp.length) {
			// We are done
			return 0;
		}

		// Hex number?
		if (idx < inp.length - 1 && inp[idx] == '\\' && inp[idx + 1] == 'x') {
			hex();
			return ExpressionParserTokens.NUM;
		}

		// Binary number?
		if (idx < inp.length - 1 && inp[idx] == '\\' && inp[idx + 1] == 'b') {
			bin();
			return ExpressionParserTokens.NUM;
		}

		// Decimal number?
		if (idx < inp.length && inp[idx] >= '0' && inp[idx] <= '9') {
			dec();
			return ExpressionParserTokens.NUM;
		}

		// Anything starting with a letter or an underscore is an identifier
		if ((idx < inp.length && inp[idx] >= 'a' && inp[idx] <= 'z')
				|| (idx < inp.length && inp[idx] >= 'Z' && inp[idx] <= 'Z') || inp[idx] == '_') {
			ident();
			return DeclarationParserTokens.IDENT;
		}

		// From here on it's either an operator or something that can't be matched.
		switch (inp[idx]) {
			case ';': {
				idx++;
				return ';';
			}
			case '=': {
				idx++;
				return '=';
			}
			case '\n': {
				idx++;
				return '\n';
			}
			default: {
				throw new ParseException("" + inp[idx], idx + 1);
			}
		}
	}

	private void dec() throws ParseException {
		// NOTE: decimal numbers must start with a number, but we already checked
		// for that in nextToken(), so we don't do it here again.
		int tmp = idx;
		while (tmp < inp.length
				&& ((inp[tmp] >= '0' && inp[tmp] <= '9') || inp[tmp] == gSep || inp[tmp] == dSep)) {
			tmp++;
		}
		ParsePosition pos = new ParsePosition(0);
		String str = new String(inp, idx, tmp - idx);
		BigDecimal res = (BigDecimal) format.parse(str, pos);
		if (pos.getIndex() != tmp - idx) {
			throw new ParseException(str, idx);
		}
		value = new TokenValue(res);
		idx = tmp;
	}

	private void hex() throws ParseException {
		// We already did the "0x" check, so skip over
		int tmp = idx + 2;
		while (tmp < inp.length
				&& ((tmp < inp.length && inp[tmp] >= 'a' && inp[tmp] <= 'f')
						|| (tmp < inp.length && inp[tmp] >= 'A' && inp[tmp] <= 'F') || (tmp < inp.length
						&& inp[tmp] >= '0' && inp[tmp] <= '9'))) {
			tmp++;
		}
		String num = new String(inp, idx + 2, tmp - (idx + 2));
		if (num.length() == 0) {
			throw new ParseException("\\x", idx);
		}
		value = new TokenValue(new BigDecimal(new BigInteger(num, 16)));
		idx = tmp;
	}

	private void bin() throws ParseException {
		// We already did the "0b" check, so skip over
		int tmp = idx + 2;
		while (tmp < inp.length && (inp[tmp] == '0' || inp[tmp] == '1')) {
			tmp++;
		}
		String num = new String(inp, idx + 2, tmp - (idx + 2));
		if (num.length() == 0) {
			throw new ParseException("\\b", idx);
		}
		value = new TokenValue(new BigDecimal(new BigInteger(num, 2)));
		idx = tmp;
	}

	private void ident() {
		// NOTE: identifiers must start with a letter or underscore, but we already
		// checked for that in nextToken(), so we don't do it here again.
		int tmp = idx;
		while (tmp < inp.length
				&& ((tmp < inp.length && inp[tmp] >= 'a' && inp[tmp] <= 'z')
						|| (tmp < inp.length && inp[tmp] >= 'A' && inp[tmp] <= 'Z')
						|| (tmp < inp.length && inp[tmp] >= '0' && inp[tmp] <= '9') || inp[tmp] == '_')) {
			tmp++;
		}
		value = new TokenValue(new String(inp, idx, tmp - idx));
		idx = tmp;
	}
}