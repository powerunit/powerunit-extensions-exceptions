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

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts a double-valued argument, may throw
 * exception and produces a result. This is the {@code double}-consuming
 * primitive specialization for {@link FunctionWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #apply(double) R apply(double value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code DoubleFunction<R>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code DoubleFunction<Optional<R>>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code DoubleFunction<R>}</li>
 * </ul>
 *
 * @see DoubleFunction
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface DoubleFunctionWithException<R, E extends Exception> extends
		ObjectReturnExceptionHandlerSupport<DoubleFunction<R>, DoubleFunction<Optional<R>>, DoubleFunction<CompletionStage<R>>, R, DoubleFunctionWithException<R, E>> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see DoubleFunction#apply(double)
	 */
	R apply(double value) throws E;

	/**
	 * Converts this {@code DoubleFunctionWithException} to a {@code DoubleFunction}
	 * that wraps to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(DoubleFunctionWithException)
	 * @see #unchecked(DoubleFunctionWithException, Function)
	 */
	@Override
	default DoubleFunction<R> uncheck() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), throwingHandler());
	}

	/**
	 * Converts this {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(DoubleFunctionWithException)
	 */
	@Override
	default DoubleFunction<Optional<R>> lift() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(apply(t)),
				notThrowingHandler());
	}

	/**
	 * Converts this {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} returning {@code null} (or the value redefined by the
	 * method {@link #defaultValue()}) in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(DoubleFunctionWithException)
	 */
	@Override
	default DoubleFunction<R> ignore() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), e -> defaultValue());
	}

	/**
	 * Convert this {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} returning {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(DoubleFunctionWithException)
	 */
	@Override
	default DoubleFunction<CompletionStage<R>> stage() {
		return t -> ObjectReturnExceptionHandlerSupport.staged(() -> apply(t));
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <R, E extends Exception> DoubleFunctionWithException<R, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleFunctionWithException} to a {@code DoubleFunction}
	 * that wraps to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(DoubleFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <R, E extends Exception> DoubleFunction<R> unchecked(DoubleFunctionWithException<R, E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code DoubleFunctionWithException} to a {@code DoubleFunction}
	 * that wraps to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(DoubleFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <R, E extends Exception> DoubleFunction<R> unchecked(DoubleFunctionWithException<R, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new DoubleFunctionWithException<R, E>() {

			@Override
			public R apply(double value) throws E {
				return function.apply(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <R, E extends Exception> DoubleFunction<Optional<R>> lifted(DoubleFunctionWithException<R, E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <R, E extends Exception> DoubleFunction<R> ignored(DoubleFunctionWithException<R, E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            the default value in case of error.
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(DoubleFunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <R, E extends Exception> DoubleFunction<R> ignored(DoubleFunctionWithException<R, E> function,
			R defaultValue) {
		verifyFunction(function);
		return new DoubleFunctionWithException<R, E>() {

			@Override
			public R apply(double value) throws E {
				return function.apply(value);
			}

			@Override
			public R defaultValue() {
				return defaultValue;
			}
		}.ignore();
	}

	/**
	 * Convert this {@code DoubleFunctionWithException} to a lifted
	 * {@code DoubleFunction} return {@code CompletionStage} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <R, E extends Exception> DoubleFunction<CompletionStage<R>> staged(
			DoubleFunctionWithException<R, E> function) {
		return verifyFunction(function).stage();
	}

}
