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

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Represents a function that produces an int-valued result and may throw
 * exception. This is the {@code int}-producing primitive specialization for
 * {@link FunctionWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #applyAsInt(Object) int applyAsInt(T value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code ToIntFunction<T>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code ToIntFunction<T>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code ToIntFunction<T>}</li>
 * </ul>
 *
 * @see ToIntFunction
 * @param <T>
 *            the type of the input to the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface ToIntFunctionWithException<T, E extends Exception> extends
		PrimitiveReturnExceptionHandlerSupport<ToIntFunction<T>, ToIntFunctionWithException<T, E>>, IntDefaultValue {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see ToIntFunction#applyAsInt(Object)
	 */
	int applyAsInt(T value) throws E;

	@Override
	default ToIntFunction<T> uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return applyAsInt(value);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, E extends Exception> ToIntFunctionWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ToIntFunctionException} to a {@code ToIntFunction} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ToIntFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToIntFunction<T> unchecked(ToIntFunctionWithException<T, E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code ToLongFunctionWithException} to a {@code ToIntFunction}
	 * that wraps exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ToIntFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <T, E extends Exception> ToIntFunction<T> unchecked(ToIntFunctionWithException<T, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new ToIntFunctionWithException<T, E>() {

			@Override
			public int applyAsInt(T value) throws E {
				return function.applyAsInt(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ToLongFunctionWithException} to a lifted
	 * {@code ToIntFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToIntFunction<T> lifted(ToIntFunctionWithException<T, E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code ToLongFunctionWithException} to a lifted
	 * {@code ToIntFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToIntFunction<T> ignored(ToIntFunctionWithException<T, E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code ToLongFunctionWithException} to a lifted
	 * {@code ToIntFunction} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception.
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(ToIntFunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <T, E extends Exception> ToIntFunction<T> ignored(ToIntFunctionWithException<T, E> function,
			int defaultValue) {
		verifyFunction(function);
		return new ToIntFunctionWithException<T, E>() {

			@Override
			public int applyAsInt(T value) throws E {
				return function.applyAsInt(value);
			}

			@Override
			public int defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
