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
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

/**
 * Represents a int binary operator with exception.
 *
 * @author borettim
 * @see IntBinaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntBinaryOperatorWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<IntBinaryOperator> {

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
	 * @see IntBinaryOperator#applyAsInt(int, int)
	 */
	int applyAsInt(int t, int u) throws E;

	/**
	 * Converts this {@code IntBinaryOperatorWithException} to a
	 * {@code IntBinaryOperator} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(IntBinaryOperatorWithException)
	 * @see #unchecked(IntBinaryOperatorWithException, Function)
	 */
	@Override
	default IntBinaryOperator uncheck() {
		return (t, u) -> {
			try {
				return applyAsInt(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code IntBinaryOperatorWithException} to a lifted
	 * {@code IntBinaryOperatorOperator} returning zero in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(IntBinaryOperatorWithException)
	 */
	@Override
	default IntBinaryOperator ignore() {
		return (t, u) -> {
			try {
				return applyAsInt(t, u);
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
	static <E extends Exception> IntBinaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntBinaryOperatorWithException} to a
	 * {@code IntBinaryOperator} that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntBinaryOperatorWithException, Function)
	 */
	static <E extends Exception> IntBinaryOperator unchecked(IntBinaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code IntBinaryOperatorWithException} to a
	 * {@code IntBinaryOperator} that convert exception to {@code RuntimeException}
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
	 * @see #unchecked(IntBinaryOperatorWithException)
	 */
	static <E extends Exception> IntBinaryOperator unchecked(IntBinaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new IntBinaryOperatorWithException<E>() {

			@Override
			public int applyAsInt(int t, int u) throws E {
				return function.applyAsInt(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntBinaryOperatorWithException} to a lifted
	 * {@code IntBinaryOperator} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> IntBinaryOperator lifted(IntBinaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code IntBinaryOperatorWithException} to a lifted
	 * {@code IntBinaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> IntBinaryOperator ignored(IntBinaryOperatorWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
