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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a predicate (boolean-valued function) of one argument and may
 * throw an exception.
 *
 * @author borettim
 * @see Predicate
 * @param <T>
 *            the type of the input to the predicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface PredicateWithException<T, E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<Predicate<T>> {

	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param t
	 *            the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see Predicate#test(Object)
	 */
	boolean test(T t) throws E;

	@Override
	default Predicate<T> uncheckOrIgnore(boolean uncheck) {
		return value -> {
			try {
				return test(value);
			} catch (Exception e) {
				if (uncheck) {
					throw exceptionMapper().apply(e);
				}
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
	 * @see #or(PredicateWithException)
	 * @see #negate()
	 */
	default PredicateWithException<T, E> and(PredicateWithException<? super T, ? extends E> other) {
		requireNonNull(other);
		return t -> test(t) && other.test(t);
	}

	/**
	 * Returns a predicate that represents the logical negation of this predicate.
	 *
	 * @return a predicate that represents the logical negation of this predicate
	 * @see #and(PredicateWithException)
	 * @see #or(PredicateWithException)
	 */
	default PredicateWithException<T, E> negate() {
		return t -> !test(t);
	}

	/**
	 * Negate a {@code PredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated predicate
	 * @see #negate()
	 */
	static <T, E extends Exception> PredicateWithException<T, E> negate(PredicateWithException<T, E> predicate) {
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
	 * @see #and(PredicateWithException)
	 * @see #negate()
	 */
	default PredicateWithException<T, E> or(PredicateWithException<? super T, ? extends E> other) {
		requireNonNull(other);
		return t -> test(t) || other.test(t);
	}

	/**
	 * Returns a predicate that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <T, E extends Exception> PredicateWithException<T, E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code PredicateWithException} to a {@code Predicate} that convert
	 * exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(PredicateWithException, Function)
	 */
	static <T, E extends Exception> Predicate<T> unchecked(PredicateWithException<T, E> predicate) {
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
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(PredicateWithException)
	 */
	static <T, E extends Exception> Predicate<T> unchecked(PredicateWithException<T, E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
		return new PredicateWithException<T, E>() {

			@Override
			public boolean test(T t) throws E {
				return predicate.test(t);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code PredicateWithException} to a lifted {@code Predicate}
	 * returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <T, E extends Exception> Predicate<T> lifted(PredicateWithException<T, E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code PredicateWithException} to a lifted {@code Predicate}
	 * returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, E extends Exception> Predicate<T> ignored(PredicateWithException<T, E> predicate) {
		return requireNonNull(predicate, PREDICATE_CANT_BE_NULL).ignore();
	}

}
