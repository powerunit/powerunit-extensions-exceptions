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
import static ch.powerunit.extensions.exceptions.Constants.verifyPredicate;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

/**
 * Represents a predicate (boolean-valued function) of one {@code int}-valued
 * argument that may throw exception. This is the {@code int}-consuming
 * primitive type specialization of {@link PredicateWithException}.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #test(int) boolean test(int value) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code IntPredicate}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code IntPredicate}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code IntPredicate}</li>
 * </ul>
 *
 * @see IntPredicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface IntPredicateWithException<E extends Exception> extends
		PrimitiveReturnExceptionHandlerSupport<IntPredicate, IntPredicateWithException<E>>, BooleanDefaultValue {

	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param value
	 *            the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see IntPredicate#test(int)
	 */
	boolean test(int value) throws E;

	@Override
	default IntPredicate uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return test(value);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
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
	 * @see #or(IntPredicateWithException)
	 * @see #negate()
	 */
	default IntPredicateWithException<E> and(IntPredicateWithException<? extends E> other) {
		requireNonNull(other);
		return t -> test(t) && other.test(t);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 * @see #and(IntPredicateWithException)
	 * @see #or(IntPredicateWithException)
	 */
	default IntPredicateWithException<E> negate() {
		return t -> !test(t);
	}

	/**
	 * Negate a {@code IntPredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated predicate
	 * @see #negate()
	 */
	static <E extends Exception> IntPredicateWithException<E> negate(IntPredicateWithException<E> predicate) {
		return verifyPredicate(predicate).negate();
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
	 * @see #and(IntPredicateWithException)
	 * @see #negate()
	 */
	default IntPredicateWithException<E> or(IntPredicateWithException<? extends E> other) {
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
	static <E extends Exception> IntPredicateWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code IntPredicateWithException} to a {@code IntPredicate} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked predicate
	 * @see #uncheck()
	 * @see #unchecked(IntPredicateWithException, Function)
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> IntPredicate unchecked(IntPredicateWithException<E> predicate) {
		return verifyPredicate(predicate).uncheck();
	}

	/**
	 * Converts a {@code PredicateWithException} to a {@code Predicate} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked predicate
	 * @see #uncheck()
	 * @see #unchecked(IntPredicateWithException)
	 * @throws NullPointerException
	 *             if predicate or exceptionMapper is null
	 */
	static <E extends Exception> IntPredicate unchecked(IntPredicateWithException<E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyPredicate(predicate);
		verifyExceptionMapper(exceptionMapper);
		return new IntPredicateWithException<E>() {

			@Override
			public boolean test(int value) throws E {
				return predicate.test(value);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code IntPredicateWithException} to a lifted {@code IntPredicate}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted predicate
	 * @see #lift()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> IntPredicate lifted(IntPredicateWithException<E> predicate) {
		return verifyPredicate(predicate).lift();
	}

	/**
	 * Converts a {@code IntPredicateWithException} to a lifted {@code IntPredicate}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted predicate
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> IntPredicate ignored(IntPredicateWithException<E> predicate) {
		return verifyPredicate(predicate).ignore();
	}

	/**
	 * Converts a {@code IntPredicateWithException} to a lifted {@code IntPredicate}
	 * returning a default value in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param defaultValue
	 *            value in case of exception
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted predicate
	 * @see #ignore()
	 * @see #ignored(IntPredicateWithException)
	 * @throws NullPointerException
	 *             if predicate is null
	 * @since 3.0.0
	 */
	static <E extends Exception> IntPredicate ignored(IntPredicateWithException<E> predicate, boolean defaultValue) {
		verifyPredicate(predicate);
		return new IntPredicateWithException<E>() {

			@Override
			public boolean test(int value) throws E {
				return predicate.test(value);
			}

			@Override
			public boolean defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

}
