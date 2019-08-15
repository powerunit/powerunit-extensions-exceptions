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
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * Represents a long unary operator with exception.
 *
 * @author borettim
 * @see LongUnaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongUnaryOperatorWithException<E extends Exception>
		extends ExceptionHandlerSupport<LongUnaryOperator, LongUnaryOperator> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see LongUnaryOperator#applyAsLong(long)
	 */
	long applyAsLong(long t) throws E;

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
		return t -> {
			try {
				return applyAsLong(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} return a zero by default.
	 *
	 * @return the lifted function
	 * @see #lifted(LongUnaryOperatorWithException)
	 */
	@Override
	default LongUnaryOperator lift() {
		return ignore();
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
		return t -> {
			try {
				return applyAsLong(t);
			} catch (Exception e) {
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
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongUnaryOperatorException} to a {@code LongUnaryOperator}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongUnaryOperatorWithException, Function)
	 */
	static <E extends Exception> LongUnaryOperator unchecked(LongUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a
	 * {@code LongUnaryOperator} that convert exception to {@code RuntimeException}
	 * by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongUnaryOperatorWithException)
	 */
	static <E extends Exception> LongUnaryOperator unchecked(LongUnaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new LongUnaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long t) throws E {
				return function.applyAsLong(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> LongUnaryOperator lifted(LongUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code LongUnaryOperatorWithException} to a lifted
	 * {@code LongUnaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> LongUnaryOperator ignored(LongUnaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
