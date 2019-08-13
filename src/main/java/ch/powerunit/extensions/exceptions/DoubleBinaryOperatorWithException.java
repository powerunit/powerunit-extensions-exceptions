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

import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a double binary operator with exception.
 *
 * @author borettim
 * @see DoubleBinaryOperator
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface DoubleBinaryOperatorWithException<E extends Exception>
		extends ExceptionHandlerSupport<DoubleBinaryOperator, DoubleBinaryOperator> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @param u
	 *            the second function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see DoubleBinaryOperator#applyAsDouble(double, double)
	 */
	double applyAsDouble(double t, double u) throws E;

	/**
	 * Converts this {@code DoubleBinaryOperatorWithException} to a
	 * {@code DoubleBinaryOperator} that convert exception to
	 * {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(DoubleBinaryOperatorWithException)
	 * @see #unchecked(DoubleBinaryOperatorWithException, Function)
	 */
	@Override
	default DoubleBinaryOperator uncheck() {
		return (t, u) -> {
			try {
				return applyAsDouble(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code DoubleBinaryOperatorWithException} to a lifted
	 * {@code DoubleBinaryOperator} return a zero by default.
	 *
	 * @return the lifted function
	 * @see #lifted(DoubleBinaryOperatorWithException)
	 */
	@Override
	default DoubleBinaryOperator lift() {
		return ignore();
	}

	/**
	 * Converts this {@code DoubleBinaryOperatorWithException} to a lifted
	 * {@code DoubleBinaryOperator} returning uero in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(DoubleBinaryOperatorWithException)
	 */
	@Override
	default DoubleBinaryOperator ignore() {
		return (t, u) -> {
			try {
				return applyAsDouble(t, u);
			} catch (Exception e) {
				return 0d;
			}
		};
	}

	/**
	 * Transforms this {@code DoubleBinaryOperatorWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @return the operation
	 * @see #biConsumer(DoubleBinaryOperatorWithException)
	 */
	default BiConsumerWithException<Double, Double, Exception> asBiConsumer() {
		return this::applyAsDouble;
	}

	/**
	 * Transforms this {@code DoubleBinaryOperatorWithException} to a
	 * {@code SupplierWithException}.
	 *
	 * @param t
	 *            the first input for the generated supplier.
	 * @param u
	 *            the second input for the generated suppoler.
	 * @return the supplier
	 */
	default SupplierWithException<Double, Exception> asSupplier(double t, double u) {
		return () -> applyAsDouble(t, u);
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
	static <E extends Exception> DoubleBinaryOperatorWithException<E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleBinaryOperatorException} to a
	 * {@code DoubleBinaryOperator} that convert exception to
	 * {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(DoubleBinaryOperatorWithException, Function)
	 */
	static <E extends Exception> DoubleBinaryOperator unchecked(DoubleBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
	}

	/**
	 * Converts a {@code DoubleBinaryOperatorWithException} to a
	 * {@code DoubleBinaryOperator} that convert exception to
	 * {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(DoubleBinaryOperatorWithException)
	 */
	static <E extends Exception> DoubleBinaryOperator unchecked(DoubleBinaryOperatorWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new DoubleBinaryOperatorWithException<E>() {

			@Override
			public double applyAsDouble(double t, double u) throws E {
				return function.applyAsDouble(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleBinaryOperatorWithException} to a lifted
	 * {@code DoubleBinaryOperator} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> DoubleBinaryOperator lifted(DoubleBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.lift();
	}

	/**
	 * Converts a {@code DoubleBinaryOperatorWithException} to a lifted
	 * {@code DoubleBinaryOperator} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> DoubleBinaryOperator ignored(DoubleBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

	/**
	 * Transforms this {@code DoubleBinaryOperatorWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asBiConsumer()
	 */
	static <E extends Exception> BiConsumerWithException<Double, Double, Exception> biConsumer(
			DoubleBinaryOperatorWithException<E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.asBiConsumer();
	}

}
