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

import static ch.powerunit.extensions.exceptions.Constants.FUNCTION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongBiFunction;

/**
 * Represents a predicate (boolean-valued function) of one argument and may
 * throw an exception.
 *
 * @author borettim
 * @see Predicate
 * @param <T>
 *            the type of the first input to the predicate
 * @param <U>
 *            the type of the second argument the predicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface ToLongBiFunctionWithException<T, U, E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<ToLongBiFunction<T, U>> {

	/**
	 * Evaluates this predicate on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the first second argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see ToLongBiFunction#applyAsLong(Object, Object)
	 */
	long applyAsLong(T t, U u) throws E;

	@Override
	default ToLongBiFunction<T, U> uncheckOrIgnore(boolean uncheck) {
		return (t, u) -> {
			try {
				return applyAsLong(t, u);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return 0;
			}
		};
	}

	/**
	 * Returns a predicate that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <T, U, E extends Exception> ToLongBiFunctionWithException<T, U, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a {@code ToLongBiFunction}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ToLongBiFunctionWithException, Function)
	 */
	static <T, U, E extends Exception> ToLongBiFunction<T, U> unchecked(
			ToLongBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a {@code ToLongBiFunction}
	 * that convert exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ToLongBiFunctionWithException)
	 */
	static <T, U, E extends Exception> ToLongBiFunction<T, U> unchecked(ToLongBiFunctionWithException<T, U, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new ToLongBiFunctionWithException<T, U, E>() {

			@Override
			public long applyAsLong(T t, U u) throws E {
				return function.applyAsLong(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a lifted
	 * {@code ToLongBiFunction} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <T, U, E extends Exception> ToLongBiFunction<T, U> lifted(ToLongBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a lifted
	 * {@code ToLongBiFunction} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, U, E extends Exception> ToLongBiFunction<T, U> ignored(ToLongBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
