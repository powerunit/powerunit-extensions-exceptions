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
import java.util.function.LongBinaryOperator;
import java.util.function.Supplier;

/**
 * Represents an operation upon two {@code long}-valued operands and producing a
 * {@code long}-valued result which may throw exception. This is the primitive
 * type specialization of {@link BinaryOperatorWithException} for {@code long}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #applyAsLong(long, long) long applyAsLong(long left, long
 * right) throws E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code LongBinaryOperator}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code LongBinaryOperator}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code LongBinaryOperator}</li>
 * </ul>
 *
 * @see LongBinaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongBinaryOperatorWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongBinaryOperator, LongBinaryOperatorWithException<E>>,
		LongDefaultValue {

	/**
	 * Applies this operator to the given operands.
	 *
	 * @param left
	 *            the first operand
	 * @param right
	 *            the second operand
	 * @return the operator result
	 * @throws E
	 *             any exception
	 * @see LongBinaryOperator#applyAsLong(long, long)
	 */
	long applyAsLong(long left, long right) throws E;

	@Override
	default LongBinaryOperator uncheckOrIgnore(boolean uncheck) {
		return (left, right) -> {
			try {
				return applyAsLong(left, right);
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
	static <E extends Exception> LongBinaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a
	 * {@code LongBinaryOperator} that convert exception to
	 * {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(LongBinaryOperatorWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> LongBinaryOperator unchecked(LongBinaryOperatorWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a
	 * {@code LongBinaryOperator} that convert exception to {@code RuntimeException}
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
	 * @see #unchecked(LongBinaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> LongBinaryOperator unchecked(LongBinaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new LongBinaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long left, long right) throws E {
				return function.applyAsLong(left, right);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} with {@code 0} as return value in case of
	 * exception.
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
	static <E extends Exception> LongBinaryOperator lifted(LongBinaryOperatorWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} with {@code 0} as return value in case of
	 * exception.
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
	static <E extends Exception> LongBinaryOperator ignored(LongBinaryOperatorWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} with a default value as return value in case of
	 * exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(LongBinaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <E extends Exception> LongBinaryOperator ignored(LongBinaryOperatorWithException<E> function,
			long defaultValue) {
		verifyFunction(function);
		return new LongBinaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long left, long right) throws E {
				return function.applyAsLong(left, right);
			}

			@Override
			public long defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
