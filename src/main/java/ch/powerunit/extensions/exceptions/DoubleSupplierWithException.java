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

import static ch.powerunit.extensions.exceptions.Constants.EXCEPTIONMAPPER_CANT_BE_NULL;
import static ch.powerunit.extensions.exceptions.Constants.SUPPLIER_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code double}-valued results and may throw
 * exception. This is the {@code double}-producing primitive specialization of
 * {@link SupplierWithException}.
 * <p>
 * There is no requirement that a distinct result be returned each time the
 * supplier is invoked.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #getAsDouble() double getAsDouble() throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code DoubleSupplier}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code DoubleSupplier}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code DoubleSupplier}</li>
 * </ul>
 *
 * @see DoubleSupplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface DoubleSupplierWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<DoubleSupplier> {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a result
	 * @see DoubleSupplier#getAsDouble()
	 */
	double getAsDouble() throws E;

	@Override
	default DoubleSupplier uncheckOrIgnore(boolean uncheck) {
		return () -> {
			try {
				return getAsDouble();
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return 0;
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
	static <E extends Exception> DoubleSupplierWithException<E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleSupplierWithException} to a {@code DoubleSupplier}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked supplier
	 * @see #uncheck()
	 * @see #unchecked(DoubleSupplierWithException, Function)
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <E extends Exception> DoubleSupplier unchecked(DoubleSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code DoubleSupplierWithException} to a {@code DoubleSupplier}
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
	 * @see #unchecked(DoubleSupplierWithException)
	 * @throws NullPointerException
	 *             if supplier or exceptionMapper is null
	 */
	static <E extends Exception> DoubleSupplier unchecked(DoubleSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new DoubleSupplierWithException<E>() {

			@Override
			public double getAsDouble() throws E {
				return supplier.getAsDouble();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleSupplierWithException} to a lifted
	 * {@code DoubleSupplier} returning {@code 0} in case of exception.
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
	static <E extends Exception> DoubleSupplier lifted(DoubleSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code DoubleSupplierWithException} to a lifted
	 * {@code DoubleSupplier} returning {@code 0} in case of exception.
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
	static <E extends Exception> DoubleSupplier ignored(DoubleSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).ignore();
	}

}
