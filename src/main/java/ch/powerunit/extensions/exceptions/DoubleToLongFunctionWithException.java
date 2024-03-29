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

import java.util.function.DoubleToLongFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts a double-valued argument, may throw
 * exception and produces a long-valued result. This is the
 * {@code double}-to-{@code long} primitive specialization for
 * {@link FunctionWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #applyAsLong(double) long applyAsLong(double value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code DoubleToLongFunction}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code DoubleToLongFunction}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code DoubleToLongFunction}</li>
 * </ul>
 *
 * @see DoubleToLongFunction
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface DoubleToLongFunctionWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<DoubleToLongFunction, DoubleToLongFunctionWithException<E>>,
		LongDefaultValue {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see DoubleToLongFunction#applyAsLong(double)
	 */
	long applyAsLong(double value) throws E;

	@Override
	default DoubleToLongFunction uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return applyAsLong(value);
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
	static <E extends Exception> DoubleToLongFunctionWithException<E> failing(Supplier<E> exceptionBuilder) {
		return value -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleToLongFunctionWithException} to a
	 * {@code DoubleToLongFunction} that convert exception to
	 * {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(DoubleToLongFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> DoubleToLongFunction unchecked(DoubleToLongFunctionWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code DoubleToLongFunctionWithException} to a
	 * {@code DoubleToLongFunction} that convert exception to
	 * {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(DoubleToLongFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> DoubleToLongFunction unchecked(DoubleToLongFunctionWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new DoubleToLongFunctionWithException<E>() {

			@Override
			public long applyAsLong(double value) throws E {
				return function.applyAsLong(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleToLongFunctionWithException} to a lifted
	 * {@code DoubleToLongFunction} returning {@code 0} in case of exception.
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
	static <E extends Exception> DoubleToLongFunction lifted(DoubleToLongFunctionWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code DoubleToLongFunctionWithException} to a lifted
	 * {@code DoubleToLongFunction} returning {@code 0} in case of exception.
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
	static <E extends Exception> DoubleToLongFunction ignored(DoubleToLongFunctionWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code DoubleToLongFunctionWithException} to a lifted
	 * {@code DoubleToLongFunction} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(DoubleToLongFunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <E extends Exception> DoubleToLongFunction ignored(DoubleToLongFunctionWithException<E> function,
			long defaultValue) {
		verifyFunction(function);
		return new DoubleToLongFunctionWithException<E>() {

			@Override
			public long applyAsLong(double value) throws E {
				return function.applyAsLong(value);
			}

			@Override
			public long defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
