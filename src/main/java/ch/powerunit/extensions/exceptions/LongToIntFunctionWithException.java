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
import java.util.function.LongToIntFunction;
import java.util.function.Supplier;

/**
 * Represents a predicate (boolean-valued function) of one argument and may
 * throw an exception.
 *
 * @author borettim
 * @see LongToIntFunction
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface LongToIntFunctionWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<LongToIntFunction> {

	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param t
	 *            the input argument
	 * @return the result
	 * @throws E
	 *             any exception
	 * @see LongToIntFunction#applyAsInt(long)
	 */
	int applyAsInt(long t) throws E;

	/**
	 * Converts this {@code DoubleToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked predicate
	 * @see #unchecked(LongToIntFunctionWithException)
	 * @see #unchecked(LongToIntFunctionWithException, Function)
	 */
	@Override
	default LongToIntFunction uncheck() {
		return t -> {
			try {
				return applyAsInt(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code DoubleToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code null} in case of exception.
	 *
	 * @return the predicate that ignore error (return false in this case)
	 * @see #ignored(LongToIntFunctionWithException)
	 */
	@Override
	default LongToIntFunction ignore() {
		return t -> {
			try {
				return applyAsInt(t);
			} catch (Exception e) {
				return 0;
			}
		};
	}

	/**
	 * Returns a predicate that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <E extends Exception> LongToIntFunctionWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoubleToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(LongToIntFunctionWithException, Function)
	 */
	static <E extends Exception> LongToIntFunction unchecked(LongToIntFunctionWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code DoubleToIntFunctionWithException} to a
	 * {@code LongToIntFunction} that convert exception to {@code RuntimeException}
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
	 * @see #unchecked(LongToIntFunctionWithException)
	 */
	static <E extends Exception> LongToIntFunction unchecked(LongToIntFunctionWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new LongToIntFunctionWithException<E>() {

			@Override
			public int applyAsInt(long t) throws E {
				return function.applyAsInt(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoubleToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> LongToIntFunction lifted(LongToIntFunctionWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code DoubleToIntFunctionWithException} to a lifted
	 * {@code LongToIntFunction} returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> LongToIntFunction ignored(LongToIntFunctionWithException<E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

}
