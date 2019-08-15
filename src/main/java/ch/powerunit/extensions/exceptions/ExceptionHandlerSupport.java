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
 *            the type of the java standard functional interface. For example,
 *            {@code Function<T,R>}.
 * @param <L>
 *            the type of the lifted functional interface. For example,
 *            {@code Function<T,Optional<R>>}.
 */
public interface ExceptionHandlerSupport<F, L> {

	/**
	 * Mapping operation to convert the exception that may be thrown during
	 * execution to {@code RuntimeException}.
	 *
	 * @return the mapping function, which is by default constructing
	 *         {@code WrappedException}.
	 * @see WrappedException
	 */
	default Function<Exception, RuntimeException> exceptionMapper() {
		return WrappedException::new;
	}

	/**
	 * Converts this functional interface to the corresponding one in java and wrap
	 * exception using {@link #exceptionMapper()}.
	 *
	 * @return the unchecked operation
	 */
	F uncheck();

	/**
	 * Converts this functional interface to a lifted one. A lifted version may or
	 * may not have the same return type of the original one. When possible a
	 * version returning an {@code Optional} is provided. For functional interface
	 * without return value, this method will be identical to {@link #ignore()}
	 *
	 * @return the lifted function
	 */
	L lift();

	/**
	 * Converts this functional interface to the corresponding java standard
	 * functional interface returning a default value in case of error. For function
	 * interface without return value, error will be silently ignored.
	 *
	 * @return the operation that ignore error
	 */
	F ignore();
}
