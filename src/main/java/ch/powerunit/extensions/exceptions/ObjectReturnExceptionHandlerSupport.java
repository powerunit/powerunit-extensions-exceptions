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

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Root interface to support global operations related to exception handling for
 * functional interface returning Object result.
 *
 * @param <F>
 *            the type of the java standard functional interface. For example,
 *            {@code Function<T,R>}.
 * @param <L>
 *            the type of the lifted functional interface. For example,
 *            {@code Function<T,Optional<R>>}.
 * @param <S>
 *            the type of the staged functional interface. For example,
 *            {@code Function<T,CompletionStage<R>>}.
 * @param <T>
 *            the type of the return type of the Functional interface.
 */
public interface ObjectReturnExceptionHandlerSupport<F, L, S, T> extends ExceptionHandlerSupport<F, L> {

	/**
	 * Converts this functional interface to the corresponding one in java and wrap
	 * exception using {@link #exceptionMapper()}.
	 * <p>
	 * Conceptually, the exception encapsulation is done in the following way :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		throw new exceptionMapper().apply(e);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the unchecked operation
	 * @see #lift()
	 * @see #ignore()
	 */
	@Override
	F uncheck();

	/**
	 * Converts this functional interface to a lifted one. The lifted version return
	 * an Optional of the original return type.
	 * <p>
	 * Conceptually, this is done by :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return Optional.ofNullable(realaction(xxx));
	 * 	} catch (Exception e) {
	 * 		return Optional.empty();
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #ignore()
	 */
	@Override
	L lift();

	/**
	 * Converts this functional interface to the corresponding java standard
	 * functional interface returning a null in case of error.
	 * <p>
	 * The principle is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		return realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		return null;
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the operation that ignore error
	 * @see #uncheck()
	 * @see #lift()
	 */
	@Override
	F ignore();

	/**
	 * Converts this functional interface to the corresponding java standard
	 * functional interface with staged result.
	 *
	 * @return the operation supporting stage.
	 * @since 1.1.0
	 * @see CompletionStage
	 */
	S stage();

	/**
	 * Used internally to support the exception interception.
	 *
	 * @param internal
	 *            the call to be done
	 * @param exceptionhandler
	 *            the exception handler. May throw RuntimeException or return some
	 *            default value.
	 * @return the result
	 * @throws RuntimeException
	 *             in case of error
	 * @param <T>
	 *            type of the return value
	 */
	static <T> T unchecked(Callable<T> internal, Function<Exception, T> exceptionhandler) {
		try {
			return internal.call();
		} catch (Exception e) {
			// exceptionhandler must throw the exception if needed
			return exceptionhandler.apply(e);
		}
	}

	/**
	 * Used internally to support the exception interception.
	 *
	 * @param internal
	 *            the call to be done
	 * @return the completion stage
	 * @throws RuntimeException
	 *             in case of error
	 * @param <T>
	 *            type of the return value
	 */
	static <T> CompletionStage<T> staged(Callable<T> internal) {
		try {
			return completedFuture(internal.call());
		} catch (Exception e) {
			// failedStage only available since 9
			CompletableFuture<T> result = new CompletableFuture<>();
			result.completeExceptionally(e);
			return result;
		}
	}

	/**
	 * Used internally to support the exception interception.
	 *
	 * @return exception handler to support exception control
	 */
	default Function<Exception, T> throwingHandler() {
		return e -> {
			throw exceptionMapper().apply(e);
		};
	}

	/**
	 * Used internally to support the exception interception.
	 *
	 * @return exception handler to ignore exception
	 */
	default Function<Exception, Optional<T>> notThrowingHandler() {
		return e -> Optional.empty();
	}
}
