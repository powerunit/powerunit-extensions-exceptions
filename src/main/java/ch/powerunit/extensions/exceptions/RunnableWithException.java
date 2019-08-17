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
import static ch.powerunit.extensions.exceptions.Constants.OPERATION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts no input argument and returns no result
 * and may throw exception. Unlike most other functional interfaces,
 * {@code RunnableWithException} is expected to operate via side-effects.
 *
 * @see Runnable
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface RunnableWithException<E extends Exception> extends NoReturnExceptionHandlerSupport<Runnable> {

	/**
	 * Performs this operation.
	 *
	 * @throws E
	 *             any exception
	 * @see Runnable#run()
	 */
	void run() throws E;

	/**
	 * Converts this {@code RunnableWithException} to a {@code Runnable} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(RunnableWithException)
	 * @see #unchecked(RunnableWithException, Function)
	 */
	@Override
	default Runnable uncheck() {
		return () -> NoReturnExceptionHandlerSupport.unchecked(this::run, throwingHandler());
	}

	/**
	 * Converts this {@code RunnableWithException} to a <i>lifted</i>
	 * {@code Runnable} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(RunnableWithException)
	 */
	@Override
	default Runnable ignore() {
		return () -> NoReturnExceptionHandlerSupport.unchecked(this::run, notThrowingHandler());
	}

	/**
	 * Returns an operation that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <E extends Exception> RunnableWithException<E> failing(Supplier<E> exceptionBuilder) {
		return () -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code RunnableWithException} to a {@code Runnable} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(RunnableWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> Runnable unchecked(RunnableWithException<E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code RunnableWithException} to a {@code Runnable} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(RunnableWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <E extends Exception> Runnable unchecked(RunnableWithException<E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new RunnableWithException<E>() {

			@Override
			public void run() throws E {
				operation.run();
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code RunnableWithException} to a lifted {@code Runnable}
	 * ignoring exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> Runnable lifted(RunnableWithException<E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code RunnableWithException} to a lifted {@code Runnable}
	 * ignoring exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> Runnable ignored(RunnableWithException<E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).ignore();
	}

}
