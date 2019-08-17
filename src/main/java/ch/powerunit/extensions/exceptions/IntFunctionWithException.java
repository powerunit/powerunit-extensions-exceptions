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
import static ch.powerunit.extensions.exceptions.Constants.FUNCTION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Represents a function that accepts an int-valued argument, may throw
 * exception and produces a result. This is the {@code int}-consuming primitive
 * specialization for {@link FunctionWithException}.
 *
 * @see IntFunction
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntFunctionWithException<R, E extends Exception>
		extends ObjectReturnExceptionHandlerSupport<IntFunction<R>, IntFunction<Optional<R>>, R> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see IntFunction#apply(int)
	 */
	R apply(int value) throws E;

	/**
	 * Converts this {@code IntFunctionWithException} to a {@code IntFunction} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(IntFunctionWithException)
	 * @see #unchecked(IntFunctionWithException, Function)
	 */
	@Override
	default IntFunction<R> uncheck() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), throwingHandler());
	}

	/**
	 * Converts this {@code IntFunctionWithException} to a lifted
	 * {@code IntFunction} using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(IntFunctionWithException)
	 */
	@Override
	default IntFunction<Optional<R>> lift() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(apply(t)),
				notThrowingHandler());
	}

	/**
	 * Converts this {@code IntFunctionWithException} to a lifted
	 * {@code IntFunction} returning {@code null} in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(IntFunctionWithException)
	 */
	@Override
	default IntFunction<R> ignore() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), e -> null);
	}

	/**
	 * Convert this {@code IntFunctionWithException} to a lifted {@code IntFunction}
	 * returning {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(IntFunctionWithException)
	 */
	default IntFunction<CompletionStage<R>> stage() {
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
	static <R, E extends Exception> IntFunctionWithException<R, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntFunctionWithException} to a {@code IntFunction} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <R, E extends Exception> IntFunction<R> unchecked(IntFunctionWithException<R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code IntFunctionWithException} to a {@code IntFunction} that
	 * wraps exception to {@code RuntimeException} by using the provided mapping
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
	 * @see #unchecked(IntFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <R, E extends Exception> IntFunction<R> unchecked(IntFunctionWithException<R, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new IntFunctionWithException<R, E>() {

			@Override
			public R apply(int value) throws E {
				return function.apply(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntFunctionWithException} to a lifted {@code IntFunction}
	 * using {@code Optional} as return value.
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
	static <R, E extends Exception> IntFunction<Optional<R>> lifted(IntFunctionWithException<R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code IntFunctionWithException} to a lifted {@code IntFunction}
	 * returning {@code null} in case of exception.
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
	static <R, E extends Exception> IntFunction<R> ignored(IntFunctionWithException<R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

	/**
	 * Convert this {@code IntFunctionWithException} to a lifted {@code IntFunction}
	 * returning {@code CompletionStage} as return value.
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
	static <R, E extends Exception> IntFunction<CompletionStage<R>> staged(IntFunctionWithException<R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).stage();
	}

}
