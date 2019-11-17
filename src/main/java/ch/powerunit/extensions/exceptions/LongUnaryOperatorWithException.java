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

import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * Represents an operation on a single {@code long}-valued operand, may thrown
 * exception and that produces a {@code long}-valued result. This is the
 * primitive type specialization of {@link UnaryOperatorWithException} for
 * {@code long}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #applyAsLong(long) long applyAsLong(long operand) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code LongUnaryOperator}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code LongUnaryOperator}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code LongUnaryOperator}</li>
 * </ul>
 *
 * @see LongUnaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongUnaryOperatorWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongUnaryOperator>, LongDefaultValue {

	/**
	 * Applies this operator to the given operand.
	 *
	 * @param operand
	 *            the operand
	 * @return the operator result
	 *
	 * @throws E
	 *             any exception
	 * @see LongUnaryOperator#applyAsLong(long)
	 */
	long applyAsLong(long operand) throws E;

	@Override
	default LongUnaryOperator uncheckOrIgnore(boolean uncheck) {
		return operand -> {
			try {
				return applyAsLong(operand);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Converts this {@code LongUnaryOperatorWithException} to a
	 * {@code LongUnaryOperator} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(LongUnaryOperatorWithException)
	 * @see #unchecked(LongUnaryOperatorWithException, Function)
	 */
	@Override
	default LongUnaryOperator uncheck() {
		return uncheckOrIgnore(true);

	}

	/**
	 * Converts this {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} returning zero in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(LongUnaryOperatorWithException)
	 */
	@Override
	default LongUnaryOperator ignore() {
		return uncheckOrIgnore(false);
	}

	/**
	 * Returns a composed operator that first applies the {@code before} operator to
	 * its input, and then applies this operator to the result. If evaluation of
	 * either operator throws an exception, it is relayed to the caller of the
	 * composed operator.
	 *
	 * @param before
	 *            the operator to apply before this operator is applied
	 * @return a composed operator that first applies the {@code before} operator
	 *         and then applies this operator
	 * @throws NullPointerException
	 *             if before is null
	 *
	 * @see #andThen(LongUnaryOperatorWithException)
	 */
	default LongUnaryOperatorWithException<E> compose(LongUnaryOperatorWithException<? extends E> before) {
		requireNonNull(before);
		return v -> applyAsLong(before.applyAsLong(v));
	}

	/**
	 * Returns a composed operator that first applies this operator to its input,
	 * and then applies the {@code after} operator to the result. If evaluation of
	 * either operator throws an exception, it is relayed to the caller of the
	 * composed operator.
	 *
	 * @param after
	 *            the operator to apply after this operator is applied
	 * @return a composed operator that first applies this operator and then applies
	 *         the {@code after} operator
	 * @throws NullPointerException
	 *             if after is null
	 *
	 * @see #compose(LongUnaryOperatorWithException)
	 */
	default LongUnaryOperatorWithException<E> andThen(LongUnaryOperatorWithException<? extends E> after) {
		requireNonNull(after);
		return t -> after.applyAsLong(applyAsLong(t));
	}

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 *
	 * @param <E>
	 *            the exception that may be thrown
	 */
	static <E extends Exception> LongUnaryOperatorWithException<E> identity() {
		return t -> t;
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
	static <E extends Exception> LongUnaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return operand -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongUnaryOperatorException} to a {@code LongUnaryOperator}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(LongUnaryOperatorWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> LongUnaryOperator unchecked(LongUnaryOperatorWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a
	 * {@code LongUnaryOperator} that wraps exception to {@code RuntimeException} by
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
	 * @see #unchecked(LongUnaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> LongUnaryOperator unchecked(LongUnaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new LongUnaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long operand) throws E {
				return function.applyAsLong(operand);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} using {@code 0} as return value in case of error.
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
	static <E extends Exception> LongUnaryOperator lifted(LongUnaryOperatorWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} returning {@code 0} in case of exception.
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
	static <E extends Exception> LongUnaryOperator ignored(LongUnaryOperatorWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(LongUnaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function is null
	 * @since 3.0.0
	 */
	static <E extends Exception> LongUnaryOperator ignored(LongUnaryOperatorWithException<E> function,
			long defaultValue) {
		verifyFunction(function);
		return new LongUnaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long operand) throws E {
				return function.applyAsLong(operand);
			}

			@Override
			public long defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
