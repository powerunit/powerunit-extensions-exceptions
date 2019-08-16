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

import static ch.powerunit.extensions.exceptions.Constants.SUPPLIER_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code long}-valued results that may throw
 * exception. This is the {@code long}-producing primitive specialization of
 * {@link Supplier}.
 *
 * @author borettim
 * @see Supplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface LongSupplierWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongSupplier> {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a boolean
	 * @see LongSupplier#getAsLong()
	 */
	long getAsLong() throws E;

	@Override
	default LongSupplier uncheckOrIgnore(boolean uncheck) {
		return () -> {
			try {
				return getAsLong();
			} catch (Exception e) {
				if (uncheck) {
					throw exceptionMapper().apply(e);
				}
				return 0;
			}
		};
	}

	/**
	 * Converts this {@code LongSupplierWithException} to a {@code LongSupplier}
	 * that convert exception to {@code RuntimeException}.
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
	 * convert exception to {@code RuntimeException}. o
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongSupplierWithException, Function)
	 */
	static <E extends Exception> LongSupplier unchecked(LongSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a {@code LongSupplier} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param supplier
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongSupplierWithException)
	 */
	static <E extends Exception> LongSupplier unchecked(LongSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
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
	 * returning {@code null} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #lift()
	 */
	static <E extends Exception> LongSupplier lifted(LongSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code LongSupplierWithException} to a lifted {@code LongSupplier}
	 * returning {@code null} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 */
	static <E extends Exception> LongSupplier ignored(LongSupplierWithException<E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).ignore();
	}

}
