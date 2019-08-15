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

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation upon two operands of the same type, may throw
 * exception and producing a result of the same type as the operands. This is a
 * specialization of {@code FunctionWithException} for the case where the
 * operand and result are of the same type.
 *
 * @author borettim
 * @see FunctionWithException
 * @see BinaryOperator
 * @param <T>
 *            the type of the input and output to the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface BinaryOperatorWithException<T, E extends Exception> extends BiFunctionWithException<T, T, T, E> {

	/**
	 * Converts this {@code BinaryOperatorWithException} to a {@code BinaryOperator}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(BinaryOperatorWithException)
	 * @see #unchecked(BinaryOperatorWithException, Function)
	 */
	@Override
	default BinaryOperator<T> uncheck() {
		return (t, u) -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t, u), throwingHandler());
	}

	/**
	 * Converts this {@code BinaryOperatorWithException} to a lifted
	 * {@code BinaryOperator} returning {@code null} in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(BinaryOperatorWithException)
	 */
	@Override
	default BinaryOperator<T> ignore() {
		return (t, u) -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t, u), e -> null);
	}

	/**
	 * Returns a binary operator that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input and output object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, E extends Exception> BinaryOperatorWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BinaryOperatorWithException} to a {@code BinaryOperator}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BinaryOperatorWithException, Function)
	 */
	static <T, E extends Exception> BinaryOperator<T> unchecked(BinaryOperatorWithException<T, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
	}

	/**
	 * Converts a {@code BinaryOperatorWithException} to a {@code BinaryOperator}
	 * that convert exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input and output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BinaryOperatorWithException)
	 */
	static <T, E extends Exception> BinaryOperator<T> unchecked(BinaryOperatorWithException<T, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new BinaryOperatorWithException<T, E>() {

			@Override
			public T apply(T t, T u) throws E {
				return function.apply(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BinaryOperatorWithException} to a lifted
	 * {@code BinaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input and output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, E extends Exception> BinaryOperator<T> ignored(BinaryOperatorWithException<T, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

}
