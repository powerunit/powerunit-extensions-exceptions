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

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts an object-valued and a
 * {@code double}-valued argument, and returns no result. This is the
 * {@code (reference, double)} specialization of
 * {@link BiConsumerWithException}. Unlike most other functional interfaces,
 * {@code ObjDoubleConsumerWithException} is expected to operate via
 * side-effects.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(Object, double) void accept(T t, double value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code ObjDoubleConsumer<T>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code ObjDoubleConsumer<T>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code ObjDoubleConsumer<T>}</li>
 * </ul>
 *
 * @see ObjDoubleConsumer
 * @param <T>
 *            the type of the object argument to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface ObjDoubleConsumerWithException<T, E extends Exception>
		extends NoReturnExceptionHandlerSupport<ObjDoubleConsumer<T>, BiFunction<T, Double, CompletionStage<Void>>> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param value
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see ObjDoubleConsumer#accept(Object,double)
	 */
	void accept(T t, double value) throws E;

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a
	 * {@code ObjDoubleConsumer} that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ObjDoubleConsumerWithException)
	 * @see #unchecked(ObjDoubleConsumerWithException, Function)
	 */
	@Override
	default ObjDoubleConsumer<T> uncheck() {
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), throwingHandler());
	}

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a <i>lifted</i>
	 * {@code ObjDoubleConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(ObjDoubleConsumerWithException)
	 */
	@Override
	default ObjDoubleConsumer<T> ignore() {
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), notThrowingHandler());
	}

	/**
	 * Converts this {@code ObjDoubleConsumerWithException} to a <i>staged</i>
	 * {@code BiFunction} that return a {@code CompletionStage}.
	 * 
	 * @return the staged operation.
	 * @since 1.1.0
	 */
	@Override
	default BiFunction<T, Double, CompletionStage<Void>> stage() {
		return (t, value) -> NoReturnExceptionHandlerSupport.staged(() -> accept(t, value));
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
	static <T, E extends Exception> ObjDoubleConsumerWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ObjDoubleConsumerWithException} to a
	 * {@code ObjDoubleConsumer} that wraps exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(ObjDoubleConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> ObjDoubleConsumer<T> unchecked(ObjDoubleConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a
	 * {@code ObjDoubleConsumer} that wraps exception to {@code RuntimeException} by
	 * using the provided mapping function.
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
	 * @see #unchecked(ObjDoubleConsumerWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <T, E extends Exception> ObjDoubleConsumer<T> unchecked(ObjDoubleConsumerWithException<T, E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new ObjDoubleConsumerWithException<T, E>() {

			@Override
			public void accept(T t, double value) throws E {
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
	 * {@code ObjDoubleConsumer} ignoring exception.
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
	static <T, E extends Exception> ObjDoubleConsumer<T> lifted(ObjDoubleConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjDoubleConsumer} ignoring exception.
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
	static <T, E extends Exception> ObjDoubleConsumer<T> ignored(ObjDoubleConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).ignore();
	}

	/**
	 * Converts a {@code ObjDoubleConsumerWithException} to a staged
	 * {@code BiFunction} .
	 *
	 * @param operation
	 *            to be staged
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the staged operation
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> BiFunction<T, Double, CompletionStage<Void>> staged(
			ObjDoubleConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL).stage();
	}

	/**
	 * Converts a {@code ObjDoubleConsumerWithException} to a
	 * {@code BiConsumerWithException} returning {@code null}.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <T>
	 *            the type of the object argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @throws NullPointerException
	 *             if operation is null
	 * @since 1.1.0
	 */
	static <T, E extends Exception> BiConsumerWithException<T, Double, E> asBiConsumer(
			ObjDoubleConsumerWithException<T, E> operation) {
		return requireNonNull(operation, OPERATION_CANT_BE_NULL)::accept;
	}

}
