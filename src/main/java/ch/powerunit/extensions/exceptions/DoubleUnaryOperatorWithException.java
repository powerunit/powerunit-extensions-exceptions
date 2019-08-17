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

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation on a single {@code double}-valued operand that may
 * thrown exception and produces a {@code double}-valued result. This is the
 * primitive type specialization of {@link UnaryOperatorWithException} for
 * {@code double}.
 *
 * @see DoubleUnaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface DoubleUnaryOperatorWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<DoubleUnaryOperator> {

	/**
	 * AApplies this operator to the given operand.
	 *
	 * @param operand
	 *            the operand
	 * @return the operator result
	 * @throws E
	 *             any exception
	 * @see DoubleUnaryOperator#applyAsDouble(double)
	 */
	double applyAsDouble(double operand) throws E;

	@Override
	default DoubleUnaryOperator uncheckOrIgnore(boolean uncheck) {
		return operand -> {
			try {
				return applyAsDouble(operand);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return 0;
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
	 * @see #andThen(DoubleUnaryOperatorWithException)
	 */
	default DoubleUnaryOperatorWithException<E> compose(DoubleUnaryOperatorWithException<? extends E> before) {
		requireNonNull(before);
		return v -> applyAsDouble(before.applyAsDouble(v));
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
	 * @see #compose(DoubleUnaryOperatorWithException)
	 */
	default DoubleUnaryOperatorWithException<E> andThen(DoubleUnaryOperatorWithException<? extends E> after) {
		requireNonNull(after);
		return t -> after.applyAsDouble(applyAsDouble(t));
	}

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 *
	 * @param <E>
	 *            the exception that may be thrown
	 */
	static <E extends Exception> DoubleUnaryOperatorWithException<E> identity() {
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
	static <E extends Exception> DoubleUnaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return operand -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleUnaryOperatorException} to a
	 * {@code DoubleUnaryOperator} that wraps exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(DoubleUnaryOperatorWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> DoubleUnaryOperator unchecked(DoubleUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code DoubleUnaryOperatorWithException} to a
	 * {@code DoubleUnaryOperator} that wraps exception to {@code RuntimeException}
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
	 * @see #unchecked(DoubleUnaryOperatorWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> DoubleUnaryOperator unchecked(DoubleUnaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new DoubleUnaryOperatorWithException<E>() {

			@Override
			public double applyAsDouble(double operand) throws E {
				return function.applyAsDouble(operand);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleUnaryOperatorWithException} to a lifted
	 * {@code DoubleUnaryOperator} using {@code 0} as return value in case of error.
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
	static <E extends Exception> DoubleUnaryOperator lifted(DoubleUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code DoubleUnaryOperatorWithException} to a lifted
	 * {@code DoubleUnaryOperator} returning {@code 0} in case of exception.
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
	static <E extends Exception> DoubleUnaryOperator ignored(DoubleUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
