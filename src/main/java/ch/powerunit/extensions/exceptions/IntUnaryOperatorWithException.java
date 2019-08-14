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
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * Represents a int unary operator with exception.
 *
 * @author borettim
 * @see IntUnaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntUnaryOperatorWithException<E extends Exception>
		extends ExceptionHandlerSupport<IntUnaryOperator, IntUnaryOperator> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see IntUnaryOperator#applyAsInt(int)
	 */
	int applyAsInt(int t) throws E;

	/**
	 * Converts this {@code IntUnaryOperatorWithException} to a
	 * {@code IntUnaryOperator} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(IntUnaryOperatorWithException)
	 * @see #unchecked(IntUnaryOperatorWithException, Function)
	 */
	@Override
	default IntUnaryOperator uncheck() {
		return t -> {
			try {
				return applyAsInt(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} return a zero by default.
	 *
	 * @return the lifted function
	 * @see #lifted(IntUnaryOperatorWithException)
	 */
	@Override
	default IntUnaryOperator lift() {
		return ignore();
	}

	/**
	 * Converts this {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} returning zero in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(IntUnaryOperatorWithException)
	 */
	@Override
	default IntUnaryOperator ignore() {
		return t -> {
			try {
				return applyAsInt(t);
			} catch (Exception e) {
				return 0;
			}
		};
	}

	/**
	 * Transforms this {@code IntUnaryOperatorWithException} to a
	 * {@code ConsumerWithException}.
	 *
	 * @return the operation
	 * @see #consumer(IntUnaryOperatorWithException)
	 */
	default ConsumerWithException<Integer, Exception> asConsumer() {
		return this::applyAsInt;
	}

	/**
	 * Transforms this {@code IntUnaryOperatorWithException} to a
	 * {@code SupplierWithException}.
	 *
	 * @param t
	 *            the first input for the generated supplier.
	 * @return the supplier
	 */
	default SupplierWithException<Integer, Exception> asSupplier(int t) {
		return () -> applyAsInt(t);
	}

	/**
	 * Returns a composed operator that first applies the {@code before} operator to
	 * its input, and then applies this operator to the result. If evaluation of
	 * either operator throws an exception, it is relayed to the caller of the
	 * composed operator.
	 *
	 * @param before
	 *            the operator to apply before this operator is applied
	 * @return a composed operator that first applies the {@code before} operator
	 *         and then applies this operator
	 * @throws NullPointerException
	 *             if before is null
	 *
	 * @see #andThen(IntUnaryOperatorWithException)
	 */
	default IntUnaryOperatorWithException<E> compose(IntUnaryOperatorWithException<? extends E> before) {
		requireNonNull(before);
		return v -> applyAsInt(before.applyAsInt(v));
	}

	/**
	 * Returns a composed operator that first applies this operator to its input,
	 * and then applies the {@code after} operator to the result. If evaluation of
	 * either operator throws an exception, it is relayed to the caller of the
	 * composed operator.
	 *
	 * @param after
	 *            the operator to apply after this operator is applied
	 * @return a composed operator that first applies this operator and then applies
	 *         the {@code after} operator
	 * @throws NullPointerException
	 *             if after is null
	 *
	 * @see #compose(IntUnaryOperatorWithException)
	 */
	default IntUnaryOperatorWithException<E> andThen(IntUnaryOperatorWithException<? extends E> after) {
		requireNonNull(after);
		return t -> after.applyAsInt(applyAsInt(t));
	}

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 *
	 * @param <E>
	 *            the exception that may be thrown
	 */
	static <E extends Exception> IntUnaryOperatorWithException<E> identity() {
		return t -> t;
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <E extends Exception> IntUnaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntUnaryOperatorException} to a {@code IntUnaryOperator}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntUnaryOperatorWithException, Function)
	 */
	static <E extends Exception> IntUnaryOperator unchecked(IntUnaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a
	 * {@code IntUnaryOperator} that convert exception to {@code RuntimeException}
	 * by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(IntUnaryOperatorWithException)
	 */
	static <E extends Exception> IntUnaryOperator unchecked(IntUnaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new IntUnaryOperatorWithException<E>() {

			@Override
			public int applyAsInt(int t) throws E {
				return function.applyAsInt(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> IntUnaryOperator lifted(IntUnaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.lift();
	}

	/**
	 * Converts a {@code IntUnaryOperatorWithException} to a lifted
	 * {@code IntUnaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> IntUnaryOperator ignored(IntUnaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

	/**
	 * Transforms this {@code IntUnaryOperatorWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asConsumer()
	 */
	static <E extends Exception> ConsumerWithException<Integer, Exception> consumer(
			IntUnaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.asConsumer();
	}

}
