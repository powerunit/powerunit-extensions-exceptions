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

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a supplier of results that may thrown exception.
 *
 * <p>
 * There is no requirement that a new or distinct result be returned each time
 * the supplier is invoked.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #get() T get() throws E}</b>&nbsp;-&nbsp;The functional
 * method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code Supplier<T>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code Supplier<Optional<T>>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code Supplier<T>}</li>
 * </ul>
 *
 * @see Supplier
 * @param <T>
 *            the type of results supplied by this supplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface SupplierWithException<T, E extends Exception>
		extends ObjectReturnExceptionHandlerSupport<Supplier<T>, Supplier<Optional<T>>, T> {

	/**
	 * Gets a result.
	 *
	 * @return the result.
	 * @throws E
	 *             any exception
	 * @see Supplier#get()
	 */
	T get() throws E;

	/**
	 * Converts this {@code SupplierWithException} to a {@code Supplier} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @return the unchecked supplier
	 * @see #unchecked(SupplierWithException)
	 * @see #unchecked(SupplierWithException, Function)
	 */
	@Override
	default Supplier<T> uncheck() {
		return () -> ObjectReturnExceptionHandlerSupport.unchecked(this::get, throwingHandler());
	}

	/**
	 * Converts this {@code SupplierWithException} to a lifted {@code Supplier}
	 * using {@code Optional} as return value.
	 *
	 * @return the lifted supplier
	 * @see #lifted(SupplierWithException)
	 */
	@Override
	default Supplier<Optional<T>> lift() {
		return () -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(get()),
				notThrowingHandler());
	}

	/**
	 * Converts this {@code SupplierWithException} to a lifted {@code Supplier}
	 * returning {@code null} in case of exception.
	 *
	 * @return the supplier that ignore error
	 * @see #ignored(SupplierWithException)
	 */
	@Override
	default Supplier<T> ignore() {
		return () -> lift().get().orElse(null);
	}

	/**
	 * Convert this {@code SupplierWithException} to a lifted {@code Supplier}
	 * return {@code CompletionStage} as return value.
	 *
	 * @return the lifted supplier
	 * @see #staged(SupplierWithException)
	 */
	default Supplier<CompletionStage<T>> stage() {
		return () -> ObjectReturnExceptionHandlerSupport.staged(this::get);
	}

	/**
	 * Returns a supplier that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the return object to the operation
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <T, E extends Exception> SupplierWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code SupplierWithException} to a {@code Supplier} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @param supplier
	 *            to be unchecked
	 * @param <T>
	 *            the type of the output object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(SupplierWithException, Function)
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <T, E extends Exception> Supplier<T> unchecked(SupplierWithException<T, E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code SupplierWithException} to a {@code Supplier} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param supplier
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the output object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(SupplierWithException)
	 * @throws NullPointerException
	 *             if supplier or exceptionMapper is null
	 */
	static <T, E extends Exception> Supplier<T> unchecked(SupplierWithException<T, E> supplier,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(supplier, SUPPLIER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new SupplierWithException<T, E>() {

			@Override
			public T get() throws E {
				return supplier.get();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code SupplierWithException} to a lifted {@code Supplier} using
	 * {@code Optional} as return value.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <T>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <T, E extends Exception> Supplier<Optional<T>> lifted(SupplierWithException<T, E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code SupplierWithException} to a lifted {@code Supplier}
	 * returning {@code null} in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <T>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <T, E extends Exception> Supplier<T> ignored(SupplierWithException<T, E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).ignore();
	}

	/**
	 * Convert this {@code SupplierWithException} to a lifted {@code Supplier}
	 * return {@code CompletionStage} as return value.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param <T>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #stage()
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	static <T, E extends Exception> Supplier<CompletionStage<T>> staged(SupplierWithException<T, E> supplier) {
		return requireNonNull(supplier, SUPPLIER_CANT_BE_NULL).stage();
	}

}
