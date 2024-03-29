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

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code boolean}-valued results which may throw
 * exception. This is the {@code boolean}-producing primitive specialization of
 * {@link SupplierWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #getAsBoolean() boolean getAsBoolean() throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code BooleanSupplier}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code BooleanSupplier}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code BooleanSupplier}</li>
 * </ul>
 *
 * @see BooleanSupplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface BooleanSupplierWithException<E extends Exception> extends
		PrimitiveReturnExceptionHandlerSupport<BooleanSupplier, BooleanSupplierWithException<E>>, BooleanDefaultValue {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a boolean
	 * @see BooleanSupplier#getAsBoolean()
	 */
	boolean getAsBoolean() throws E;

	@Override
	default BooleanSupplier uncheckOrIgnore(boolean uncheck) {
		return () -> {
			try {
				return getAsBoolean();
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
	static <E extends Exception> BooleanSupplierWithException<E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BooleanSupplierWithException} to a {@code BooleanSupplier}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(BooleanSupplierWithException, Function)
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> BooleanSupplier unchecked(BooleanSupplierWithException<E> supplier) {
		return verifySupplier(supplier).uncheck();
	}

	/**
	 * Converts a {@code BooleanSupplierWithException} to a {@code BooleanSupplier}
	 * that wraps exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param supplier
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(BooleanSupplierWithException)
	 * @throws NullPointerException
	 *             if supplier and exceptionMapper is null
	 */
	static <E extends Exception> BooleanSupplier unchecked(BooleanSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifySupplier(supplier);
		verifyExceptionMapper(exceptionMapper);
		return new BooleanSupplierWithException<E>() {

			@Override
			public boolean getAsBoolean() throws E {
				return supplier.getAsBoolean();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BooleanSupplierWithException} to a lifted
	 * {@code BooleanSupplier} returning {@code null} in case of exception.
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
	static <E extends Exception> BooleanSupplier lifted(BooleanSupplierWithException<E> supplier) {
		return verifySupplier(supplier).lift();
	}

	/**
	 * Converts a {@code BooleanSupplierWithException} to a lifted
	 * {@code BooleanSupplier} returning {@code null} in case of exception.
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
	static <E extends Exception> BooleanSupplier ignored(BooleanSupplierWithException<E> supplier) {
		return verifySupplier(supplier).ignore();
	}

	/**
	 * Converts a {@code BooleanSupplierWithException} to a lifted
	 * {@code BooleanSupplier} returning a default value in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 * @see #ignored(BooleanSupplierWithException)
	 * @throws NullPointerException
	 *             if supplier is null
	 * @since 3.0.0
	 */
	static <E extends Exception> BooleanSupplier ignored(BooleanSupplierWithException<E> supplier,
			boolean defaultValue) {
		verifySupplier(supplier);
		return new BooleanSupplierWithException<E>() {

			@Override
			public boolean getAsBoolean() throws E {
				return supplier.getAsBoolean();
			}

			@Override
			public boolean defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
