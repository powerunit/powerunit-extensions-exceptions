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
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code long}-valued results and may throw exception.
 * This is the {@code long}-producing primitive specialization of
 * {@link SupplierWithException}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #getAsLong() long getAsLong() throws E}</b>&nbsp;-&nbsp;The
 * functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code LongSupplier}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code LongSupplier}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code LongSupplier}</li>
 * </ul>
 *
 * <p>
 * There is no requirement that a distinct result be returned each time the
 * supplier is invoked.
 *
 * @see SupplierWithException
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface LongSupplierWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongSupplier>, LongDefaultValue {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a result
	 * @see LongSupplier#getAsLong()
	 */
	long getAsLong() throws E;

	@Override
	default LongSupplier uncheckOrIgnore(boolean uncheck) {
		return () -> {
			try {
				return getAsLong();
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Converts this {@code LongSupplierWithException} to a {@code LongSupplier}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked supplier
	 * @see #unchecked(LongSupplierWithException)
	 * @see #unchecked(LongSupplierWithException, Function)
	 */
	@Override
	default LongSupplier uncheck() {
		return uncheckOrIgnore(true);
	}

	/**
	 * Converts this {@code LongSupplierWithException} to a lifted
	 * {@code LongSupplier} returning {@code 0} in case of exception.
	 *
	 * @return the supplier that ignore error
	 * @see #ignored(LongSupplierWithException)
	 */
	@Override
	default LongSupplier ignore() {
		return uncheckOrIgnore(false);
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
	static <E extends Exception> LongSupplierWithException<E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a {@code LongSupplier} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(LongSupplierWithException, Function)
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> LongSupplier unchecked(LongSupplierWithException<E> supplier) {
		return verifySupplier(supplier).uncheck();
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a {@code LongSupplier} that
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
	 * @see #unchecked(LongSupplierWithException)
	 * @throws NullPointerException
	 *             if supplier or exceptionMapper is null
	 */
	static <E extends Exception> LongSupplier unchecked(LongSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifySupplier(supplier);
		verifyExceptionMapper(exceptionMapper);
		return new LongSupplierWithException<E>() {

			@Override
			public long getAsLong() throws E {
				return supplier.getAsLong();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a lifted {@code LongSupplier}
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
	static <E extends Exception> LongSupplier lifted(LongSupplierWithException<E> supplier) {
		return verifySupplier(supplier).lift();
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a lifted {@code LongSupplier}
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
	static <E extends Exception> LongSupplier ignored(LongSupplierWithException<E> supplier) {
		return verifySupplier(supplier).ignore();
	}

}
