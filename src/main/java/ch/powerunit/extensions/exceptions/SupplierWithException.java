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
 * <h2>General contract</h2>
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
public interface SupplierWithException<T, E extends Exception> extends
		ObjectReturnExceptionHandlerSupport<Supplier<T>, Supplier<Optional<T>>, Supplier<CompletionStage<T>>, T, SupplierWithException<T, E>> {

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
	 * returning {@code null} (or the value redefined by the method
	 * {@link #defaultValue()}) in case of exception.
	 *
	 * @return the supplier that ignore error
	 * @see #ignored(SupplierWithException)
	 */
	@Override
	default Supplier<T> ignore() {
		return () -> lift().get().orElse(defaultValue());
	}

	/**
	 * Convert this {@code SupplierWithException} to a lifted {@code Supplier}
	 * return {@code CompletionStage} as return value.
	 *
	 * @return the lifted supplier
	 * @see #staged(SupplierWithException)
	 */
	@Override
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
		return verifySupplier(supplier).uncheck();
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
		verifySupplier(supplier);
		verifyExceptionMapper(exceptionMapper);
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
		return verifySupplier(supplier).lift();
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
		return verifySupplier(supplier).ignore();
	}

	/**
	 * Converts a {@code SupplierWithException} to a lifted {@code Supplier}
	 * returning a default value in case of exception.
	 *
	 * @param supplier
	 *            to be lifted
	 * @param defaultValue
	 *            the default value in case of exception
	 * @param <T>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted supplier
	 * @see #ignore()
	 * @see #ignored(SupplierWithException)
	 * @throws NullPointerException
	 *             if supplier is null
	 * @since 3.0.0
	 */
	static <T, E extends Exception> Supplier<T> ignored(SupplierWithException<T, E> supplier, T defaultValue) {
		verifySupplier(supplier);
		return new SupplierWithException<T, E>() {

			@Override
			public T get() throws E {
				return supplier.get();
			}

			@Override
			public T defaultValue() {
				return defaultValue;
			}

		}.ignore();
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
		return verifySupplier(supplier).stage();
	}

	/**
	 * Converts a {@code SupplierWithException} to a {@code FunctionWithException}.
	 *
	 * @param supplier
	 *            the supplier to be converted
	 * @param <T>
	 *            the input type of the function
	 * @param <R>
	 *            the result type of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @throws NullPointerException
	 *             if consumer is null
	 * @since 1.2.0
	 */
	static <T, R, E extends Exception> FunctionWithException<T, R, E> asFunction(SupplierWithException<R, E> supplier) {
		return verifySupplier(supplier).asFunction();
	}

	/**
	 * Converts a {@code SupplierWithException} to a {@code FunctionWithException} .
	 *
	 * @param <T1>
	 *            the input parameter of the function
	 * @return the function
	 * @since 1.2.0
	 */
	default <T1> FunctionWithException<T1, T, E> asFunction() {
		return t -> get();
	}

}
