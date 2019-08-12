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
 * Represents a function that accepts one argument, may throw exception and
 * produces a result.
 * 
 * @author borettim
 * @see Function
 * @param <T>
 *            the type of the input to the function
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> extends ExceptionHandlerSupport {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param t
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see Function#apply(Object)
	 */
	R apply(T t) throws E;

	/**
	 * Converts this {@code FunctionWithException} to a {@code Function} that
	 * convert exception to {@code RuntimeException}.
	 * 
	 * @return the unchecked function
	 * @see #unchecked(FunctionWithException)
	 * @see #unchecked(FunctionWithException, Function)
	 */
	default Function<T, R> uncheck() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};
	}

	/**
	 * Converts this {@code FunctionWithException} to a lifted {@code Function}
	 * using {@code Optional} as return value.
	 * 
	 * @return the lifted function
	 * @see #lifted(FunctionWithException)
	 */
	default Function<T, Optional<R>> lift() {
		return t -> {
			try {
				return Optional.ofNullable(apply(t));
			} catch (Exception e) {
				return Optional.empty();
			}
		};
	}

	/**
	 * Converts this {@code FunctionWithException} to a lifted {@code Function}
	 * returning {@code null} in case of exception.
	 * 
	 * @return the function that ignore error
	 * @see #ignored(FunctionWithException)
	 */
	default Function<T, R> ignore() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception e) {
				return null;
			}
		};
	}

	/**
	 * Convert this {@code FunctionWithException} to a lifted {@code Function}
	 * return {@code CompletionStage} as return value.
	 * 
	 * @return the lifted function
	 * @see #staged(FunctionWithException)
	 */
	default Function<T, CompletionStage<R>> stage() {
		return t -> {
			try {
				return completedFuture(apply(t));
			} catch (Exception e) {
				// failedStage only available since 9
				CompletableFuture<R> result = new CompletableFuture<>();
				result.completeExceptionally(e);
				return result;
			}
		};
	}

	/**
	 * Transforms this {@code FunctionWithException} to a
	 * {@code ConsumerWithException}.
	 * 
	 * @return the operation
	 * @see #asConsumer(FunctionWithException)
	 */
	default ConsumerWithException<T, Exception> asConsumer() {
		return t -> apply(t);
	}

	/**
	 * Transforms this {@code FunctionWithException} to a
	 * {@code SupplierWithException}.
	 * 
	 * @param input
	 *            the input for the generated supplier.
	 * @return the supplier
	 * @see #asSupplier()
	 */
	default SupplierWithException<R, Exception> asSupplier(T input) {
		return () -> apply(input);
	}

	/**
	 * Transforms this {@code FunctionWithException} to a
	 * {@code SupplierWithException}, passing {@code null} as input.
	 * 
	 * @return the supplier
	 * @see #asSupplier(Object)
	 */
	default SupplierWithException<R, Exception> asSupplier() {
		return asSupplier(null);
	}

	/**
	 * Returns a composed function that first applies the {@code before} function to
	 * its input, and then applies this function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>
	 *            the type of input to the {@code before} function, and to the
	 *            composed function
	 * @param before
	 *            the function to apply before this function is applied
	 * @return a composed function that first applies the {@code before} function
	 *         and then applies this function
	 * @throws NullPointerException
	 *             if before is null
	 *
	 * @see #andThen(FunctionWithException)
	 * @see Function#andThen(Function)
	 */
	default <V> FunctionWithException<V, R, E> compose(
			FunctionWithException<? super V, ? extends T, ? extends E> before) {
		requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}

	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>
	 *            the type of output of the {@code after} function, and of the
	 *            composed function
	 * @param after
	 *            the function to apply after this function is applied
	 * @return a composed function that first applies this function and then applies
	 *         the {@code after} function
	 * @throws NullPointerException
	 *             if after is null
	 *
	 * @see #compose(FunctionWithException)
	 * @see Function#compose(Function)
	 */
	default <V> FunctionWithException<T, V, E> andThen(
			FunctionWithException<? super R, ? extends V, ? extends E> after) {
		requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}

	/**
	 * Returns a function that always returns its input argument.
	 *
	 * @param <T>
	 *            the type of the input and output objects to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return a function that always returns its input argument
	 * @see Function#identity()
	 */
	static <T, E extends Exception> FunctionWithException<T, T, E> identity() {
		return t -> t;
	}

	/**
	 * Returns a function that always throw exception.
	 * 
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, R, E extends Exception> FunctionWithException<T, R, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code Function} that convert
	 * exception to {@code RuntimeException}.
	 * 
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(FunctionWithException, Function)
	 */
	static <T, R, E extends Exception> Function<T, R> unchecked(FunctionWithException<T, R, E> function) {
		requireNonNull(function, "function can't be null");
		return function.uncheck();
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code Function} that convert
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 * 
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(FunctionWithException)
	 */
	static <T, R, E extends Exception> Function<T, R> unchecked(FunctionWithException<T, R, E> function,
			Function<Exception, ? extends RuntimeException> exceptionMapper) {
		requireNonNull(function, "function can't be null");
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new FunctionWithException<T, R, E>() {

			@Override
			public R apply(T t) throws E {
				return function.apply(t);
			}

			@Override
			public Function<Exception, ? extends RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code FunctionWithException} to a lifted {@code Function} using
	 * {@code Optional} as return value.
	 * 
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <T, R, E extends Exception> Function<T, Optional<R>> lifted(FunctionWithException<T, R, E> function) {
		requireNonNull(function, "function can't be null");
		return function.lift();
	}

	/**
	 * Converts a {@code FunctionWithException} to a lifted {@code Function}
	 * returning {@code null} in case of exception.
	 * 
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, R, E extends Exception> Function<T, R> ignored(FunctionWithException<T, R, E> function) {
		requireNonNull(function, "function can't be null");
		return function.ignore();
	}

	/**
	 * Convert this {@code FunctionWithException} to a lifted {@code Function}
	 * return {@code CompletionStage} as return value.
	 * 
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 */
	static <T, R, E extends Exception> Function<T, CompletionStage<R>> staged(FunctionWithException<T, R, E> function) {
		requireNonNull(function, "function can't be null");
		return function.stage();
	}

	/**
	 * Transforms this {@code FunctionWithException} to a
	 * {@code ConsumerWithException}.
	 * 
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asConsumer()
	 */
	static <T, R, E extends Exception> ConsumerWithException<T, Exception> asConsumer(
			FunctionWithException<T, R, E> function) {
		requireNonNull(function, "function can't be null");
		return function.asConsumer();
	}

}
