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

import static ch.powerunit.extensions.exceptions.Constants.EXCEPTIONMAPPER_CANT_BE_NULL;
import static ch.powerunit.extensions.exceptions.Constants.FUNCTION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts two arguments, may thrown exception and
 * produces a result. This is the two-arity specialization of
 * {@link FunctionWithException}.
 *
 * @see BiFunction
 * @param <T>
 *            the type of the first argument to the function
 * @param <U>
 *            the type of the second argument to the function
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface BiFunctionWithException<T, U, R, E extends Exception>
		extends ObjectReturnExceptionHandlerSupport<BiFunction<T, U, R>, BiFunction<T, U, Optional<R>>, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @param u
	 *            the second function argument
	 * @return the function result
	 * @throws E
	 *             any exception
	 * @see BiFunction#apply(Object,Object)
	 */
	R apply(T t, U u) throws E;

	/**
	 * Converts this {@code BiFunctionWithException} to a {@code BiFunction} that
	 * wraps exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(BiFunctionWithException)
	 * @see #unchecked(BiFunctionWithException, Function)
	 * @see BiFunction
	 */
	@Override
	default BiFunction<T, U, R> uncheck() {
		return (t, u) -> ObjectReturnExceptionHandlerSupport.unchecked(() -> apply(t, u), throwingHandler());

	}

	/**
	 * Converts this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(BiFunctionWithException)
	 * @see BiFunction
	 */
	@Override
	default BiFunction<T, U, Optional<R>> lift() {
		return (t, u) -> ObjectReturnExceptionHandlerSupport.unchecked(() -> Optional.ofNullable(apply(t, u)),
				notThrowingHandler());
	}

	/**
	 * Converts this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * returning {@code null} in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(BiFunctionWithException)
	 * @see BiFunction
	 */
	@Override
	default BiFunction<T, U, R> ignore() {
		return lift().andThen(o -> o.orElse(null));
	}

	/**
	 * Convert this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * that uses {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(BiFunctionWithException)
	 * @see BiFunction
	 * @see CompletionStage
	 */
	default BiFunction<T, U, CompletionStage<R>> stage() {
		return (t, u) -> ObjectReturnExceptionHandlerSupport.staged(() -> apply(t, u));
	}

	/**
	 * Returns a composed {@code FunctionWithException} that first applies this
	 * {@code FunctionWithException} to its input, and then applies the
	 * {@code after} {@code FunctionWithException} to the result. If evaluation of
	 * either {@code FunctionWithException} throws an exception, it is relayed to
	 * the caller of the composed function.
	 *
	 * @param <V>
	 *            the type of output of the {@code after}
	 *            {@code FunctionWithException}, and of the composed
	 *            {@code FunctionWithException}
	 * @param after
	 *            the function to apply after this {@code FunctionWithException} is
	 *            applied
	 * @return a composed function that first applies this function and then applies
	 *         the {@code after} function
	 * @throws NullPointerException
	 *             if after is null
	 *
	 * @see BiFunction#andThen(Function)
	 */
	default <V> BiFunctionWithException<T, U, V, E> andThen(
			FunctionWithException<? super R, ? extends V, ? extends E> after) {
		requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}

	/**
	 * Returns a {@code FunctionWithException} that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the exception
	 * @return a function that always throw exception
	 */
	static <T, U, R, E extends Exception> BiFunctionWithException<T, U, R, E> failing(Supplier<E> exceptionBuilder) {
		return (t, u) -> {
			throw exceptionBuilder.get();
		};
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a {@code BiFunction} that
	 * convert exception to {@code RuntimeException}.
	 * <p>
	 * For example :
	 * 
	 * <pre>
	 * BiFunction&lt;String, String, String&gt; biFunctionThrowingRuntimeException = BiFunctionWithException
	 * 		.unchecked(biFouctionThrowingException);
	 * </pre>
	 * 
	 * Will generate a {@code BiFunction} throwing {@code RuntimeException} in case
	 * of error.
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiFunctionWithException, Function)
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> unchecked(BiFunctionWithException<T, U, R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).uncheck();
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a {@code BiFunction} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 * <p>
	 * For example :
	 * 
	 * <pre>
	 * BiFunction&lt;String, String, String&gt; functionThrowingRuntimeException = BiFunctionWithException
	 * 		.unchecked(fonctionThrowingException, IllegalArgumentException::new);
	 * </pre>
	 * 
	 * Will generate a {@code BiFunction} throwing {@code IllegalArgumentException}
	 * in case of error.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiFunctionWithException)
	 * @throws NullPointerException
	 *             if function or exceptionMapper is null
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> unchecked(BiFunctionWithException<T, U, R, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, EXCEPTIONMAPPER_CANT_BE_NULL);
		return new BiFunctionWithException<T, U, R, E>() {

			@Override
			public R apply(T t, U u) throws E {
				return function.apply(t, u);
			}

			@Override
			public Function<Exception, RuntimeException> exceptionMapper() {
				return exceptionMapper;
			}

		}.uncheck();
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * using {@code Optional} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, Optional<R>> lifted(
			BiFunctionWithException<T, U, R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).lift();
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> ignored(BiFunctionWithException<T, U, R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).ignore();
	}

	/**
	 * Convert this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * return {@code CompletionStage} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first argument to the function
	 * @param <U>
	 *            the type of the second argument to the function
	 * @param <R>
	 *            the type of the result of the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 * @throws NullPointerException
	 *             if function is null
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, CompletionStage<R>> staged(
			BiFunctionWithException<T, U, R, E> function) {
		return requireNonNull(function, FUNCTION_CANT_BE_NULL).stage();
	}

}
