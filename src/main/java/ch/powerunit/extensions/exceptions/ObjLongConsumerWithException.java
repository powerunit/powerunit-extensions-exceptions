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
import static ch.powerunit.extensions.exceptions.Constants.OPERATION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code Consumer} is expected to operate via side-effects.
 *
 * @author borettim
 * @see ObjLongConsumer
 * @param <T>
 *            the type of the input to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface ObjLongConsumerWithException<T, E extends Exception>
		extends NoReturnExceptionHandlerSupport<ObjLongConsumer<T>> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see ObjLongConsumer#accept(Object,long)
	 */
	void accept(T t, long u) throws E;

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a
	 * {@code ObjLongConsumer} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ObjLongConsumerWithException)
	 * @see #unchecked(ObjLongConsumerWithException, Function)
	 */
	@Override
	default ObjLongConsumer<T> uncheck() {
		return (t, u) -> {
			try {
				accept(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a <i>lifted</i>
	 * {@code ObjLongConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(ObjLongConsumerWithException)
	 */
	@Override
	default ObjLongConsumer<T> ignore() {
		return (t, u) -> {
			try {
				accept(t, u);
			} catch (Exception e) {
				// ignore
			}
		};
	}

	/**
	 * Transforms this {@code ObjLongConsumerWithException} to a
	 * {@code BiFunctionWithException} that returns nothing.
	 *
	 * @return the function
	 * @see #biFunction(ObjLongConsumerWithException)
	 */
	default BiFunctionWithException<T, Long, Void, E> asBiFunction() {
		return (t, u) -> {
			accept(t, u);
			return null;
		};
	}

	/**
	 * Returns an operation that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <T, E extends Exception> ObjLongConsumerWithException<T, E> failing(Supplier<E> exceptionBuilder) {
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
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ObjLongConsumerWithException, Function)
	 */
	static <T, E extends Exception> ObjLongConsumer<T> unchecked(ObjLongConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a {@code ObjLongConsumer}
	 * that convert exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ObjLongConsumerWithException)
	 */
	static <T, E extends Exception> ObjLongConsumer<T> unchecked(ObjLongConsumerWithException<T, E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new ObjLongConsumerWithException<T, E>() {

			@Override
			public void accept(T t, long u) throws E {
				operation.accept(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjLongConsumer} returning {@code null} in case of exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 */
	static <T, E extends Exception> ObjLongConsumer<T> lifted(ObjLongConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.lift();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjLongConsumer} returning {@code null} in case of exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 */
	static <T, E extends Exception> ObjLongConsumer<T> ignored(ObjLongConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.ignore();
	}

	/**
	 * Transforms a {@code ObjLongConsumerWithException} to a
	 * {@code ObjLongConsumerWithException} that returns nothing.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asBiFunction()
	 */
	static <T, E extends Exception> BiFunctionWithException<T, Long, Void, E> biFunction(
			ObjLongConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.asBiFunction();
	}

}
