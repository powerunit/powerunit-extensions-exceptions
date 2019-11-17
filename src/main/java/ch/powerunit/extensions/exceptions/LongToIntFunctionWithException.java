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
import java.util.function.LongToIntFunction;
import java.util.function.Supplier;

/**
 * Represents a function that accepts a long-valued argument, may throw
 * exception and produces an int-valued result. This is the
 * {@code long}-to-{@code int} primitive specialization for
 * {@link FunctionWithException}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #applyAsInt(long) int applyAsInt(long value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code LongToIntFunction}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code LongToIntFunction}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code LongToIntFunction}</li>
 * </ul>
 *
 * @see LongToIntFunction
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongToIntFunctionWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongToIntFunction>, IntDefaultValue {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see LongToIntFunction#applyAsInt(long)
	 */
	int applyAsInt(long value) throws E;

	@Override
	default LongToIntFunction uncheckOrIgnore(boolean uncheck) {
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
	 * Converts this {@code LongToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(LongToIntFunctionWithException)
	 * @see #unchecked(LongToIntFunctionWithException, Function)
	 */
	@Override
	default LongToIntFunction uncheck() {
		return uncheckOrIgnore(true);
	}

	/**
	 * Converts this {@code LongToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code 0} in case of exception.
	 *
	 * @return the ignoring function
	 * @see #ignored(LongToIntFunctionWithException)
	 */
	@Override
	default LongToIntFunction ignore() {
		return uncheckOrIgnore(false);
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
	static <E extends Exception> LongToIntFunctionWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(LongToIntFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> LongToIntFunction unchecked(LongToIntFunctionWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code LongToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that wraps exception to {@code RuntimeException} by
	 * using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(LongToIntFunctionWithException)
	 * @throws NullPointerException
	 *             if function or ExceptionMapper is null
	 */
	static <E extends Exception> LongToIntFunction unchecked(LongToIntFunctionWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new LongToIntFunctionWithException<E>() {

			@Override
			public int applyAsInt(long value) throws E {
				return function.applyAsInt(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code 0} in case of exception.
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
	static <E extends Exception> LongToIntFunction lifted(LongToIntFunctionWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code LongToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code 0} in case of exception.
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
	static <E extends Exception> LongToIntFunction ignored(LongToIntFunctionWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code LongToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(LongToIntFunctionWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <E extends Exception> LongToIntFunction ignored(LongToIntFunctionWithException<E> function,
			int defaultValue) {
		verifyFunction(function);
		return new LongToIntFunctionWithException<E>() {

			@Override
			public int applyAsInt(long value) throws E {
				return function.applyAsInt(value);
			}

			@Override
			public int defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
