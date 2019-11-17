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

import static ch.powerunit.extensions.exceptions.Constants.verifyExceptionMapper;
import static ch.powerunit.extensions.exceptions.Constants.verifySupplier;

import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code int}-valued results and may throw exception.
 * This is the {@code int}-producing primitive specialization of
 * {@link SupplierWithException}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #getAsInt() int getAsInt() throws E}</b>&nbsp;-&nbsp;The
 * functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code IntSupplier}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code IntSupplier}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code IntSupplier}</li>
 * </ul>
 *
 * <p>
 * There is no requirement that a distinct result be returned each time the
 * supplier is invoked.
 *
 * @see IntSupplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface IntSupplierWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<IntSupplier>, IntDefaultValue {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a result
	 * @see IntSupplier#getAsInt()
	 */
	int getAsInt() throws E;

	@Override
	default IntSupplier uncheckOrIgnore(boolean uncheck) {
		return () -> {
			try {
				return getAsInt();
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Returns a supplier that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <E extends Exception> IntSupplierWithException<E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a {@code IntSupplier} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(IntSupplierWithException, Function)
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> IntSupplier unchecked(IntSupplierWithException<E> supplier) {
		return verifySupplier(supplier).uncheck();
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a {@code IntSupplier} that
	 * wraps exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param supplier
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(IntSupplierWithException)
	 * @throws NullPointerException
	 *             if supplier or exceptionMapper is null
	 */
	static <E extends Exception> IntSupplier unchecked(IntSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifySupplier(supplier);
		verifyExceptionMapper(exceptionMapper);
		return new IntSupplierWithException<E>() {

			@Override
			public int getAsInt() throws E {
				return supplier.getAsInt();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a lifted {@code IntSupplier}
	 * returning {@code 0} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #lift()
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> IntSupplier lifted(IntSupplierWithException<E> supplier) {
		return verifySupplier(supplier).lift();
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a lifted {@code IntSupplier}
	 * returning {@code 0} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> IntSupplier ignored(IntSupplierWithException<E> supplier) {
		return verifySupplier(supplier).ignore();
	}

}
