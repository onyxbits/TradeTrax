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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

/**
 * A {@link ReflectedMathLib} automatically registers its methods as functions.
 * <p>
 * To register as a function, a method only needs to have {@link BigDecimal} as
 * return type and accept zero or more {@link BigDecimal}S as it's parameters.
 * It may not declare a throws clause with checked exceptions (throwing
 * {@link RuntimeException}S is acceptable).
 * 
 * @author patrick
 * 
 */
public abstract class ReflectedMathLib extends DefaultMathLib {

	/**
	 * {@inheritDoc}
	 */
	public final BigDecimal onCall(String name, List<BigDecimal> args) {
		Method[] methods = getClass().getDeclaredMethods();
		int len = args.size();
		for (Method m : methods) {
			if (name.equals(m.getName()) && m.getReturnType().equals(BigDecimal.class)) {
				Class<?>[] params = m.getParameterTypes();
				boolean fits = true;
				if (params.length == len) {
					for (Class<?> cl : params) {
						if (!cl.equals(BigDecimal.class)) {
							fits = false;
							break;
						}
					}
				}
				if (fits) {
					Object[] pass = new BigDecimal[len];
					for (int i = 0; i < pass.length; i++) {
						pass[i] = args.get(i);
					}
					try {
						return (BigDecimal) m.invoke(this, pass);
					}
					catch (IllegalAccessException e) {
						// e.printStackTrace();
					}
					catch (IllegalArgumentException e) {
						// e.printStackTrace();
					}
					catch (InvocationTargetException e) {
						Throwable t = e.getCause();
						if (t instanceof RuntimeException) {
							throw (RuntimeException) t;
						}
						// We should NEVER get here! If we do, the implementator
						// deliberately messed up by adding a throws clause for checked
						// exceptions. In that case we treat the function as not defined
						throw new NotDefinedException(name);
						// e.printStackTrace();
					}
				}
			}
		}
		return super.onCall(name, args);
	}
}
