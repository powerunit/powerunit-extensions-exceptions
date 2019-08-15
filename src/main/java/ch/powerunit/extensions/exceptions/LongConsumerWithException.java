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
import java.util.function.LongConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code Consumer} is expected to operate via side-effects.
 *
 * @author borettim
 * @see LongConsumer
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface LongConsumerWithException<E extends Exception> extends NoReturnExceptionHandlerSupport<LongConsumer> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t
	 *            the input argument
	 * @throws E
	 *             any exception
	 * @see LongConsumer#accept(long)
	 */
	void accept(long t) throws E;

	/**
	 * Converts this {@code LongConsumerWithException} to a {@code LongConsumer}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(LongConsumerWithException)
	 * @see #unchecked(LongConsumerWithException, Function)
	 */
	@Override
	default LongConsumer uncheck() {
		return t -> {
			try {
				accept(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};
	}

	/**
	 * Converts this {@code LongConsumerWithException} to a <i>lifted</i>
	 * {@code LongConsumer} ignoring exception.
	 *
	 * @return the operation that ignore error
	 * @see #ignored(LongConsumerWithException)
	 */
	@Override
	default LongConsumer ignore() {
		return t -> {
			try {
				accept(t);
			} catch (Exception e) {
				// ignore
			}
		};
	}

	/**
	 * Transforms this {@code LongConsumerWithException} to a
	 * {@code FunctionWithException} that returns nothing.
	 *
	 * @return the function
	 * @see #function(LongConsumerWithException)
	 */
	default FunctionWithException<Long, Void, E> asFunction() {
		return t -> {
			accept(t);
			return null;
		};
	}

	/**
	 * Returns a composed {@code LongConsumerWithException} that performs, in
	 * sequence, this operation followed by the {@code after} operation. If
	 * performing either operation throws an exception, it is relayed to the caller
	 * of the composed operation. If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code LongConsumer} that performs in sequence this
	 *         operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 *
	 *
	 * @see LongConsumer#andThen(LongConsumer)
	 */
	default LongConsumerWithException<E> andThen(LongConsumerWithException<? extends E> after) {
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
	static <E extends Exception> LongConsumerWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code LongConsumerWithException} to a {@code LongConsumer} that
	 * convert exception to {@code RuntimeException}.
	 *
	 * @param operation
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongConsumerWithException, Function)
	 */
	static <E extends Exception> LongConsumer unchecked(LongConsumerWithException<E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.uncheck();
	}

	/**
	 * Converts a {@code LongConsumerWithException} to a {@code LongConsumer} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param operation
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongConsumerWithException)
	 */
	static <E extends Exception> LongConsumer unchecked(LongConsumerWithException<E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new LongConsumerWithException<E>() {

			@Override
			public void accept(long t) throws E {
				operation.accept(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code LongConsumerWithException} to a lifted {@code LongConsumer}
	 * returning {@code null} in case of exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #lift()
	 */
	static <E extends Exception> LongConsumer lifted(LongConsumerWithException<E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.lift();
	}

	/**
	 * Converts a {@code LongConsumerWithException} to a lifted {@code LongConsumer}
	 * returning {@code null} in case of exception.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted operation
	 * @see #ignore()
	 */
	static <E extends Exception> LongConsumer ignored(LongConsumerWithException<E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.ignore();
	}

	/**
	 * Transforms a {@code ConsumerWithException} to a {@code FunctionWithException}
	 * that returns nothing.
	 *
	 * @param operation
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the function
	 * @see #asFunction()
	 */
	static <E extends Exception> FunctionWithException<Long, Void, E> function(LongConsumerWithException<E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.asFunction();
	}

}
