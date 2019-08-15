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

import static ch.powerunit.extensions.exceptions.Constants.PREDICATE_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a predicate (boolean-valued function) of one argument and may
 * throw an exception.
 *
 * @author borettim
 * @see DoublePredicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface DoublePredicateWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<DoublePredicate> {

	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param t
	 *            the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see DoublePredicate#test(double)
	 */
	boolean test(double t) throws E;

	/**
	 * Converts this {@code DoublePredicateWithException} to a
	 * {@code DoublePredicate} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked predicate
	 * @see #unchecked(DoublePredicateWithException)
	 * @see #unchecked(DoublePredicateWithException, Function)
	 */
	@Override
	default DoublePredicate uncheck() {
		return t -> {
			try {
				return test(t);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code DoublePredicateWithException} to a lifted
	 * {@code DoublePredicate} returning {@code false} in case of exception.
	 *
	 * @return the predicate that ignore error (return false in this case)
	 * @see #ignored(DoublePredicateWithException)
	 */
	@Override
	default DoublePredicate ignore() {
		return t -> {
			try {
				return test(t);
			} catch (Exception e) {
				return false;
			}
		};
	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical AND
	 * of this predicate and another. When evaluating the composed predicate, if
	 * this predicate is {@code false}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other
	 *            a predicate that will be logically-ANDed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical AND
	 *         of this predicate and the {@code other} predicate
	 * @throws NullPointerException
	 *             if other is null
	 * @see #or(DoublePredicateWithException)
	 * @see #negate()
	 */
	default DoublePredicateWithException<E> and(DoublePredicateWithException<? extends E> other) {
		requireNonNull(other);
		return t -> test(t) && other.test(t);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 * @see #and(DoublePredicateWithException)
	 * @see #or(DoublePredicateWithException)
	 */
	default DoublePredicateWithException<E> negate() {
		return t -> !test(t);
	}

	/**
	 * Negate a {@code DoublePredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated predicate
	 * @see #negate()
	 */
	static <E extends Exception> DoublePredicateWithException<E> negate(DoublePredicateWithException<E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).negate();
	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical OR of
	 * this predicate and another. When evaluating the composed predicate, if this
	 * predicate is {@code true}, then the {@code other} predicate is not evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either predicate are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other
	 *            a predicate that will be logically-ORed with this predicate
	 * @return a composed predicate that represents the short-circuiting logical OR
	 *         of this predicate and the {@code other} predicate
	 * @throws NullPointerException
	 *             if other is null
	 * @see #and(DoublePredicateWithException)
	 * @see #negate()
	 */
	default DoublePredicateWithException<E> or(DoublePredicateWithException<? extends E> other) {
		requireNonNull(other);
		return t -> test(t) || other.test(t);
	}

	/**
	 * Returns a predicate that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception the type of the input object
	 *            to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <E extends Exception> DoublePredicateWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code DoublePredicateWithException} to a {@code DoublePredicate}
	 * that convert exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(DoublePredicateWithException, Function)
	 */
	static <E extends Exception> DoublePredicate unchecked(DoublePredicateWithException<E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code PredicateWithException} to a {@code Predicate} that convert
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(DoublePredicateWithException)
	 */
	static <E extends Exception> DoublePredicate unchecked(DoublePredicateWithException<E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new DoublePredicateWithException<E>() {

			@Override
			public boolean test(double t) throws E {
				return predicate.test(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code DoublePredicateWithException} to a lifted
	 * {@code DoublePredicate} returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <E extends Exception> DoublePredicate lifted(DoublePredicateWithException<E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code DoublePredicateWithException} to a lifted
	 * {@code DoublePredicate} returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <E extends Exception> DoublePredicate ignored(DoublePredicateWithException<E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).ignore();
	}

}
