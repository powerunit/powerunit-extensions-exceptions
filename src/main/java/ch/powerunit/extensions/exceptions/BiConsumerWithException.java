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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code Consumer} is expected to operate via side-effects.
 * 
 * @author borettim
 * @see Consumer
 * @param <T>
 *            the type of the input to the operation
 * @param <U>
 *            the type of the second argument to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface BiConsumerWithException<T, U, E extends Exception> extends ExceptionHandlerSupport {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see BiConsumer#accept(Object,Object)
	 */
	void accept(T t, U u) throws E;

	/**
	 * Converts this {@code BiConsumerWithException} to a {@code BiConsumer} that
	 * convert exception to {@code RuntimeException}.
	 * 
	 * @return the unchecked operation
	 * @see #unchecked(BiConsumerWithException)
	 * @see #unchecked(BiConsumerWithException, Function)
	 */
	default BiConsumer<T, U> uncheck() {
		return (t, u) -> {
			try {
				accept(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code BiConsumerWithException} to a <i>lifted</i>
	 * {@code BiConsumer} ignoring exception.
	 * 
	 * @return the operation that ignore error
	 * @see #ignored(BiConsumerWithException)
	 */
	default BiConsumer<T, U> ignore() {
		return (t, u) -> {
			try {
				accept(t, u);
			} catch (Exception e) {
				// ignore
			}
		};
	}

	/**
	 * Transforms this {@code BiConsumerWithException} to a
	 * {@code BiFunctionWithException} that returns nothing.
	 * 
	 * @return the function
	 * @see #biFunction(BiConsumerWithException)
	 */
	default BiFunctionWithException<T, U, Void, E> asBiFunction() {
		return (t, u) -> {
			accept(t, u);
			return null;
		};
	}

	/**
	 * Returns a composed {@code BiConsumerWithException} that performs, in
	 * sequence, this operation followed by the {@code after} operation. If
	 * performing either operation throws an exception, it is relayed to the caller
	 * of the composed operation. If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code Consumer} that performs in sequence this operation
	 *         followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 *
	 * 
	 * @see BiConsumer#andThen(BiConsumer)
	 */
	default BiConsumerWithException<T, U, E> andThen(BiConsumerWithException<? super T, ? super U, ? extends E> after) {
		requireNonNull(after);
		return (T t, U u) -> {
			accept(t, u);
			after.accept(t, u);
		};
	}

	/**
	 * Returns an operation that always throw exception.
	 * 
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <U>
	 *            the type of the second input object to the operation
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <T, U, E extends Exception> BiConsumerWithException<T, U, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a {@code Consumer} that convert
	 * exception to {@code RuntimeException}.
	 * 
	 * @param operation
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <U>
	 *            the type of the second input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiConsumerWithException, Function)
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> unchecked(BiConsumerWithException<T, U, E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.uncheck();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a {@code BiConsumer} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 * 
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <U>
	 *            the type of the second input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiConsumerWithException)
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> unchecked(BiConsumerWithException<T, U, E> operation,
			Function<Exception, ? extends RuntimeException> exceptionMapper) {
		requireNonNull(operation, "function can't be null");
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new BiConsumerWithException<T, U, E>() {

			@Override
			public void accept(T t, U u) throws E {
				operation.accept(t, u);
			}

			@Override
			public Function<Exception, ? extends RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a lifted {@code BiConsumer}
	 * returning {@code null} in case of exception.
	 * 
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <U>
	 *            the type of the second input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> ignored(BiConsumerWithException<T, U, E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.ignore();
	}

	/**
	 * Transforms a {@code BiConsumerWithException} to a
	 * {@code BiFunctionWithException} that returns nothing.
	 * 
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <U>
	 *            the type of the second input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asBiFunction()
	 */
	static <T, U, E extends Exception> BiFunctionWithException<T, U, Void, E> biFunction(
			BiConsumerWithException<T, U, E> operation) {
		requireNonNull(operation, "operation can't be null");
		return operation.asBiFunction();
	}

}
