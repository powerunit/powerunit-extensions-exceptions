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
import java.io.FileFilter;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A filter for abstract pathnames and may throw an exception.
 * <h3>General contract</h3>
 * <ul>
 * <li><b>{@link #accept(File) boolean accept(File path) throws
 * E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code FileFilter}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a {@code FileFilter}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code FileFilter}</li>
 * </ul>
 *
 * @see FileFilter
 * @param <E>
 *            the type of the potential exception of the function
 * @since 1.1.0
 */
@FunctionalInterface
public interface FileFilterWithException<E extends Exception>
		extends PrimitiveReturnExceptionHandlerSupport<FileFilter>, BooleanDefaultValue {

	/**
	 * Tests whether or not the specified abstract pathname should be included in a
	 * pathname list.
	 *
	 * @param pathname
	 *            The abstract pathname to be tested
	 * @return <code>true</code> if and only if <code>pathname</code> should be
	 *         included
	 * @throws E
	 *             any exception
	 * @see FileFilter#accept(File)
	 */
	boolean accept(File pathname) throws E;

	@Override
	default FileFilter uncheckOrIgnore(boolean uncheck) {
		return pathname -> {
			try {
				return accept(pathname);
			} catch (Exception e) {
				PrimitiveReturnExceptionHandlerSupport.handleException(uncheck, e, exceptionMapper());
				return defaultValue();
			}
		};
	}

	/**
	 * Returns a composed FileFilter that represents a short-circuiting logical AND
	 * of this FileFilter and another. When evaluating the composed predicate, if
	 * this predicate is {@code false}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either FileFilter are relayed to
	 * the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} FileFilter will not be evaluated.
	 *
	 * @param other
	 *            a FileFilter that will be logically-ANDed with this predicate
	 * @return a composed FileFilter that represents the short-circuiting logical
	 *         AND of this FileFilter and the {@code other} FileFilter
	 * @throws NullPointerException
	 *             if other is null
	 * @see #or(FileFilterWithException)
	 * @see #negate()
	 */
	default FileFilterWithException<E> and(FileFilterWithException<? extends E> other) {
		requireNonNull(other);
		return t -> accept(t) && other.accept(t);
	}

	/**
	 * Returns a FileFilter that represents the logical negation of this FileFilter.
	 *
	 * @return a FileFilter that represents the logical negation of this FileFilter
	 * @see #and(FileFilterWithException)
	 * @see #or(FileFilterWithException)
	 */
	default FileFilterWithException<E> negate() {
		return t -> !accept(t);
	}

	/**
	 * Negate a {@code PredicateWithException}.
	 *
	 * @param predicate
	 *            to be negate
	 * @param <E>
	 *            the type of the potential exception
	 * @return the negated FileFilter
	 * @see #negate()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FileFilterWithException<E> negate(FileFilterWithException<E> predicate) {
		return verifyPredicate(predicate).negate();
	}

	/**
	 * Returns a composed FileFilter that represents a short-circuiting logical OR
	 * of this FileFilter and another. When evaluating the composed FileFilter, if
	 * this FileFilter is {@code true}, then the {@code other} predicate is not
	 * evaluated.
	 *
	 * <p>
	 * Any exceptions thrown during evaluation of either FileFilter are relayed to
	 * the caller; if evaluation of this FileFilter throws an exception, the
	 * {@code other} FileFilter will not be evaluated.
	 *
	 * @param other
	 *            a FileFilter that will be logically-ORed with this predicate
	 * @return a composed FileFilter that represents the short-circuiting logical OR
	 *         of this predicate and the {@code other} FileFilter
	 * @throws NullPointerException
	 *             if other is null
	 * @see #and(FileFilterWithException)
	 * @see #negate()
	 */
	default FileFilterWithException<E> or(FileFilterWithException<? extends E> other) {
		requireNonNull(other);
		return t -> accept(t) || other.accept(t);
	}

	/**
	 * Returns a FileFilter that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <E>
	 *            the type of the exception
	 * @return a FileFilter that always throw exception
	 */
	static <E extends Exception> FileFilterWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code FileFilterWithException} to a {@code FileFilter} that wraps
	 * exception to {@code RuntimeException}.
	 *
	 * @param predicate
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked FileFilter
	 * @see #uncheck()
	 * @see #unchecked(FileFilterWithException, Function)
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FileFilter unchecked(FileFilterWithException<E> predicate) {
		return verifyPredicate(predicate).uncheck();
	}

	/**
	 * Converts a {@code FileFilterWithException} to a {@code FileFilter} that wraps
	 * exception to {@code RuntimeException} by using the provided mapping function.
	 *
	 * @param predicate
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked FileFilter
	 * @see #uncheck()
	 * @see #unchecked(FileFilterWithException)
	 * @throws NullPointerException
	 *             if predicate or exceptionMapper is null
	 */
	static <E extends Exception> FileFilter unchecked(FileFilterWithException<E> predicate,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyPredicate(predicate);
		verifyExceptionMapper(exceptionMapper);
		return new FileFilterWithException<E>() {

			@Override
			public boolean accept(File pathname) throws E {
				return predicate.accept(pathname);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code FileFilterWithException} to a lifted {@code FileFilter}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted FileFilter
	 * @see #lift()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FileFilter lifted(FileFilterWithException<E> predicate) {
		return verifyPredicate(predicate).lift();
	}

	/**
	 * Converts a {@code FileFilterWithException} to a lifted {@code FileFilter}
	 * returning {@code false} in case of exception.
	 *
	 * @param predicate
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted FileFilter
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if predicate is null
	 */
	static <E extends Exception> FileFilter ignored(FileFilterWithException<E> predicate) {
		return verifyPredicate(predicate).ignore();
	}

}
