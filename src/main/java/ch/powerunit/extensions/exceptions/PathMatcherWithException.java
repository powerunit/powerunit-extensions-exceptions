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

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An interface that is implemented by objects that perform match operations on
 * paths and may throw an exception.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #matches(Path) boolean matches(Path path) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code PathMatcher}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code PathMatcher}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code PathMatcher}</li>
 * </ul>
 *
 * @see PathMatcher
 * @param <E>
 *            the type of the potential exception of the function
 * @since 1.1.0
 */
@FunctionalInterface
public interface PathMatcherWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<PathMatcher>, BooleanDefaultValue {

	/**
	 * Tells if given path matches this matcher's pattern.
	 *
	 * @param path
	 *            the path to match
	 *
	 * @return {@code true} if, and only if, the path matches this matcher's pattern
	 * @throws E
	 *             any exception
	 * @see PathMatcher#matches(Path)
	 */
	boolean matches(Path path) throws E;

	@Override
	default PathMatcher uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return matches(value);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Returns a composed PathMatcher that represents a short-circuiting logical AND
	 * of this PathMatcher and another. When evaluating the composed predicate, if
	 * this predicate is {@code false}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either PathMatcher are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} PathMatcher will not be evaluated.
	 *
	 * @param other
	 *            a PathMatcher that will be logically-ANDed with this predicate
	 * @return a composed PathMatcher that represents the short-circuiting logical
	 *         AND of this PathMatcher and the {@code other} PathMatcher
	 * @throws NullPointerException
	 *             if other is null
	 * @see #or(PathMatcherWithException)
	 * @see #negate()
	 */
	default PathMatcherWithException<E> and(PathMatcherWithException<? extends E> other) {
		requireNonNull(other);
		return t -> matches(t) && other.matches(t);
	}

	/**
	 * Returns a PathMatcher that represents the logical negation of this
	 * PathMatcher.
	 *
	 * @return a PathMatcher that represents the logical negation of this
	 *         PathMatcher
	 * @see #and(PathMatcherWithException)
	 * @see #or(PathMatcherWithException)
	 */
	default PathMatcherWithException<E> negate() {
		return t -> !matches(t);
	}

	/**
	 * Negate a {@code PredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated PathMatcher
	 * @see #negate()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> PathMatcherWithException<E> negate(PathMatcherWithException<E> predicate) {
		return verifyPredicate(predicate).negate();
	}

	/**
	 * Returns a composed PathMatcher that represents a short-circuiting logical OR
	 * of this PathMatcher and another. When evaluating the composed PathMatcher, if
	 * this PathMatcher is {@code true}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either PathMatcher are relayed to
	 * the caller; if evaluation of this PathMatcher throws an exception, the
	 * {@code other} PathMatcher will not be evaluated.
	 *
	 * @param other
	 *            a PathMatcher that will be logically-ORed with this predicate
	 * @return a composed PathMatcher that represents the short-circuiting logical
	 *         OR of this predicate and the {@code other} PathMatcher
	 * @throws NullPointerException
	 *             if other is null
	 * @see #and(PathMatcherWithException)
	 * @see #negate()
	 */
	default PathMatcherWithException<E> or(PathMatcherWithException<? extends E> other) {
		requireNonNull(other);
		return t -> matches(t) || other.matches(t);
	}

	/**
	 * Returns a PathMatcher that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return a PathMatcher that always throw exception
	 */
	static <E extends Exception> PathMatcherWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code PathMatcherWithException} to a {@code PathMatcher} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked PathMatcher
	 * @see #uncheck()
	 * @see #unchecked(PathMatcherWithException, Function)
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> PathMatcher unchecked(PathMatcherWithException<E> predicate) {
		return verifyPredicate(predicate).uncheck();
	}

	/**
	 * Converts a {@code PathMatcherWithException} to a {@code PathMatcher} that
	 * wraps exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked PathMatcher
	 * @see #uncheck()
	 * @see #unchecked(PathMatcherWithException)
	 * @throws NullPointerException
	 *             if predicate or exceptionMapper is null
	 */
	static <E extends Exception> PathMatcher unchecked(PathMatcherWithException<E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyPredicate(predicate);
		verifyExceptionMapper(exceptionMapper);
		return new PathMatcherWithException<E>() {

			@Override
			public boolean matches(Path t) throws E {
				return predicate.matches(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code PathMatcherWithException} to a lifted {@code PathMatcher}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted PathMatcher
	 * @see #lift()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> PathMatcher lifted(PathMatcherWithException<E> predicate) {
		return verifyPredicate(predicate).lift();
	}

	/**
	 * Converts a {@code PathMatcherWithException} to a lifted {@code PathMatcher}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted PathMatcher
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> PathMatcher ignored(PathMatcherWithException<E> predicate) {
		return verifyPredicate(predicate).ignore();
	}

}
