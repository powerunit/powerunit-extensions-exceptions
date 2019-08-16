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

import static ch.powerunit.extensions.exceptions.Constants.CONSUMER_CANT_BE_NULL;
import static ch.powerunit.extensions.exceptions.Constants.EXCEPTIONMAPPER_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts a single input argument, may thrown
 * exception and returns no result. Unlike most other functional interfaces,
 * {@code ConsumerWithException} is expected to operate via side-effects.
 *
 * @see Consumer
 * @param <T>
 *            the type of the input to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface ConsumerWithException<T, E extends Exception> extends NoReturnExceptionHandlerSupport<Consumer<T>> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t
	 *            the input argument
	 * @throws E
	 *             any exception
	 * @see Consumer#accept(Object)
	 */
	void accept(T t) throws E;

	/**
	 * Converts this {@code ConsumerWithException} to a {@code Consumer} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ConsumerWithException)
	 * @see #unchecked(ConsumerWithException, Function)
	 * @see Consumer
	 */
	@Override
	default Consumer<T> uncheck() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), throwingHandler());
	}

	/**
	 * Converts this {@code ConsumerWithException} to a <i>lifted</i>
	 * {@code Consumer} that ignore exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(ConsumerWithException)
	 * @see Consumer
	 */
	@Override
	default Consumer<T> ignore() {
		return t -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t), notThrowingHandler());
	}

	/**
	 * Returns a composed {@code ConsumerWithException} that performs, in sequence,
	 * this operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the composed
	 * operation. If performing this operation throws an exception, the
	 * {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code ConsumerWithException} that performs in sequence
	 *         this operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 * @see Consumer#andThen(Consumer)
	 */
	default ConsumerWithException<T, E> andThen(ConsumerWithException<? super T, ? extends E> after) {
		requireNonNull(after);
		return (T t) -> {
			accept(t);
			after.accept(t);
		};
	}

	/**
	 * Returns a {@code Consumer} that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <E>
	 *            the type of the exception
	 * @return an operation that always throw exception
	 */
	static <T, E extends Exception> ConsumerWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ConsumerWithException} to a {@code Consumer} that wraps
	 * exception to {@code RuntimeException}.
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * ConsumerWithException&lt;String, IOException&gt; consumerThrowingException = ...;
	 *
	 * Consumer&lt;String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.unchecked(consumerThrowingException);
	 *
	 * Stream....forEach(consumerThrowingRuntimeException);
	 * </pre>
	 *
	 * In case of exception inside {@code consumerThrowingRuntimeException} an
	 * instance of {@code WrappedException} with the original exception as cause
	 * will be thrown.
	 *
	 * @param consumer
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, E extends Exception> Consumer<T> unchecked(ConsumerWithException<T, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code ConsumerWithException} to a {@code Consumer} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * ConsumerWithException&lt;String, IOException&gt; consumerThrowingException = ...;
	 *
	 * Consumer&lt;String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.unchecked(
	 *     consumerThrowingException,
	 *     IllegalArgumentException::new
	 *   );
	 *
	 * Stream....forEach(consumerThrowingRuntimeException);
	 * </pre>
	 *
	 * In case of exception inside {@code consumerThrowingRuntimeException} an
	 * instance of {@code IllegalArgumentException} with the original exception as
	 * cause will be thrown.
	 *
	 * @param consumer
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ConsumerWithException)
	 * @throws NullPointerException
	 *             if consumer or exceptionMapper is null
	 */
	static <T, E extends Exception> Consumer<T> unchecked(ConsumerWithException<T, E> consumer,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(consumer, CONSUMER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new ConsumerWithException<T, E>() {

			@Override
			public void accept(T t) throws E {
				consumer.accept(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ConsumerWithException} to a lifted {@code Consumer}
	 * ignoring exception.
	 *
	 * @param consumer
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, E extends Exception> Consumer<T> lifted(ConsumerWithException<T, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code ConsumerWithException} to a lifted {@code Consumer}
	 * ignoring exception.
	 *
	 * @param consumer
	 *            to be lifted
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, E extends Exception> Consumer<T> ignored(ConsumerWithException<T, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).ignore();
	}

	/**
	 * Converts a {@code ConsumerWithException} to a {@code FunctionWithException}
	 * returning {@code null}.
	 *
	 * @param consumer
	 *            to be converted
	 * @param <T>
	 *            the type of the input to the operation
	 * @param <R>
	 *            the type of the return value
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, R, E extends Exception> FunctionWithException<T, R, E> asFunction(ConsumerWithException<T, E> consumer) {
		requireNonNull(consumer, CONSUMER_CANT_BE_NULL);
		return t -> {
			consumer.accept(t);
			return null;
		};
	}

}
