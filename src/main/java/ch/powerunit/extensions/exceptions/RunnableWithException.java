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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts a no input argument and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code Consumer} is expected to operate via side-effects.
 * 
 * @author borettim
 * @see Runnable
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface RunnableWithException<E extends Exception> extends ExceptionHandlerSupport {

	/**
	 * Performs this operation.
	 *
	 * @throws E
	 *             any exception
	 * @see Runnable#run()
	 */
	void run() throws E;

	/**
	 * Converts this {@code RunnableWithException} to a {@code Runnable} that
	 * convert exception to {@code RuntimeException}.
	 * 
	 * @return the unchecked operation
	 * @see #unchecked(RunnableWithException)
	 * @see #unchecked(RunnableWithException, Function)
	 */
	default Runnable uncheck() {
		return () -> {
			try {
				run();
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code RunnableWithException} to a <i>lifted</i>
	 * {@code Runnable} ignoring exception.
	 * 
	 * @return the operation that ignore error
	 * @see #ignored(RunnableWithException)
	 */
	default Runnable ignore() {
		return () -> {
			try {
				run();
			} catch (Exception e) {
				// ignore
			}
		};
	}

	/**
	 * Transforms this {@code RunnableWithException} to a
	 * {@code FunctionWithException} that returns nothing.
	 * 
	 * @return the function
	 * @param <T>
	 *            The type for the ignored input parameter
	 * @see #function(RunnableWithException)
	 */
	default <T> FunctionWithException<T, Void, E> asFunction() {
		return t -> {
			run();
			return null;
		};
	}

	/**
	 * Transforms this {@code RunnableWithException} to a
	 * {@code ConsumerWithException} that returns nothing.
	 * 
	 * @return the function
	 * @param <T>
	 *            The type for the ignored input parameter
	 * @see #function(RunnableWithException)
	 */
	default <T> ConsumerWithException<T, E> asConsumer() {
		return t -> {
			run();
		};
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
	 * Converts a {@code RunnableWithException} to a {@code Runnable} that convert
	 * exception to {@code RuntimeException}.
	 * 
	 * @param operation
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(RunnableWithException, Function)
	 */
	static <E extends Exception> Runnable unchecked(RunnableWithException<E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.uncheck();
	}

	/**
	 * Converts a {@code RunnableWithException} to a {@code Runnable} that convert
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
	 */
	static <E extends Exception> Runnable unchecked(RunnableWithException<E> operation,
			Function<Exception,RuntimeException> exceptionMapper) {
		requireNonNull(operation, "function can't be null");
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new RunnableWithException<E>() {

			@Override
			public void run() throws E {
				operation.run();
			}

			@Override
			public Function<Exception,RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code RunnableWithException} to a lifted {@code Runnable}
	 * returning {@code null} in case of exception.
	 * 
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 */
	static <E extends Exception> Runnable ignored(RunnableWithException<E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.ignore();
	}

	/**
	 * Transforms a {@code RunnableWithException} to a {@code FunctionWithException} that
	 * returns nothing.
	 * 
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asFunction()
	 */
	static <T, E extends Exception> FunctionWithException<T, Void, E> function(RunnableWithException<E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.asFunction();
	}

	/**
	 * Transforms a {@code RunnableWithException} to a {@code ConsumerWithException} that
	 * returns nothing.
	 * 
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asFunction()
	 */
	static <T, E extends Exception> ConsumerWithException<T, E> consumer(RunnableWithException<E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.asConsumer();
	}

}
