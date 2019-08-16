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
 * function interface with primitive return value.
 *
 * @author borettim
 * @param <F>
 *            the type of the java standard function interface
 */
public interface PrimitiveReturnExceptionHandlerSupport<F> extends ExceptionHandlerSupport<F, F> {

	/**
	 * Converts this function interface to the corresponding one in java and wrap
	 * exception.
	 *
	 * @return the unchecked operation
	 */
	@Override
	default F uncheck() {
		return uncheckOrIgnore(true);
	}

	/**
	 * Converts this function interface to a lifted one.
	 * <p>
	 * This method is identical to {@link #ignore()}
	 *
	 * @return the lifted function
	 */
	@Override
	default F lift() {
		return ignore();
	}

	/**
	 * Converts this function interface to the corresponding <i>lifted</i> java
	 * standard interface ignoring exception.
	 *
	 * @return the operation that ignore error
	 */
	@Override
	default F ignore() {
		return uncheckOrIgnore(false);
	}

	/**
	 * Uncheck or ignore, depending on the param.
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
