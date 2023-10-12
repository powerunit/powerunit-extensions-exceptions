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
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

/**
 * Represents a function that accepts an int-valued argument, may throw an
 * exception and produces a double-valued result. This is the
 * {@code int}-to-{@code double} primitive specialization for
 * {@link FunctionWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #applyAsDouble(int) double applyAsDouble(int value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code IntToDoubleFunction}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code IntToDoubleFunction}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code IntToDoubleFunction}</li>
 * </ul>
 *
 * @see IntToDoubleFunction
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntToDoubleFunctionWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<IntToDoubleFunction, IntToDoubleFunctionWithException<E>>,
		DoubleDefaultValue {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see IntToDoubleFunction#applyAsDouble(int)
	 */
	double applyAsDouble(int value) throws E;

	@Override
	default IntToDoubleFunction uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return applyAsDouble(value);
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
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <E extends Exception> IntToDoubleFunctionWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntToDoubleFunctionWithException} to a
	 * {@code IntToDoubleFunction} that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntToDoubleFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> IntToDoubleFunction unchecked(IntToDoubleFunctionWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code IntToDoubleFunctionWithException} to a
	 * {@code IntToDoubleFunction} that wraps exception to {@code RuntimeException}
	 * by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(IntToDoubleFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> IntToDoubleFunction unchecked(IntToDoubleFunctionWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new IntToDoubleFunctionWithException<E>() {

			@Override
			public double applyAsDouble(int value) throws E {
				return function.applyAsDouble(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntToDoubleFunctionWithException} to a lifted
	 * {@code IntToDoubleFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> IntToDoubleFunction lifted(IntToDoubleFunctionWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code IntToDoubleFunctionWithException} to a lifted
	 * {@code IntToDoubleFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> IntToDoubleFunction ignored(IntToDoubleFunctionWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code IntToDoubleFunctionWithException} to a lifted
	 * {@code IntToDoubleFunction} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(IntToDoubleFunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <E extends Exception> IntToDoubleFunction ignored(IntToDoubleFunctionWithException<E> function,
			double defaultValue) {
		verifyFunction(function);
		return new IntToDoubleFunctionWithException<E>() {

			@Override
			public double applyAsDouble(int value) throws E {
				return function.applyAsDouble(value);
			}

			@Override
			public double defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
