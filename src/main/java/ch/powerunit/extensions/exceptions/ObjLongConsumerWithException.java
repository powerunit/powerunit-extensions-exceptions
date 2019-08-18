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
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts an object-valued and a
 * {@code long}-valued argument, and returns no result. This is the
 * {@code (reference, long)} specialization of {@link BiConsumerWithException}.
 * Unlike most other functional interfaces, {@code ObjLongConsumerWithException}
 * is expected to operate via side-effects.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(Object, long) void accept(T t, long value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code ObjLongConsumer<T>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code ObjLongConsumer<T>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code ObjLongConsumer<T>}</li>
 * </ul>
 *
 * @see ObjLongConsumer
 * @param <T>
 *            the type of the object argument to the operation
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
	 * @param value
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see ObjLongConsumer#accept(Object,long)
	 */
	void accept(T t, long value) throws E;

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a
	 * {@code ObjLongConsumer} that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ObjLongConsumerWithException)
	 * @see #unchecked(ObjLongConsumerWithException, Function)
	 */
	@Override
	default ObjLongConsumer<T> uncheck() {
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), throwingHandler());
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
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), notThrowingHandler());
	}

	/**
	 * Returns an operation that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the object argument to the operation
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
	 * Converts a {@code ObjLongConsumerWithException} to a {@code ObjLongConsumer}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(ObjLongConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> ObjLongConsumer<T> unchecked(ObjLongConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a {@code ObjLongConsumer}
	 * that wraps exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(ObjLongConsumerWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <T, E extends Exception> ObjLongConsumer<T> unchecked(ObjLongConsumerWithException<T, E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new ObjLongConsumerWithException<T, E>() {

			@Override
			public void accept(T t, long value) throws E {
				operation.accept(t, value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjLongConsumer} ignoring exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> ObjLongConsumer<T> lifted(ObjLongConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjLongConsumer} ignoring exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> ObjLongConsumer<T> ignored(ObjLongConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).ignore();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a
	 * {@code BiConsumerWithException} returning {@code null}.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the bi consumer
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> BiConsumerWithException<T, Long, E> asBiConsumer(
			ObjLongConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL)::accept;
	}

}
