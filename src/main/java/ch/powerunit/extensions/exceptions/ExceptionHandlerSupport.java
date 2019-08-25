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
	 * <p>
	 * Conceptually, the exception encapsulation is done in the following way :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		// do the underlying functional interface action and return result if
	 * 		// applicable
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
	F uncheck();

	/**
	 * Converts this functional interface to a lifted one. A lifted version may or
	 * may not have the same return type of the original one. When possible a
	 * version returning an {@code Optional} is provided. For functional interface
	 * without return value, this method will be identical to {@link #ignore()}.
	 * <p>
	 * For functional interface with Object result, the principle is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return Optional.ofNullable(realaction(xxx));
	 * 	} catch (Exception e) {
	 * 		return Optional.empty();
	 * 	}
	 * }
	 * </pre>
	 *
	 * For functional interface with primitive result, the principle is :
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
	 * For functional interface without result, the principle is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		// do nothing
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #ignore()
	 */
	L lift();

	/**
	 * Converts this functional interface to the corresponding java standard
	 * functional interface returning a default value in case of error. For function
	 * interface without return value, error will be silently ignored.
	 *
	 * <p>
	 * For functional interface with Object result, the principle is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		return null;
	 * 	}
	 * }
	 * </pre>
	 *
	 * For functional interface with primitive result, the principle is :
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
	 * For functional interface without result, the principle is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		// do nothing
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the operation that ignore error
	 * @see #uncheck()
	 * @see #lift()
	 */
	F ignore();

	/**
	 * Helper methods to create exception wrapper that check the exception class.
	 * 
	 * @param clazz
	 *            the class of the exception to be wrapped.
	 * @param mapper
	 *            the exception mapper.
	 * @param <E>
	 *            the type of the exception.
	 * @return A new exception mapper, which use the one received as parameter or
	 *         for the other exception just create a {@code WrappedException}.
	 * @since 1.1.0
	 */
	static <E extends Exception> Function<Exception, RuntimeException> exceptionMapperFor(Class<E> clazz,
			Function<E, RuntimeException> mapper) {
		return e -> clazz.isInstance(e) ? mapper.apply(clazz.cast(e)) : new WrappedException(e);
	}
}
 