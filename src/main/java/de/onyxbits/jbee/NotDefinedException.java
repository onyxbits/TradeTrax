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

/**
 * Thrown when an expression tries to access a function or variable that does
 * not exist. Note: in case of functions, this method might get thrown if the
 * function exists by name but has the wrong parameter count.
 * 
 * @author patrick
 * 
 */
public final class NotDefinedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code NotDefinedException} with the current stack trace
	 * and the specified detail message.
	 * 
	 * @param symbolName
	 *          name of the missing function/variable.
	 */
	public NotDefinedException(String symbolName) {
		super(symbolName);
	}
}
