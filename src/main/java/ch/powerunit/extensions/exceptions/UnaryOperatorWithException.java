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
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Represents an operation on a single operand, may throw exception and that
 * produces a result of the same type as its operand. This is a specialization
 * of {@code FunctionWithException} for the case where the operand and result
 * are of the same type.
 *
 * @author borettim
 * @see FunctionWithException
 * @see UnaryOperator
 * @param <T>
 *            the type of the input and output to the function
 */
@FunctionalInterface
public interface UnaryOperatorWithException<T, E extends Exception> extends FunctionWithException<T, T, E> {

	/**
	 * Converts this {@code UnaryOperatorWithException} to a {@code UnaryOperator}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(FunctionWithException)
	 * @see #unchecked(FunctionWithException, Function)
	 */
	@Override
	default UnaryOperator<T> uncheck() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), e -> {
			throw exceptionMapper().apply(e);
		});
	}

	/**
	 * Converts this {@code UnaryOperatorWithException} to a lifted
	 * {@code UnaryOperator} returning {@code null} in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(FunctionWithException)
	 */
	@Override
	default UnaryOperator<T> ignore() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t), e -> null);
	}

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @param <T>
	 *            the type of the input and output objects to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return a function that always returns its input argument
	 * @see Function#identity()
	 */
	static <T, E extends Exception> UnaryOperatorWithException<T, E> identity() {
		return t -> t;
	}

	/**
	 * Returns a unary operator that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input and output object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, E extends Exception> UnaryOperatorWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code UnaryOperatorWithException} to a {@code UnaryOperator} that
	 * convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(UnaryOperatorWithException, Function)
	 */
	static <T, E extends Exception> UnaryOperator<T> unchecked(UnaryOperatorWithException<T, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
	}

	/**
	 * Converts a {@code UnaryOperatorWithException} to a {@code UnaryOperator} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
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
	 * @see #unchecked(UnaryOperatorWithException)
	 */
	static <T, E extends Exception> UnaryOperator<T> unchecked(UnaryOperatorWithException<T, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new UnaryOperatorWithException<T, E>() {

			@Override
			public T apply(T t) throws E {
				return function.apply(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code UnaryOperatorWithException} to a lifted
	 * {@code UnaryOperator} returning {@code null} in case of exception.
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
	static <T, E extends Exception> UnaryOperator<T> ignored(UnaryOperatorWithException<T, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

	/**
	 * Transforms this {@code UnaryOperatorWithException} to a
	 * {@code ConsumerWithException}.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input and output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asConsumer()
	 */
	static <T, E extends Exception> ConsumerWithException<T, Exception> asConsumer(
			UnaryOperatorWithException<T, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.asConsumer();
	}

}
