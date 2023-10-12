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
import static ch.powerunit.extensions.exceptions.Constants.verifyFunction;

import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.FilterInfo;
import java.io.ObjectInputFilter.Status;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Filter classes, array lengths, and graph metrics during deserialization that
 * may throw exception. If set on an {@link ObjectInputStream}, the
 * {@link #checkInput checkInput(FilterInfo)} method is called to validate
 * classes, the length of each array, the number of objects being read from the
 * stream, the depth of the graph, and the total number of bytes read from the
 * stream.
 * <h2>General contract</h2>
 * <ul>
 * <li><b>{@link #checkInput(FilterInfo) Status checkInput(FilterInfo
 * filterInfo) throws E}</b>&nbsp;-&nbsp;The functional method.</li>
 * <li><b>uncheck</b>&nbsp;-&nbsp;Return a {@code ObjectInputFilter}</li>
 * <li><b>lift</b>&nbsp;-&nbsp;Return a
 * {@code Function<FilterInfo, Optional<Status>>}</li>
 * <li><b>ignore</b>&nbsp;-&nbsp;Return a {@code ObjectInputFilter}</li>
 * </ul>
 *
 * @see ObjectInputFilter
 * @param <E>
 *            the type of the potential exception of the function
 * @since 2.0.0
 */
@FunctionalInterface
public interface ObjectInputFilterWithException<E extends Exception> extends
		ObjectReturnExceptionHandlerSupport<ObjectInputFilter, Function<FilterInfo, Optional<Status>>, Function<FilterInfo, CompletionStage<Status>>, Status, ObjectInputFilterWithException<E>> {

	/**
	 * Check the class, array length, number of object references, depth, stream
	 * size, and other available filtering information. Implementations of this
	 * method check the contents of the object graph being created during
	 * deserialization. The filter returns {@link Status#ALLOWED Status.ALLOWED},
	 * {@link Status#REJECTED Status.REJECTED}, or {@link Status#UNDECIDED
	 * Status.UNDECIDED}.
	 *
	 * @param filterInfo
	 *            provides information about the current object being deserialized,
	 *            if any, and the status of the {@link ObjectInputStream}
	 * @return {@link Status#ALLOWED Status.ALLOWED} if accepted,
	 *         {@link Status#REJECTED Status.REJECTED} if rejected,
	 *         {@link Status#UNDECIDED Status.UNDECIDED} if undecided.
	 * @throws E
	 *             any exception
	 * @see ObjectInputFilter#checkInput(java.io.ObjectInputFilter.FilterInfo)
	 */
	Status checkInput(FilterInfo filterInfo) throws E;

	/**
	 * Converts this {@code ObjectInputFilterWithException} to a
	 * {@code ObjectInputFilter} that convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked ObjectInputFilter
	 * @see #unchecked(ObjectInputFilterWithException)
	 * @see #unchecked(ObjectInputFilterWithException, Function)
	 * @see ObjectInputFilter
	 */
	@Override
	default ObjectInputFilter uncheck() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> checkInput(t), throwingHandler());
	}

	/**
	 * Converts this {@code ObjectInputFilterWithException} to a lifted
	 * {@code Function} using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(ObjectInputFilterWithException)
	 * @see Function
	 */
	@Override
	default Function<FilterInfo, Optional<Status>> lift() {
		return t -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(checkInput(t)),
				notThrowingHandler());
	}

	@Override
	default Status defaultValue() {
		return Status.UNDECIDED;
	}

	/**
	 * Converts this {@code ObjectInputFilterWithException} to a lifted
	 * {@code ObjectInputFilter} returning {@link Status#UNDECIDED Status.UNDECIDED}
	 * (or the value redefined by the method {@link #defaultValue()}) in case of
	 * exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(ObjectInputFilterWithException)
	 * @see Function
	 */
	@Override
	default ObjectInputFilter ignore() {
		return t -> lift().apply(t).orElse(defaultValue());
	}

	/**
	 * Convert this {@code ObjectInputFilterWithException} to a lifted
	 * {@code ObjectInputFilter} return {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(ObjectInputFilterWithException)
	 * @see CompletionStage
	 */
	@Override
	default Function<FilterInfo, CompletionStage<Status>> stage() {
		return t -> ObjectReturnExceptionHandlerSupport.staged(() -> checkInput(t));
	}

	/**
	 * Returns a ObjectInputFilter that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the ObjectInputFilter the type of the
	 *            result of the function
	 * @param <E>
	 *            the type of the exception
	 * @return a ObjectInputFilter that always throw exception
	 */
	static <E extends Exception> ObjectInputFilterWithException<E> failing(Supplier<E> exceptionBuilder) {
		return t -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code ObjectInputFilterWithException} to a
	 * {@code ObjectInputFilter} that convert exception to {@code RuntimeException}.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ObjectInputFilterWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> ObjectInputFilter unchecked(ObjectInputFilterWithException<E> function) {
		return verifyFunction(function).uncheck();
	}

	/**
	 * Converts a {@code ObjectInputFilterWithException} to a
	 * {@code ObjectInputFilter} that wraps exception to {@code RuntimeException} by
	 * using the provided mapping function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(ObjectInputFilterWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <E extends Exception> ObjectInputFilter unchecked(ObjectInputFilterWithException<E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		verifyFunction(function);
		verifyExceptionMapper(exceptionMapper);
		return new ObjectInputFilterWithException<E>() {

			@Override
			public Status checkInput(FilterInfo filterInfo) throws E {
				return function.checkInput(filterInfo);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code ObjectInputFilterWithException} to a lifted
	 * {@code ObjectInputFilter} using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> Function<FilterInfo, Optional<Status>> lifted(
			ObjectInputFilterWithException<E> function) {
		return verifyFunction(function).lift();
	}

	/**
	 * Converts a {@code ObjectInputFilterWithException} to a lifted
	 * {@code ObjectInputFilter} returning {@link Status#UNDECIDED Status.UNDECIDED}
	 * in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> ObjectInputFilter ignored(ObjectInputFilterWithException<E> function) {
		return verifyFunction(function).ignore();
	}

	/**
	 * Converts a {@code ObjectInputFilterWithException} to a lifted
	 * {@code ObjectInputFilter} returning a default value in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param defaultValue
	 *            the default value in case of exception. <b>Can't be null</b>.
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @see #ignored(ObjectInputFilterWithException)
	 * @throws NullPointerException
	 *             if function or defaultValue is null
	 * @since 3.0.0
	 */
	static <E extends Exception> ObjectInputFilter ignored(ObjectInputFilterWithException<E> function,
			Status defaultValue) {
		verifyFunction(function);
		Objects.requireNonNull(defaultValue, "defaultValue can't be null");
		return new ObjectInputFilterWithException<E>() {

			@Override
			public Status checkInput(FilterInfo filterInfo) throws E {
				return function.checkInput(filterInfo);
			}

			@Override
			public Status defaultValue() {
				return defaultValue;
			}

		}.ignore();
	}

	/**
	 * Convert this {@code ObjectInputFilterWithException} to a lifted
	 * {@code Function} return {@code CompletionStage} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <E extends Exception> Function<FilterInfo, CompletionStage<Status>> staged(
			ObjectInputFilterWithException<E> function) {
		return verifyFunction(function).stage();
	}

}
