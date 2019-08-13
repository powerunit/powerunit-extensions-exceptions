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
 * Root interface to support global operations related to exception handling.
 *
 * @author borettim
 * @param <F>
 *            the type of the java standard function interface
 * @param <L>
 *            the type of the lifted function interface
 * @param <E>
 *            the type of the potential exception of the operation
 */
public interface ExceptionHandlerSupport<F, L, E extends Exception> {

	/**
	 * Mapping operation to convert the exception to {@code RuntimeException}.
	 *
	 * @return the mapping function
	 */
	default Function<Exception, RuntimeException> exceptionMapper() {
		return WrappedException::new;
	}

	/**
	 * Converts this function interface to the corresponding one in java and wrap
	 * exception.
	 *
	 * @return the unchecked operation
	 */
	F uncheck();

	/**
	 * Converts this function interface to a lifted one.
	 *
	 * @return the lifted function
	 */
	L lift();

	/**
	 * Converts this function interface to the corresponding <i>lifted</i> java
	 * standard interface ignoring exception.
	 *
	 * @return the operation that ignore error
	 */
	F ignore();
}
