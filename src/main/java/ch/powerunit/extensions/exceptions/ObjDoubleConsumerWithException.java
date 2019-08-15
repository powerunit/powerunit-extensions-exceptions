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
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result and may throw exception. Unlike most other functional interfaces,
 * {@code Consumer} is expected to operate via side-effects.
 *
 * @author borettim
 * @see ObjDoubleConsumer
 * @param <T>
 *            the type of the input to the operation
 * @param <E>
 *            the type of the potential exception of the operation
 */
@FunctionalInterface
public interface ObjDoubleConsumerWithException<T, E extends Exception>
		extends NoReturnExceptionHandlerSupport<ObjDoubleConsumer<T>> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the second input argument
	 * @throws E
	 *             any exception
	 * @see ObjDoubleConsumer#accept(Object,double)
	 */
	void accept(T t, double u) throws E;

	/**
	 * Converts this {@code ObjLongConsumerWithException} to a
	 * {@code ObjDoubleConsumer} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked operation
	 * @see #unchecked(ObjDoubleConsumerWithException)
	 * @see #unchecked(ObjDoubleConsumerWithException, Function)
	 */
	@Override
	default ObjDoubleConsumer<T> uncheck() {
		return (t, u) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, u), e -> {
			throw exceptionMapper().apply(e);
		});
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
		return (t, u) -> NoReturnExceptionHandlerSupport.unchecked(() -> accept(t, u), e -> {
		});
	}

	/**
	 * Transforms this {@code ObjLongConsumerWithException} to a
	 * {@code BiFunctionWithException} that returns nothing.
	 *
	 * @return the function
	 * @see #biFunction(ObjDoubleConsumerWithException)
	 */
	default BiFunctionWithException<T, Double, Void, E> asBiFunction() {
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
	static <T, E extends Exception> ObjDoubleConsumerWithException<T, E> failing(Supplier<E> exceptionBuilder) {
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
	 * @see #unchecked(ObjDoubleConsumerWithException, Function)
	 */
	static <T, E extends Exception> ObjDoubleConsumer<T> unchecked(ObjDoubleConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.uncheck();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a
	 * {@code ObjDoubleConsumer} that convert exception to {@code RuntimeException}
	 * by using the provided mapping function.
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
	 * @see #unchecked(ObjDoubleConsumerWithException)
	 */
	static <T, E extends Exception> ObjDoubleConsumer<T> unchecked(ObjDoubleConsumerWithException<T, E> operation,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(operation, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new ObjDoubleConsumerWithException<T, E>() {

			@Override
			public void accept(T t, double u) throws E {
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
	 * {@code ObjDoubleConsumer} returning {@code null} in case of exception.
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
	static <T, E extends Exception> ObjDoubleConsumer<T> lifted(ObjDoubleConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.lift();
	}

	/**
	 * Converts a {@code ObjLongConsumerWithException} to a lifted
	 * {@code ObjDoubleConsumer} returning {@code null} in case of exception.
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
	static <T, E extends Exception> ObjDoubleConsumer<T> ignored(ObjDoubleConsumerWithException<T, E> operation) {
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
	static <T, E extends Exception> BiFunctionWithException<T, Double, Void, E> biFunction(
			ObjDoubleConsumerWithException<T, E> operation) {
		requireNonNull(operation, OPERATION_CANT_BE_NULL);
		return operation.asBiFunction();
	}

}
