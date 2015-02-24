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
 * Wrapper around the parser so it can be executed on a background thread.
 * 
 * @author patrick
 * 
 */
final class BackgroundRunner implements Runnable {

	protected ExpressionParser parser;
	protected RuntimeException error;
	protected boolean finished;

	public BackgroundRunner(ExpressionParser parser) {
		this.parser = parser;
	}

	public void run() {
		try {
			parser.yyparse();
			finished = true;
		}
		catch (RuntimeException e) {
			error = e;
			finished=true;
		}
	}
}
