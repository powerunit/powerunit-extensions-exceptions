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
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * Represents an operation on a single {@code int}-valued operand that produces
 * an {@code int}-valued result and may throws exception. This is the primitive
 * type specialization of {@link UnaryOperatorWithException} for {@code int}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #applyAsInt(int) int applyAsInt(int operand) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code IntUnaryOperator}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code IntUnaryOperator}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code IntUnaryOperator}</li>
 * </ul>
 *
 * @see IntUnaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntUnaryOperatorWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<IntUnaryOperator>, IntDefaultValue {

	/**
	 * Applies this operator to the given operand.
	 *
	 * @param operand
	 *            the operand
	 * @return the operator result
	 * @throws E
	 *             any exception
	 * @see IntUnaryOperator#applyAsInt(int)
	 */
	int applyAsInt(int operand) throws E;

	@Override
	default IntUnaryOperator uncheckOrIgnore(boolean uncheck) {
		return t -> {
			try {
				return applyAsInt(t);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
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
	 * @see #andThen(IntUnaryOperatorWithException)
	 */
	default IntUnaryOperatorWithException<E> compose(IntUnaryOperatorWithException<? extends E> before) {
		requireNonNull(before);
		return v -> applyAsInt(before.applyAsInt(v));
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
	 * @see #compose(IntUnaryOperatorWithException)
	 */
	default IntUnaryOperatorWithException<E> andThen(IntUnaryOperatorWithException<? extends E> after) {
		requireNonNull(after);
		return t -> after.applyAsInt(applyAsInt(t));
	}

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 *
	 * @param <E>
	 *            the exception that may be thrown
	 */
	static <E extends Exception> IntUnaryOperatorWithException<E> identity() {
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
	static <E extends Exception> IntUnaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntUnaryOperatorException} to a {@code IntUnaryOperator}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(IntUnaryOperatorWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> IntUnaryOperator unchecked(IntUnaryOperatorWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a
	 * {@code IntUnaryOperator} that wraps exception to {@code RuntimeException} by
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
	 * @see #unchecked(IntUnaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> IntUnaryOperator unchecked(IntUnaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new IntUnaryOperatorWithException<E>() {

			@Override
			public int applyAsInt(int operand) throws E {
				return function.applyAsInt(operand);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} using {@code 0} as return value in case of error.
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
	static <E extends Exception> IntUnaryOperator lifted(IntUnaryOperatorWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} returning {@code 0} in case of exception.
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
	static <E extends Exception> IntUnaryOperator ignored(IntUnaryOperatorWithException<E> function) {
		return verifyFunction(function).ignore();
	}

}
