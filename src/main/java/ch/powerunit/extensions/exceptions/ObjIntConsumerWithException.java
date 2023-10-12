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

import static ch.powerunit.extensions.exceptions.Constants.verifyExceptionMapper;
import static ch.powerunit.extensions.exceptions.Constants.verifyOperation;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts an object-valued and a
 * {@code int}-valued argument, and returns no result. This is the
 * {@code (reference, int)} specialization of {@link BiConsumerWithException}.
 * Unlike most other functional interfaces, {@code ObjIntConsumer} is expected
 * to operate via side-effects.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #accept(Object, int) void accept(T t, int value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code ObjIntConsumer<T>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code ObjIntConsumer<T>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code ObjIntConsumer<T>}</li>
 * </ul>
 *
 * @see ObjIntConsumer
 * @param <T>
 *            the type of the object argument to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface ObjIntConsumerWithException<T, E extends Exception> extends
		NoReturnExceptionHandlerSupport<ObjIntConsumer<T>, BiFunction<T, Integer, CompletionStage<Void>>, ObjIntConsumerWithException<T, E>> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param value
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see ObjIntConsumer#accept(Object,int)
	 */
	void accept(T t, int value) throws E;

	/**
	 * Converts this {@code ObjIntConsumerWithException} to a {@code ObjIntConsumer}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ObjIntConsumerWithException)
	 * @see #unchecked(ObjIntConsumerWithException, Function)
	 */
	@Override
	default ObjIntConsumer<T> uncheck() {
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), throwingHandler());
	}

	/**
	 * Converts this {@code ObjIntConsumerWithException} to a <i>lifted</i>
	 * {@code ObjIntConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(ObjIntConsumerWithException)
	 */
	@Override
	default ObjIntConsumer<T> ignore() {
		return (t, value) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, value), notThrowingHandler());
	}

	/**
	 * Converts this {@code ObjIntConsumerWithException} to a <i>staged</i>
	 * {@code BiFunction} that return a {@code CompletionStage}.
	 *
	 * @return the staged operation.
	 * @since 1.1.0
	 */
	@Override
	default BiFunction<T, Integer, CompletionStage<Void>> stage() {
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
	static <T, E extends Exception> ObjIntConsumerWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a {@code ObjIntConsumer}
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
	 * @see #unchecked(ObjIntConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <T, E extends Exception> ObjIntConsumer<T> unchecked(ObjIntConsumerWithException<T, E> operation) {
		return verifyOperation(operation).uncheck();
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a {@code ObjIntConsumer}
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
	 * @see #unchecked(ObjIntConsumerWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <T, E extends Exception> ObjIntConsumer<T> unchecked(ObjIntConsumerWithException<T, E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyOperation(operation);
		verifyExceptionMapper(exceptionMapper);
		return new ObjIntConsumerWithException<T, E>() {

			@Override
			public void accept(T t, int value) throws E {
				operation.accept(t, value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a lifted
	 * {@code ObjIntConsumer} ignoring exception.
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
	static <T, E extends Exception> ObjIntConsumer<T> lifted(ObjIntConsumerWithException<T, E> operation) {
		return verifyOperation(operation).lift();
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a lifted
	 * {@code ObjIntConsumer} ignoring exception.
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
	static <T, E extends Exception> ObjIntConsumer<T> ignored(ObjIntConsumerWithException<T, E> operation) {
		return verifyOperation(operation).ignore();
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a staged {@code BiFunction}
	 * .
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
	 * @since 1.1.0
	 */
	static <T, E extends Exception> BiFunction<T, Integer, CompletionStage<Void>> staged(
			ObjIntConsumerWithException<T, E> operation) {
		return verifyOperation(operation).stage();
	}

	/**
	 * Converts a {@code ObjIntConsumerWithException} to a
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
	static <T, E extends Exception> BiConsumerWithException<T, Integer, E> asBiConsumer(
			ObjIntConsumerWithException<T, E> operation) {
		return verifyOperation(operation)::accept;
	}

}
