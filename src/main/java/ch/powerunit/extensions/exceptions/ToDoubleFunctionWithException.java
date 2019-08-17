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

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Represents a function that produces a double-valued result, may throw
 * exception. This is the {@code double}-producing primitive specialization for
 * {@link FunctionWithException}.
 *
 * @see ToDoubleFunction
 * @param <T>
 *            the type of the input to the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface ToDoubleFunctionWithException<T, E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<ToDoubleFunction<T>> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see ToDoubleFunction#applyAsDouble(Object)
	 */
	double applyAsDouble(T value) throws E;

	@Override
	default ToDoubleFunction<T> uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return applyAsDouble(value);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return 0;
			}
		};
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, E extends Exception> ToDoubleFunctionWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ToDoubleFunctionException} to a {@code ToDoubleFunction}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ToDoubleFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToDoubleFunction<T> unchecked(ToDoubleFunctionWithException<T, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code ToDoubleFunctionWithException} to a
	 * {@code ToDoubleFunction} that convert exception to {@code RuntimeException}
	 * by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked function
	 * @see #uncheck()
	 * @see #unchecked(ToDoubleFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <T, E extends Exception> ToDoubleFunction<T> unchecked(ToDoubleFunctionWithException<T, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new ToDoubleFunctionWithException<T, E>() {

			@Override
			public double applyAsDouble(T value) throws E {
				return function.applyAsDouble(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ToDoubleFunctionWithException} to a lifted
	 * {@code ToDoubleFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToDoubleFunction<T> lifted(ToDoubleFunctionWithException<T, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code ToDoubleFunctionWithException} to a lifted
	 * {@code ToDoubleFunction} returning {@code 0} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, E extends Exception> ToDoubleFunction<T> ignored(ToDoubleFunctionWithException<T, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
