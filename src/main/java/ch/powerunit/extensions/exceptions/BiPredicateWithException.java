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

import java.util.function.BiPredicate;
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
 *            the type of the first input to the predicate
 * @param <U>
 *            the type of the second argument the predicate
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface BiPredicateWithException<T, U, E extends Exception>
		extends ExceptionHandlerSupport<BiPredicate<T, U>, BiPredicate<T, U>, E> {

	/**
	 * Evaluates this predicate on the given arguments.
	 *
	 * @param t
	 *            the first input argument
	 * @param u
	 *            the first second argument
	 * @return {@code true} if the input argument matches the predicate, otherwise
	 *         {@code false}
	 * @throws E
	 *             any exception
	 * @see Predicate#test(Object)
	 */
	boolean test(T t, U u) throws E;

	/**
	 * Converts this {@code BiPredicateWithException} to a {@code BiPredicate} that
	 * convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked predicate
	 * @see #unchecked(BiPredicateWithException)
	 * @see #unchecked(BiPredicateWithException, Function)
	 */
	@Override
	default BiPredicate<T, U> uncheck() {
		return (t, u) -> {
			try {
				return test(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code BiPredicateWithException} to a lifted
	 * {@code BiPredicate} returning {@code null} in case of exception.
	 *
	 * @return the predicate that ignore error (return false in this case)
	 * @see #lifted(BiPredicateWithException)
	 */
	@Override
	default BiPredicate<T, U> lift() {
		return (t, u) -> {
			try {
				return test(t, u);
			} catch (Exception e) {
				return false;
			}
		};
	}

	/**
	 * Converts this {@code BiPredicateWithException} to a lifted
	 * {@code BiPredicate} returning {@code null} in case of exception.
	 *
	 * @return the predicate that ignore error (return false in this case)
	 * @see #ignored(BiPredicateWithException)
	 */
	@Override
	default BiPredicate<T, U> ignore() {
		return (t, u) -> {
			try {
				return test(t, u);
			} catch (Exception e) {
				return false;
			}
		};
	}

	/**
	 * Transforms this {@code BiPredicateWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @return the operation
	 * @see #biConsumer(BiPredicateWithException)
	 */
	default BiConsumerWithException<T, U, E> asBiConsumer() {
		return this::test;
	}

	/**
	 * Transforms this {@code BiPredicateWithException} to a
	 * {@code BiFunctionWithException}.
	 *
	 * @return the function
	 * @see #biFunction(BiPredicateWithException)
	 */
	default BiFunctionWithException<T, U, Boolean, E> asBiFunction() {
		return this::test;
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
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
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
	 * convert exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiPredicateWithException, Function)
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> unchecked(BiPredicateWithException<T, U, E> predicate) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		return predicate.uncheck();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a {@code BiPredicate} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiPredicateWithException)
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> unchecked(BiPredicateWithException<T, U, E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(predicate, "redicate can't be null");
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
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
	 * returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> lifted(BiPredicateWithException<T, U, E> predicate) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		return predicate.lift();
	}

	/**
	 * Converts a {@code BiPredicateWithException} to a lifted {@code BiPredicate}
	 * returning {@code null} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, U, E extends Exception> BiPredicate<T, U> ignored(BiPredicateWithException<T, U, E> predicate) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		return predicate.ignore();
	}

	/**
	 * Transforms this {@code BiPredicateWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asBiConsumer()
	 */
	static <T, U, E extends Exception> BiConsumerWithException<T, U, E> biConsumer(
			BiPredicateWithException<T, U, E> predicate) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		return predicate.asBiConsumer();
	}

	/**
	 * Transforms this {@code BiPredicateWithException} to a
	 * {@code BiFunctionWithException}.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asBiFunction()
	 */
	static <T, U, E extends Exception> BiFunctionWithException<T, U, Boolean, E> biFunction(
			BiPredicateWithException<T, U, E> predicate) {
		requireNonNull(predicate, PREDICATE_CANT_BE_NULL);
		return predicate.asBiFunction();
	}

}
