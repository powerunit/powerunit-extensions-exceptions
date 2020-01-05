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
import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts a single {@code int}-valued argument,
 * may throw exception and returns no result. This is the primitive type
 * specialization of {@link ConsumerWithException} for {@code int}. Unlike most
 * other functional interfaces, {@code IntConsumerWithException} is expected to
 * operate via side-effects.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(int) void accept(int value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code IntConsumer}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code IntConsumer}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code IntConsumer}</li>
 * </ul>
 *
 * @see IntConsumer
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface IntConsumerWithException<E extends Exception> extends
		NoReturnExceptionHandlerSupport<IntConsumer, IntFunction<CompletionStage<Void>>, IntConsumerWithException<E>> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param value
	 *            the input argument
	 * @throws E
	 *             any exception
	 * @see IntConsumer#accept(int)
	 */
	void accept(int value) throws E;

	/**
	 * Converts this {@code IntConsumerWithException} to a {@code IntConsumer} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(IntConsumerWithException)
	 * @see #unchecked(IntConsumerWithException, Function)
	 */
	@Override
	default IntConsumer uncheck() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), throwingHandler());
	}

	/**
	 * Converts this {@code IntConsumerWithException} to a <i>lifted</i>
	 * {@code IntConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(IntConsumerWithException)
	 */
	@Override
	default IntConsumer ignore() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), notThrowingHandler());
	}

	/**
	 * Converts this {@code IntConsumerWithException} to a <i>staged</i>
	 * {@code IntFunction} that return a {@code CompletionStage}.
	 *
	 * @return the staged operation.
	 * @since 1.1.0
	 */
	@Override
	default IntFunction<CompletionStage<Void>> stage() {
		return t -> NoReturnExceptionHandlerSupport.staged(() -> accept(t));
	}

	/**
	 * Returns a composed {@code IntConsumerWithException} that performs, in
	 * sequence, this operation followed by the {@code after} operation. If
	 * performing either operation throws an exception, it is relayed to the caller
	 * of the composed operation. If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code IntConsumer} that performs in sequence this
	 *         operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 *
	 *
	 * @see IntConsumer#andThen(IntConsumer)
	 */
	default IntConsumerWithException<E> andThen(IntConsumerWithException<? extends E> after) {
		requireNonNull(after);
		return t -> {
			accept(t);
			after.accept(t);
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
	static <E extends Exception> IntConsumerWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a {@code IntConsumer} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(IntConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> IntConsumer unchecked(IntConsumerWithException<E> operation) {
		return verifyOperation(operation).uncheck();
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a {@code IntConsumer} that
	 * wraps exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(IntConsumerWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <E extends Exception> IntConsumer unchecked(IntConsumerWithException<E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyOperation(operation);
		verifyExceptionMapper(exceptionMapper);
		return new IntConsumerWithException<E>() {

			@Override
			public void accept(int value) throws E {
				operation.accept(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a lifted {@code IntConsumer}
	 * ignoring exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 */
	static <E extends Exception> IntConsumer lifted(IntConsumerWithException<E> operation) {
		return verifyOperation(operation).lift();
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a lifted {@code IntConsumer}
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
	static <E extends Exception> IntConsumer ignored(IntConsumerWithException<E> operation) {
		return verifyOperation(operation).ignore();
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a staged {@code IntFunction} .
	 *
	 * @param operation
	 *            to be staged
	 * @param <E>
	 *            the type of the potential exception
	 * @return the staged operation
	 * @throws NullPointerException
	 *             if operation is null
	 * @since 1.1.0
	 */
	static <E extends Exception> IntFunction<CompletionStage<Void>> staged(IntConsumerWithException<E> operation) {
		return verifyOperation(operation).stage();
	}

	/**
	 * Converts a {@code IntConsumerWithException} to a
	 * {@code ConsumerWithException}.
	 *
	 * @param operation
	 *            to be converted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the consumer
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> ConsumerWithException<Integer, E> asConsumer(IntConsumerWithException<E> operation) {
		return verifyOperation(operation)::accept;
	}

}
