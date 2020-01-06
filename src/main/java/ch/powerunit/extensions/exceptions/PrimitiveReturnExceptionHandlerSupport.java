/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.exceptions;

import java.util.function.Function;

/**
 * Root interface to support global operations related to exception handling for
 * functional interface with primitive return value.
 *
 * @param <F>
 *            the type of the java standard functional interface. For example,
 *            {@code Predicate<T>}. The same functional interface is also used
 *            for the lifted and ignored version.
 */
public interface PrimitiveReturnExceptionHandlerSupport<F, Z extends PrimitiveReturnExceptionHandlerSupport<F, Z>>
		extends ExceptionHandlerSupport<F, F, Z> {

	/**
	 * Converts this functional interface to the corresponding one in java and wrap
	 * exception using {@link #exceptionMapper()}.
	 * <p>
	 * Conceptually, the exception encapsulation is done in the following way :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		throw new exceptionMapper().apply(e);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the unchecked operation
	 * @see #lift()
	 * @see #ignore()
	 */
	@Override
	default F uncheck() {
		return uncheckOrIgnore(true);
	}

	/**
	 * Converts this functional interface to a lifted one. A lifted version return
	 * the same type as the original one, with a default value.
	 * <p>
	 * The concept is
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		return defaultValue;
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #ignore()
	 */
	@Override
	default F lift() {
		return ignore();
	}

	/**
	 * Converts this functional interface to a lifted one. A lifted version return
	 * the same type as the original one, with a default value.
	 * <p>
	 * The concept is
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		return defaultValue;
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #lift()
	 */
	@Override
	default F ignore() {
		return uncheckOrIgnore(false);
	}

	/**
	 * Used internally to implements the ignore or uncheck operation.
	 *
	 * @param uncheck
	 *            create unchecked version of the function when true, else ignored
	 *            version.
	 * @return the function
	 */
	F uncheckOrIgnore(boolean uncheck);

	/**
	 * Internal function to throw an exception in case of error and uncheck mode.
	 *
	 * @param uncheck
	 *            true if exception must be thrown.
	 * @param e
	 *            the current exception
	 * @param exceptionMapper
	 *            the mapper to create exception
	 */
	static void handleException(boolean uncheck, Exception e, Function<Exception, RuntimeException> exceptionMapper) {
		if (uncheck) {
			throw exceptionMapper.apply(e);
		}
	}

}
