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
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts a single {@code double}-valued argument,
 * may throw exception and returns no result. This is the primitive type
 * specialization of {@link ConsumerWithException} for {@code double}. Unlike
 * most other functional interfaces, {@code DoubleConsumerWithException} is
 * expected to operate via side-effects.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(double) void accept(double value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code DoubleConsumer}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code DoubleConsumer}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code DoubleConsumer}</li>
 * </ul>
 *
 * @see DoubleConsumer
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface DoubleConsumerWithException<E extends Exception>
		extends NoReturnExceptionHandlerSupport<DoubleConsumer, DoubleFunction<CompletionStage<Void>>> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param value
	 *            the input argument
	 * @throws E
	 *             any exception
	 * @see DoubleConsumer#accept(double)
	 */
	void accept(double value) throws E;

	/**
	 * Converts this {@code DoubleConsumerWithException} to a {@code DoubleConsumer}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(DoubleConsumerWithException)
	 * @see #unchecked(DoubleConsumerWithException, Function)
	 */
	@Override
	default DoubleConsumer uncheck() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), throwingHandler());
	}

	/**
	 * Converts this {@code DoubleConsumerWithException} to a <i>lifted</i>
	 * {@code DoubleConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(DoubleConsumerWithException)
	 */
	@Override
	default DoubleConsumer ignore() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), notThrowingHandler());
	}

	/**
	 * Converts this {@code DoubleConsumerWithException} to a <i>staged</i>
	 * {@code DoubleFunction} that return a {@code CompletionStage}.
	 *
	 * @return the staged operation.
	 * @since 1.1.0
	 */
	@Override
	default DoubleFunction<CompletionStage<Void>> stage() {
		return t -> NoReturnExceptionHandlerSupport.staged(() -> accept(t));
	}

	/**
	 * Returns a composed {@code DoubleConsumerWithException} that performs, in
	 * sequence, this operation followed by the {@code after} operation. If
	 * performing either operation throws an exception, it is relayed to the caller
	 * of the composed operation. If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code DoubleConsumer} that performs in sequence this
	 *         operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 *
	 * @see DoubleConsumer#andThen(DoubleConsumer)
	 */
	default DoubleConsumerWithException<E> andThen(DoubleConsumerWithException<? extends E> after) {
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
	static <E extends Exception> DoubleConsumerWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a {@code DoubleConsumer}
	 * that wraps exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(DoubleConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if operation is null
	 */
	static <E extends Exception> DoubleConsumer unchecked(DoubleConsumerWithException<E> operation) {
		return verifyOperation(operation).uncheck();
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a {@code DoubleConsumer}
	 * that wraps exception to {@code RuntimeException} by using the provided
	 * mapping function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked operation
	 * @see #uncheck()
	 * @see #unchecked(DoubleConsumerWithException)
	 * @throws NullPointerException
	 *             if operation or exceptionMapper is null
	 */
	static <E extends Exception> DoubleConsumer unchecked(DoubleConsumerWithException<E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyOperation(operation);
		verifyExceptionMapper(exceptionMapper);
		return new DoubleConsumerWithException<E>() {

			@Override
			public void accept(double value) throws E {
				operation.accept(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a lifted
	 * {@code DoubleConsumer} ignoring exception.
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
	static <E extends Exception> DoubleConsumer lifted(DoubleConsumerWithException<E> operation) {
		return verifyOperation(operation).lift();
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a lifted
	 * {@code DoubleConsumer} ignoring exception.
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
	static <E extends Exception> DoubleConsumer ignored(DoubleConsumerWithException<E> operation) {
		return verifyOperation(operation).ignore();
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a staged
	 * {@code DoubleFunction}.
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
	static <E extends Exception> DoubleFunction<CompletionStage<Void>> staged(
			DoubleConsumerWithException<E> operation) {
		return verifyOperation(operation).stage();
	}

	/**
	 * Converts a {@code DoubleConsumerWithException} to a
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
	static <E extends Exception> ConsumerWithException<Double, E> asConsumer(DoubleConsumerWithException<E> operation) {
		return verifyOperation(operation)::accept;
	}

}
