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

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a predicate (boolean-valued function) of two arguments that may
 * throw exception. This is the two-arity specialization of
 * {@link PredicateWithException}.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #test(Object, Object) boolean test(T t, U u) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code BiPredicate<T, U>}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code BiPredicate<T, U>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code BiPredicate<T, U>}</li>
 * </ul>
 *
 *
 * @see BiPredicate
 * @param <T>
 *            the type of the first argument to the predicate
 * @param <U>
 *            the type of the second argument the predicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface BiPredicateWithException<T, U, E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<BiPredicate<T, U>>, BooleanDefaultValue {

	/**
	 * Evaluates this predicate on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the second input argument
	 * @return {@code true} if the input arguments match the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see BiPredicate#test(Object, Object)
	 */
	boolean test(T t, U u) throws E;

	@Override
	default BiPredicate<T, U> uncheckOrIgnore(boolean uncheck) {
		return (t, u) -> {
			try {
				return test(t, u);
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
	 * @see #or(BiPredicateWithException)
	 * @see #negate()
	 */
	default BiPredicateWithException<T, U, E> and(BiPredicateWithException<? super T, ? super U, ? extends E> other) {
		requireNonNull(other);
		return (t, u) -> test(t, u) && other.test(t, u);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 * @see #and(BiPredicateWithException)
	 * @see #or(BiPredicateWithException)
	 */
	default BiPredicateWithException<T, U, E> negate() {
		return (t, u) -> !test(t, u);
	}

	/**
	 * Negate a {@code DoublePredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated predicate
	 * @see #negate()
	 */
	static <T, U, E extends Exception> BiPredicateWithException<T, U, E> negate(
			BiPredicateWithException<T, U, E> predicate) {
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
	 * @see #and(BiPredicateWithException)
	 * @see #negate()
	 */
	default BiPredicateWithException<T, U, E> or(BiPredicateWithException<? super T, ? super U, ? extends E> other) {
		requireNonNull(other);
		return (t, u) -> test(t, u) || other.test(t, u);
	}

	/**
	 * Returns a predicate that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <T, U, E extends Exception> BiPredicateWithException<T, U, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a {@code BiPredicate} that
	 * wraps to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked predicate
	 * @see #uncheck()
	 * @see #unchecked(BiPredicateWithException, Function)
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> unchecked(BiPredicateWithException<T, U, E> predicate) {
		return verifyPredicate(predicate).uncheck();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a {@code BiPredicate} that
	 * wraps to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked predicate
	 * @see #uncheck()
	 * @see #unchecked(BiPredicateWithException)
	 * @throws NullPointerException
	 *             if predicate or exceptionMapper is null
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> unchecked(BiPredicateWithException<T, U, E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyPredicate(predicate);
		verifyExceptionMapper(exceptionMapper);
		return new BiPredicateWithException<T, U, E>() {

			@Override
			public boolean test(T t, U u) throws E {
				return predicate.test(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a lifted {@code BiPredicate}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted predicate
	 * @see #lift()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> lifted(BiPredicateWithException<T, U, E> predicate) {
		return verifyPredicate(predicate).lift();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a lifted {@code BiPredicate}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the predicate
	 * @param <U>
	 *            the type of the second argument the predicate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted predicate
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> ignored(BiPredicateWithException<T, U, E> predicate) {
		return verifyPredicate(predicate).ignore();
	}

}
