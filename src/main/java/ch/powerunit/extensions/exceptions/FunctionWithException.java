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
import static ch.powerunit.extensions.exceptions.Constants.verifyFunction;
import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts one argument, may throw exception and
 * produces a result.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #apply(Object) R apply(T t) throws E}</b>&nbsp;-&nbsp;The
 * functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code Function<T, R>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code Function<T, Optional<R>>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code Function<T, R>}</li>
 * </ul>
 *
 * @see Function
 * @param <T>
 *            the type of the input to the function
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> extends
		ObjectReturnExceptionHandlerSupport<Function<T, R>, Function<T, Optional<R>>, Function<T, CompletionStage<R>>, R, FunctionWithException<T, R, E>> {

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
	 * @see Function
	 */
	@Override
	default Function<T, R> uncheck() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), throwingHandler());
	}

	/**
	 * Converts this {@code FunctionWithException} to a lifted {@code Function}
	 * using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(FunctionWithException)
	 * @see Function
	 */
	@Override
	default Function<T, Optional<R>> lift() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(apply(t)),
				notThrowingHandler());
	}

	/**
	 * Converts this {@code FunctionWithException} to a lifted {@code Function}
	 * returning {@code null} (or the value redefined by the method
	 * {@link #defaultValue()}) in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(FunctionWithException)
	 * @see Function
	 */
	@Override
	default Function<T, R> ignore() {
		return lift().andThen(o -> o.orElse(defaultValue()));
	}

	/**
	 * Convert this {@code FunctionWithException} to a lifted {@code Function}
	 * return {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(FunctionWithException)
	 * @see CompletionStage
	 */
	@Override
	default Function<T, CompletionStage<R>> stage() {
		return t -> ObjectReturnExceptionHandlerSupport.staged(() -> apply(t));
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
	 * @see Function#compose(Function)
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
	 *             if after is null *
	 * @see #compose(FunctionWithException)
	 * @see Function#andThen(Function)
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
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
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
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(FunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, R, E extends Exception> Function<T, R> unchecked(FunctionWithException<T, R, E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code Function} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(FunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <T, R, E extends Exception> Function<T, R> unchecked(FunctionWithException<T, R, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new FunctionWithException<T, R, E>() {

			@Override
			public R apply(T t) throws E {
				return function.apply(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
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
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, R, E extends Exception> Function<T, Optional<R>> lifted(FunctionWithException<T, R, E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code FunctionWithException} to a lifted {@code Function}
	 * returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, R, E extends Exception> Function<T, R> ignored(FunctionWithException<T, R, E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code FunctionWithException} to a lifted {@code Function}
	 * returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            the value to be returned in case of exception.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(FunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <T, R, E extends Exception> Function<T, R> ignored(FunctionWithException<T, R, E> function, R defaultValue) {
		verifyFunction(function);
		return new FunctionWithException<T, R, E>() {

			@Override
			public R apply(T t) throws E {
				return function.apply(t);
			}

			@Override
			public R defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

	/**
	 * Convert this {@code FunctionWithException} to a lifted {@code Function}
	 * return {@code CompletionStage} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, R, E extends Exception> Function<T, CompletionStage<R>> staged(FunctionWithException<T, R, E> function) {
		return verifyFunction(function).stage();
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code ConsumerWithException}.
	 *
	 * @param function
	 *            to be converter
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the consumer
	 * @throws NullPointerException
	 *             if function is null
	 * @since 1.2.0
	 */
	static <T, R, E extends Exception> ConsumerWithException<T, E> asConsumer(FunctionWithException<T, R, E> function) {
		return verifyFunction(function).asConsumer();
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code ConsumerWithException}.
	 *
	 * @return the consumer
	 * @since 1.2.0
	 */
	default ConsumerWithException<T, E> asConsumer() {
		return this::apply;
	}

	/**
	 * Converts a {@code FunctionWithException} to a
	 * {@code BiFunctionWithException}.
	 *
	 * @param function
	 *            to be converter
	 * @param <T>
	 *            the type of the input to the function
	 * @param <U>
	 *            the type of the second input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @throws NullPointerException
	 *             if function is null
	 * @since 1.2.0
	 */
	static <T, U, R, E extends Exception> BiFunctionWithException<T, U, R, E> asBiFunction(
			FunctionWithException<T, R, E> function) {
		return verifyFunction(function).asBiFunction();
	}

	/**
	 * Converts a {@code FunctionWithException} to a
	 * {@code BiFunctionWithException}.
	 *
	 * @param <U>
	 *            the type of the second input to the function
	 *
	 * @return the function
	 * @since 1.2.0
	 */
	default <U> BiFunctionWithException<T, U, R, E> asBiFunction() {
		return (t, u) -> apply(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code SupplierWithException}.
	 *
	 * @param function
	 *            to be converter
	 * @param t
	 *            The input to the function.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the supplier
	 * @throws NullPointerException
	 *             if function is null
	 * @since 1.2.0
	 */
	static <T, R, E extends Exception> SupplierWithException<R, E> asSupplier(FunctionWithException<T, R, E> function,
			T t) {
		return verifyFunction(function).asSupplier(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code SupplierWithException}.
	 *
	 * @param t
	 *            The input to the function.
	 *
	 * @return the supplier
	 * @since 1.2.0
	 */
	default SupplierWithException<R, E> asSupplier(T t) {
		return () -> apply(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code Callable}.
	 *
	 * @param function
	 *            to be converter
	 * @param t
	 *            The input to the function.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the callable
	 * @throws NullPointerException
	 *             if function is null
	 * @since 1.2.0
	 */
	static <T, R, E extends Exception> Callable<R> asCallable(FunctionWithException<T, R, E> function, T t) {
		return verifyFunction(function).asCallable(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code Callable}.
	 *
	 * @param t
	 *            The input to the function.
	 *
	 * @return the callable
	 * @since 1.2.0
	 */
	default Callable<R> asCallable(T t) {
		return () -> apply(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code RunnableWithException}.
	 *
	 * @param function
	 *            to be converter
	 * @param t
	 *            The input to the function.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the Runnable
	 * @throws NullPointerException
	 *             if function is null
	 * @since 1.2.0
	 */
	static <T, R, E extends Exception> RunnableWithException<E> asRunnable(FunctionWithException<T, R, E> function,
			T t) {
		return verifyFunction(function).asRunnable(t);
	}

	/**
	 * Converts a {@code FunctionWithException} to a {@code RunnableWithException}.
	 *
	 * @param t
	 *            The input to the function.
	 *
	 * @return the Runnable
	 * @since 1.2.0
	 */
	default RunnableWithException<E> asRunnable(T t) {
		return () -> apply(t);
	}

}
