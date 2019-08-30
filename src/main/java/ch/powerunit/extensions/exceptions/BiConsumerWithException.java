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

import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code BiConsumerWithException} is expected to operate via side-effects.
 *
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(Object, Object) void accept(T t,U u) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code Biconsumer<T,U>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code Biconsumer<T,U>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code Biconsumer<T,U>}</li>
 * </ul>
 *
 * @see BiConsumer
 * @param <T>
 *            the type of the first argument to the operation
 * @param <U>
 *            the type of the second argument to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface BiConsumerWithException<T, U, E extends Exception>
		extends NoReturnExceptionHandlerSupport<BiConsumer<T, U>, BiFunction<T, U, CompletionStage<Void>>> {

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
	 * wraps exception into {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(BiConsumerWithException)
	 * @see #unchecked(BiConsumerWithException, Function)
	 * @see BiConsumer
	 */
	@Override
	default BiConsumer<T, U> uncheck() {
		return (t, u) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, u), throwingHandler());
	}

	/**
	 * Converts this {@code BiConsumerWithException} to a <i>lifted</i>
	 * {@code BiConsumer} that ignore exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(BiConsumerWithException)
	 * @see BiConsumer
	 */
	@Override
	default BiConsumer<T, U> ignore() {
		return (t, u) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, u), notThrowingHandler());
	}

	/**
	 * Converts this {@code BiConsumerWithException} to a <i>staged</i>
	 * {@code BiFunction} that return a {@code CompletionStage}.
	 *
	 * @return the staged operation.
	 * @since 1.1.0
	 */
	@Override
	default BiFunction<T, U, CompletionStage<Void>> stage() {
		return (t, u) -> NoReturnExceptionHandlerSupport.staged(() -> accept(t, u));
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
	 * @return a composed {@code BiConsumerWithException} that performs in sequence
	 *         this operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
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
	 * Returns a {@code BiConsumerWithException} that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception of the operation
	 *
	 * @return an operation that always throw exception
	 */
	static <T, U, E extends Exception> BiConsumerWithException<T, U, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a {@code BiConsumer} that wraps
	 * exception to {@code RuntimeException}.
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * BiConsumerWithException&lt;String, String, IOException&gt; consumerThrowingException = ...;
	 *
	 * BiConsumer&lt;String, String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.unchecked(consumerThrowingException);
	 *
	 * myMap.forEach(consumerThrowingRuntimeException);
	 * </pre>
	 *
	 * In case of exception inside {@code consumerThrowingRuntimeException} an
	 * instance of {@code WrappedException} with the original exception as cause
	 * will be thrown.
	 *
	 * @param consumer
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception of the operation
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiConsumerWithException, Function)
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> unchecked(BiConsumerWithException<T, U, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a {@code BiConsumer} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * BiConsumerWithException&lt;String, String, IOException&gt; consumerThrowingException = ...;
	 *
	 * BiConsumer&lt;String, String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.unchecked(
	 *     consumerThrowingException,
	 *     IllegalArgumentException::new);
	 *
	 * myMap.forEach(consumerThrowingRuntimeException)
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
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception of the operation
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiConsumerWithException)
	 * @throws NullPointerException
	 *             if consumer or exceptionMapper is null
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> unchecked(BiConsumerWithException<T, U, E> consumer,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(consumer, CONSUMER_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new BiConsumerWithException<T, U, E>() {

			@Override
			public void accept(T t, U u) throws E {
				consumer.accept(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a lifted {@code BiConsumer}
	 * ignoring exception.
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * BiConsumerWithException&lt;String, String, IOException&gt; consumerThrowingException = ...;
	 *
	 * BiConsumer&lt;String, String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.lifted(consumerThrowingException);
	 *
	 * myMap.forEach(consumerThrowingRuntimeException);
	 * </pre>
	 *
	 * In case of exception inside {@code consumerThrowingRuntimeException} the
	 * exception will be ignored.
	 *
	 * @param consumer
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception of the operation
	 * @return the lifted operation
	 * @see #lift()
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> lifted(BiConsumerWithException<T, U, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a lifted {@code BiConsumer}
	 * ignoring exception.
	 * <p>
	 * For example :
	 *
	 * <pre>
	 * BiConsumerWithException&lt;String, String, IOException&gt; consumerThrowingException = ...;
	 *
	 * BiConsumer&lt;String, String&gt; consumerThrowingRuntimeException =
	 *   ConsumerWithException.ignored(consumerThrowingException);
	 *
	 * myMap.forEach(consumerThrowingRuntimeException);
	 * </pre>
	 *
	 * In case of exception inside {@code consumerThrowingRuntimeException} the
	 * exception will be ignored.
	 *
	 * @param consumer
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception of the operation
	 * @return the lifted operation
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, U, E extends Exception> BiConsumer<T, U> ignored(BiConsumerWithException<T, U, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).ignore();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a staged {@code BiFunction}.
	 *
	 * @param consumer
	 *            to be staged
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <E>
	 *            the type of the potential exception
	 * @return the staged operation
	 * @throws NullPointerException
	 *             if consumer is null
	 * @since 1.1.0
	 */
	static <T, U, E extends Exception> BiFunction<T, U, CompletionStage<Void>> staged(
			BiConsumerWithException<T, U, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).stage();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a
	 * {@code BiFunctionWithException} returning {@code null}.
	 *
	 * @param consumer
	 *            to be converter
	 * @param <T>
	 *            the type of the first argument to the operation
	 * @param <U>
	 *            the type of the second argument to the operation
	 * @param <R>
	 *            the type of the return value of the function
	 * @param <E>
	 *            the type of the potential exception of the operation
	 * @return the function
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	static <T, U, R, E extends Exception> BiFunctionWithException<T, U, R, E> asBiFunction(
			BiConsumerWithException<T, U, E> consumer) {
		return requireNonNull(consumer, CONSUMER_CANT_BE_NULL).asBiFunction();
	}

	/**
	 * Converts a {@code BiConsumerWithException} to a
	 * {@code BiFunctionWithException} returning {@code null}.
	 *
	 * @param <R>
	 *            the type of the return value of the function
	 * @return the function
	 * @since 1.2.0
	 */
	default <R> BiFunctionWithException<T, U, R, E> asBiFunction() {
		return (t, u) -> {
			accept(t, u);
			return null;
		};
	}

}
