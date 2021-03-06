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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Root interface to support global operations related to exception handling.
 *
 * @param <F>
 *            the type of the java standard functional interface. For example,
 *            {@code Function<T,R>}.
 * @param <L>
 *            the type of the lifted functional interface. For example,
 *            {@code Function<T,Optional<R>>}.
 * @param <Z>
 *            the type of the interface it self.
 */
public interface ExceptionHandlerSupport<F, L, Z extends ExceptionHandlerSupport<F, L, Z>> {

	/**
	 * Mapping operation to convert the exception that may be thrown during
	 * execution to {@code RuntimeException}.
	 * <p>
	 * The default mapper function may be changed by :
	 * <ul>
	 * <li>Using the second argument of the various {@code unchecked} methods. This
	 * override only the current interface.</li>
	 * <li>By defining global {@code ExceptionMapper}, using the module syntax :
	 * {@code provides ch.powerunit.extensions.exceptions.ExceptionMapper with XXX}</li>
	 * </ul>
	 *
	 * @return the mapping function, which is by default constructing
	 *         {@code WrappedException}.
	 * @see WrappedException
	 */
	default Function<Exception, RuntimeException> exceptionMapper() {
		return Constants.MAPPERS;
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
	 * Add a {@code toString} method to the existing interface based on the received
	 * {@code Supplier}.
	 * 
	 * @param toString
	 *            the supplier to be used
	 * @return a new interface, with {@code toString} using the received supplier.
	 * @throws NullPointerException
	 *             if toString is null
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	default Z documented(Supplier<String> toString) {
		return (Z) InternalHelper.documented(this, Objects.requireNonNull(toString, "toString can't be null"));
	}

}
