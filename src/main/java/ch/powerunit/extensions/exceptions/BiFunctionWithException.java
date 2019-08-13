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

import static ch.powerunit.extensions.exceptions.Constants.FUNCTION_CANT_BE_NULL;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts two arguments, may throw exception and
 * produces a result.
 *
 * @author borettim
 * @see BiFunction
 * @param <T>
 *            the type of the first input to the function
 * @param <U>
 *            the type of the second input to the function
 * @param <R>
 *            the type of the result of the function
 * @param <E>
 *            the type of the potential exception of the function
 */
@FunctionalInterface
public interface BiFunctionWithException<T, U, R, E extends Exception>
		extends ExceptionHandlerSupport<BiFunction<T, U, R>, BiFunction<T, U, Optional<R>>, E> {

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
	 * convert exception to {@code RuntimeException}.
	 *
	 * @return the unchecked function
	 * @see #unchecked(BiFunctionWithException)
	 * @see #unchecked(BiFunctionWithException, Function)
	 */
	@Override
	default BiFunction<T, U, R> uncheck() {
		return (t, u) -> {
			try {
				return apply(t, u);
			} catch (Exception e) {
				throw exceptionMapper().apply(e);
			}
		};

	}

	/**
	 * Converts this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * using {@code Optional} as return value.
	 *
	 * @return the lifted function
	 * @see #lifted(BiFunctionWithException)
	 */
	@Override
	default BiFunction<T, U, Optional<R>> lift() {
		return (t, u) -> {
			try {
				return Optional.ofNullable(apply(t, u));
			} catch (Exception e) {
				return Optional.empty();
			}
		};
	}

	/**
	 * Converts this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * returning {@code null} in case of exception.
	 *
	 * @return the function that ignore error
	 * @see #ignored(BiFunctionWithException)
	 */
	@Override
	default BiFunction<T, U, R> ignore() {
		return (t, u) -> {
			try {
				return apply(t, u);
			} catch (Exception e) {
				return null;
			}
		};
	}

	/**
	 * Convert this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * return {@code CompletionStage} as return value.
	 *
	 * @return the lifted function
	 * @see #staged(BiFunctionWithException)
	 */
	default BiFunction<T, U, CompletionStage<R>> stage() {
		return (t, u) -> {
			try {
				return completedFuture(apply(t, u));
			} catch (Exception e) {
				// failedStage only available since 9
				CompletableFuture<R> result = new CompletableFuture<>();
				result.completeExceptionally(e);
				return result;
			}
		};
	}

	/**
	 * Transforms this {@code BiFunctionWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @return the operation
	 * @see #biConsumer(BiFunctionWithException)
	 */
	default BiConsumerWithException<T, U, Exception> asBiConsumer() {
		return this::apply;
	}

	/**
	 * Transforms this {@code BiFunctionWithException} to a
	 * {@code SupplierWithException}.
	 *
	 * @param t
	 *            the first input for the generated supplier.
	 * @param u
	 *            the second input for the generated suppoler.
	 * @return the supplier
	 * @see #asSupplier()
	 */
	default SupplierWithException<R, Exception> asSupplier(T t, U u) {
		return () -> apply(t, u);
	}

	/**
	 * Transforms this {@code BiFunctionWithException} to a
	 * {@code BiSupplierWithException}, passing {@code null} as input.
	 *
	 * @return the supplier
	 * @see #asSupplier(Object,Object)
	 */
	default SupplierWithException<R, Exception> asSupplier() {
		return asSupplier(null, null);
	}

	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>
	 *            the type of output of the {@code after} function, and of the
	 *            composed function
	 * @param after
	 *            the function to apply after this function is applied
	 * @return a composed function that first applies this function and then applies
	 *         the {@code after} function
	 * @throws NullPointerException
	 *             if after is null
	 * @see BiFunction#andThen(Function)
	 */
	default <V> BiFunctionWithException<T, U, V, E> andThen(
			FunctionWithException<? super R, ? extends V, ? extends E> after) {
		requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}

	/**
	 * Returns a function that always throw exception.
	 *
	 * @param exceptionBuilder
	 *            the supplier to create the exception
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
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
	 *
	 * @param function
	 *            to be unchecked
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiFunctionWithException, Function)
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> unchecked(BiFunctionWithException<T, U, R, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.uncheck();
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a {@code BiFunction} that
	 * convert exception to {@code RuntimeException} by using the provided mapping
	 * function.
	 *
	 * @param function
	 *            the be unchecked
	 * @param exceptionMapper
	 *            a function to convert the exception to the runtime exception.
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the unchecked exception
	 * @see #uncheck()
	 * @see #unchecked(BiFunctionWithException)
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> unchecked(BiFunctionWithException<T, U, R, E> function,
			Function<Exception, RuntimeException> exceptionMapper) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		requireNonNull(exceptionMapper, "exceptionMapper can't be null");
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
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #lift()
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, Optional<R>> lifted(
			BiFunctionWithException<T, U, R, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.lift();
	}

	/**
	 * Converts a {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * returning {@code null} in case of exception.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #ignore()
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, R> ignored(BiFunctionWithException<T, U, R, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.ignore();
	}

	/**
	 * Convert this {@code BiFunctionWithException} to a lifted {@code BiFunction}
	 * return {@code CompletionStage} as return value.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the lifted function
	 * @see #stage()
	 */
	static <T, U, R, E extends Exception> BiFunction<T, U, CompletionStage<R>> staged(
			BiFunctionWithException<T, U, R, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.stage();
	}

	/**
	 * Transforms this {@code BiFunctionWithException} to a
	 * {@code BiConsumerWithException}.
	 *
	 * @param function
	 *            to be lifted
	 * @param <T>
	 *            the type of the first input object to the function
	 * @param <U>
	 *            the type of the second input object to the function
	 * @param <R>
	 *            the type of the output object to the function
	 * @param <E>
	 *            the type of the potential exception
	 * @return the operation function
	 * @see #asBiConsumer()
	 */
	static <T, U, R, E extends Exception> BiConsumerWithException<T, U, Exception> biConsumer(
			BiFunctionWithException<T, U, R, E> function) {
		requireNonNull(function, FUNCTION_CANT_BE_NULL);
		return function.asBiConsumer();
	}

}
