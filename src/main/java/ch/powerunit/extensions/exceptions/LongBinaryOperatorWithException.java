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
import java.util.function.LongBinaryOperator;
import java.util.function.Supplier;

/**
 * Represents a long binary operator with exception.
 *
 * @author borettim
 * @see LongBinaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongBinaryOperatorWithException<E extends Exception>
		extends ExceptionHandlerSupport<LongBinaryOperator, LongBinaryOperator> {

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
	 * @see LongBinaryOperator#applyAsLong(long, long)
	 */
	long applyAsLong(long t, long u) throws E;

	/**
	 * Converts this {@code LongBinaryOperatorWithException} to a
	 * {@code LongBinaryOperator} that convert exception to
	 * {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(LongBinaryOperatorWithException)
	 * @see #unchecked(LongBinaryOperatorWithException, Function)
	 */
	@Override
	default LongBinaryOperator uncheck() {
		return (t, u) -> {
			try {
				return applyAsLong(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} return a zero by default.
	 *
	 * @return the lifted function
	 * @see #lifted(LongBinaryOperatorWithException)
	 */
	@Override
	default LongBinaryOperator lift() {
		return ignore();
	}

	/**
	 * Converts this {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperatorOperator} returning zero in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(LongBinaryOperatorWithException)
	 */
	@Override
	default LongBinaryOperator ignore() {
		return (t, u) -> {
			try {
				return applyAsLong(t, u);
			} catch (Exception e) {
				return 0;
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
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongBinaryOperatorWithException, Function)
	 */
	static <E extends Exception> LongBinaryOperator unchecked(LongBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
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
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongBinaryOperatorWithException)
	 */
	static <E extends Exception> LongBinaryOperator unchecked(LongBinaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new LongBinaryOperatorWithException<E>() {

			@Override
			public long applyAsLong(long t, long u) throws E {
				return function.applyAsLong(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> LongBinaryOperator lifted(LongBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.lift();
	}

	/**
	 * Converts a {@code LongBinaryOperatorWithException} to a lifted
	 * {@code LongBinaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> LongBinaryOperator ignored(LongBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

}
