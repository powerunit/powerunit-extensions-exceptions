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

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

/**
 * Represents a function that accepts two arguments and produces an int-valued
 * result. This is the {@code int}-producing primitive specialization for
 * {@link BiFunctionWithException}.
 *
 * @see ToIntBiFunction
 * @param <T>
 *            the type of the first argument to the function
 * @param <U>
 *            the type of the second argument to the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface ToIntBiFunctionWithException<T, U, E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<ToIntBiFunction<T, U>> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @param u
	 *            the second function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see ToIntBiFunction#applyAsInt(Object, Object)
	 */
	int applyAsInt(T t, U u) throws E;

	@Override
	default ToIntBiFunction<T, U> uncheckOrIgnore(boolean uncheck) {
		return (t, u) -> {
			try {
				return applyAsInt(t, u);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return 0;
			}
		};
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, U, E extends Exception> ToIntBiFunctionWithException<T, U, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ToIntBiFunctionWithException} to a {@code ToIntBiFunction}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(ToIntBiFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, E extends Exception> ToIntBiFunction<T, U> unchecked(ToIntBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code ToIntBiFunctionWithException} to a {@code ToIntBiFunction}
	 * that wraps exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(ToIntBiFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <T, U, E extends Exception> ToIntBiFunction<T, U> unchecked(ToIntBiFunctionWithException<T, U, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new ToIntBiFunctionWithException<T, U, E>() {

			@Override
			public int applyAsInt(T t, U u) throws E {
				return function.applyAsInt(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ToIntBiFunctionWithException} to a lifted
	 * {@code ToIntBiFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, E extends Exception> ToIntBiFunction<T, U> lifted(ToIntBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code ToIntBiFunctionWithException} to a lifted
	 * {@code ToIntBiFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, E extends Exception> ToIntBiFunction<T, U> ignored(ToIntBiFunctionWithException<T, U, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
