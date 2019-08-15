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
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Represents a supplier of {@code int}-valued results that may throw exception.
 * This is the {@code int}-producing primitive specialization of
 * {@link Supplier}.
 *
 * @author borettim
 * @see Supplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface IntSupplierWithException<E extends Exception>
		extends ExceptionHandlerSupport<IntSupplier, IntSupplier> {

	/**
	 * Gets a result.
	 *
	 * @throws E
	 *             any exception
	 * @return a boolean
	 * @see IntSupplier#getAsInt()
	 */
	int getAsInt() throws E;

	/**
	 * Converts this {@code IntSupplierWithException} to a {@code IntSupplier} that
	 * convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked supplier
	 * @see #unchecked(IntSupplierWithException)
	 * @see #unchecked(IntSupplierWithException, Function)
	 */
	@Override
	default IntSupplier uncheck() {
		return () -> {
			try {
				return getAsInt();
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};
	}

	/**
	 * Converts this {@code IntSupplierWithException} to a lifted
	 * {@code IntSupplier} returning {@code false} in case of exception.
	 *
	 * @return the supplier that ignore error
	 * @see #lifted(IntSupplierWithException)
	 */
	@Override
	default IntSupplier lift() {
		return ignore();
	}

	/**
	 * Converts this {@code IntSupplierWithException} to a lifted
	 * {@code IntSupplier} returning {@code 0} in case of exception.
	 *
	 * @return the supplier that ignore error
	 * @see #ignored(IntSupplierWithException)
	 */
	@Override
	default IntSupplier ignore() {
		return () -> {
			try {
				return getAsInt();
			} catch (Exception e) {
				return 0;
			}
		};
	}

	/**
	 * Transforms this {@code IntSupplierWithException} to a
	 * {@code FunctionWithException}.
	 *
	 * @param <T>
	 *            The type for the input parameter of the function
	 *
	 * @return the function
	 * @see #function(IntSupplierWithException)
	 */
	default <T> FunctionWithException<T, Integer, E> asFunction() {
		return t -> getAsInt();
	}

	/**
	 * Transforms this {@code IntSupplierWithException} to a
	 * {@code SupplierWithException}.
	 *
	 * @return the function
	 * @see #supplier(IntSupplierWithException)
	 */
	default SupplierWithException<Integer, E> asSupplier() {
		return this::getAsInt;
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
	 * convert exception to {@code RuntimeException}. o
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntSupplierWithException, Function)
	 */
	static <E extends Exception> IntSupplier unchecked(IntSupplierWithException<E> supplier) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		return supplier.uncheck();
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a {@code IntSupplier} that
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
	 * @see #unchecked(IntSupplierWithException)
	 */
	static <E extends Exception> IntSupplier unchecked(IntSupplierWithException<E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
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
	 * returning {@code null} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #lift()
	 */
	static <E extends Exception> IntSupplier lifted(IntSupplierWithException<E> supplier) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		return supplier.lift();
	}

	/**
	 * Converts a {@code IntSupplierWithException} to a lifted {@code IntSupplier}
	 * returning {@code null} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 */
	static <E extends Exception> IntSupplier ignored(IntSupplierWithException<E> supplier) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		return supplier.ignore();
	}

	/**
	 * Transforms a {@code IntSupplierWithException} to a
	 * {@code FunctionWithException}.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asFunction()
	 */
	static <T, E extends Exception> FunctionWithException<T, Integer, E> function(
			IntSupplierWithException<E> supplier) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		return supplier.asFunction();
	}

	/**
	 * Transforms a {@code IntSupplierWithException} to a
	 * {@code SupplierWithException}.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asSupplier()
	 */
	static <E extends Exception> SupplierWithException<Integer, E> supplier(IntSupplierWithException<E> supplier) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		return supplier.asSupplier();
	}

}