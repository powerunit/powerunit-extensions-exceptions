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

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a supplier of results that may throw exception
 * 
 * @author borettim
 * @see Supplier
 * @param <T>
 *            the type of results supplied by this supplier
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface SupplierWithException<T, E extends Exception> extends ExceptionHandlerSupport {

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
	 * Converts this {@code SupplierWithException} to a {@code Supplier} that
	 * convert exception to {@code RuntimeException}.
	 * 
	 * @return the unchecked supplier
	 * @see #unchecked(SupplierWithException)
	 * @see #unchecked(SupplierWithException, Function)
	 */
	default Supplier<T> uncheck() {
		return () -> {
			try {
				return get();
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};
	}

	/**
	 * Converts this {@code SupplierWithException} to a lifted {@code Supplier}
	 * using {@code Optional} as return value.
	 * 
	 * @return the lifted supplier
	 * @see #lifted(SupplierWithException)
	 */
	default Supplier<Optional<T>> lift() {
		return () -> {
			try {
				return Optional.ofNullable(get());
			} catch (Exception e) {
				return Optional.empty();
			}
		};
	}

	/**
	 * Converts this {@code SupplierWithException} to a lifted {@code Supplier}
	 * returning {@code null} in case of exception.
	 * 
	 * @return the supplier that ignore error
	 * @see #ignored(SupplierWithException)
	 */
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
		return () -> {
			try {
				return completedFuture(get());
			} catch (Exception e) {
				// failedStage only available since 9
				CompletableFuture<T> result = new CompletableFuture<>();
				result.completeExceptionally(e);
				return result;
			}
		};
	}

	/**
	 * Transforms this {@code SupplierWithException} to a {@code FunctionWithException}.
	 * 
	 * @param <T1> The type of the input for the produced function
	 * 
	 * @return the function
	 * @see #function(SupplierWithException)
	 */
	default <T1> FunctionWithException<T1, T, E> asFunction() {
		return t -> {
			return get();
		};
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
	 * Converts a {@code SupplierWithException} to a {@code Supplier} that convert
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
	 */
	static <T, E extends Exception> Supplier<T> unchecked(SupplierWithException<T, E> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return supplier.uncheck();
	}

	/**
	 * Converts a {@code SupplierWithException} to a {@code Supplier} that convert
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
	 */
	static <T, E extends Exception> Supplier<T> unchecked(SupplierWithException<T, E> supplier,
			Function<Exception, ? extends RuntimeException> exceptionMapper) {
		requireNonNull(supplier, "supplier can't be null");
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new SupplierWithException<T, E>() {

			@Override
			public T get() throws E {
				return supplier.get();
			}

			@Override
			public Function<Exception, ? extends RuntimeException> exceptionMapper() {
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
	 */
	static <T, E extends Exception> Supplier<Optional<T>> lifted(SupplierWithException<T, E> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return supplier.lift();
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
	 */
	static <T, E extends Exception> Supplier<T> ignored(SupplierWithException<T, E> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return supplier.ignore();
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
	 */
	static <T, E extends Exception> Supplier<CompletionStage<T>> staged(SupplierWithException<T, E> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return supplier.stage();
	}

	/**
	 * Transforms a {@code SupplierWithException} to a
	 * {@code FunctionWithException}.
	 * 
	 * @param supplier
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the operation
	 * @param <R>
	 *            the type of the output object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asFunction()
	 */
	static <T, R, E extends Exception> FunctionWithException<T, R, E> function(SupplierWithException<R, E> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return supplier.asFunction();
	}

}
