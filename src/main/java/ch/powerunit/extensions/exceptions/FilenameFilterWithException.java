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

import java.io.File;
import java.io.FilenameFilter;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Instances of classes that implement this interface are used to filter
 * filenames and may throw exception.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(File, String) boolean accept(File dir, String name)
 * throws E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code FilenameFilter}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code FilenameFilter}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code FilenameFilter}</li>
 * </ul>
 *
 *
 * @see FilenameFilter
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface FilenameFilterWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<FilenameFilter> {

	/**
	 * Tests if a specified file should be included in a file list.
	 *
	 * @param dir
	 *            the directory in which the file was found.
	 * @param name
	 *            the name of the file.
	 * @return <code>true</code> if and only if the name should be included in the
	 *         file list; <code>false</code> otherwise.
	 * @throws E
	 *             any exception
	 * @see FilenameFilter#accept(File, String)
	 */
	boolean accept(File dir, String name) throws E;

	@Override
	default FilenameFilter uncheckOrIgnore(boolean uncheck) {
		return (dir, name) -> {
			try {
				return accept(dir, name);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return false;
			}
		};
	}

	/**
	 * Returns a composed FilenameFilter that represents a short-circuiting logical
	 * AND of this FilenameFilter and another. When evaluating the composed
	 * FilenameFilter, if this FilenameFilter is {@code false}, then the
	 * {@code other} FilenameFilter is not evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either FilenameFilter are relayed
	 * to the caller; if evaluation of this FilenameFilter throws an exception, the
	 * {@code other} FilenameFilter will not be evaluated.
	 *
	 * @param other
	 *            a FilenameFilter that will be logically-ANDed with this predicate
	 * @return a composed FilenameFilter that represents the short-circuiting
	 *         logical AND of this FilenameFilter and the {@code other}
	 *         FilenameFilter
	 * @throws NullPointerException
	 *             if other is null
	 * @see #or(FilenameFilterWithException)
	 * @see #negate()
	 */
	default FilenameFilterWithException<E> and(FilenameFilterWithException<? extends E> other) {
		requireNonNull(other);
		return (t, u) -> accept(t, u) && other.accept(t, u);
	}

	/**
	 * Returns a FilenameFilter that represents the logical negation of this
	 * FilenameFilter.
	 *
	 * @return a FilenameFilter that represents the logical negation of this
	 *         FilenameFilter
	 * @see #and(FilenameFilterWithException)
	 * @see #or(FilenameFilterWithException)
	 */
	default FilenameFilterWithException<E> negate() {
		return (t, u) -> !accept(t, u);
	}

	/**
	 * Negate a {@code DoublePredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated FilenameFilter
	 * @see #negate()
	 */
	static <E extends Exception> FilenameFilterWithException<E> negate(FilenameFilterWithException<E> predicate) {
		return verifyPredicate(predicate).negate();
	}

	/**
	 * Returns a composed FilenameFilter that represents a short-circuiting logical
	 * OR of this FilenameFilter and another. When evaluating the composed
	 * FilenameFilter, if this FilenameFilter is {@code true}, then the
	 * {@code other} FilenameFilter is not evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either FilenameFilter are relayed
	 * to the caller; if evaluation of this FilenameFilter throws an exception, the
	 * {@code other} FilenameFilter will not be evaluated.
	 *
	 * @param other
	 *            a FilenameFilter that will be logically-ORed with this
	 *            FilenameFilter
	 * @return a composed FilenameFilter that represents the short-circuiting
	 *         logical OR of this FilenameFilter and the {@code other}
	 *         FilenameFilter
	 * @throws NullPointerException
	 *             if other is null
	 * @see #and(FilenameFilterWithException)
	 * @see #negate()
	 */
	default FilenameFilterWithException<E> or(FilenameFilterWithException<? extends E> other) {
		requireNonNull(other);
		return (t, u) -> accept(t, u) || other.accept(t, u);
	}

	/**
	 * Returns a FilenameFilter that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return a predicate that always throw exception
	 */
	static <E extends Exception> FilenameFilterWithException<E> failing(Supplier<E> exceptionBuilder) {
		return (dir, name) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code FilenameFilterWithException} to a {@code FilenameFilter}
	 * that wraps to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked FilenameFilter
	 * @see #uncheck()
	 * @see #unchecked(FilenameFilterWithException, Function)
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FilenameFilter unchecked(FilenameFilterWithException<E> predicate) {
		return verifyPredicate(predicate).uncheck();
	}

	/**
	 * Converts a {@code FilenameFilterWithException} to a {@code FilenameFilter}
	 * that wraps to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked FilenameFilter
	 * @see #uncheck()
	 * @see #unchecked(FilenameFilterWithException)
	 * @throws NullPointerException
	 *             if predicate or exceptionMapper is null
	 */
	static <E extends Exception> FilenameFilter unchecked(FilenameFilterWithException<E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyPredicate(predicate);
		verifyExceptionMapper(exceptionMapper);
		return new FilenameFilterWithException<E>() {

			@Override
			public boolean accept(File dir, String name) throws E {
				return predicate.accept(dir, name);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code FilenameFilterWithException} to a lifted
	 * {@code FilenameFilter} returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted FilenameFilter
	 * @see #lift()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FilenameFilter lifted(FilenameFilterWithException<E> predicate) {
		return verifyPredicate(predicate).lift();
	}

	/**
	 * Converts a {@code FilenameFilterWithException} to a lifted
	 * {@code FilenameFilter} returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted FilenameFilter
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FilenameFilter ignored(FilenameFilterWithException<E> predicate) {
		return verifyPredicate(predicate).ignore();
	}

}
