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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * Root interface to support global operations related to exception handling for
 * functional interface without return value.
 *
 * @param <F>
 *            the type of the java standard functional interface. For example,
 *            {@code Consumer<T>}. The same functional interface is also used
 *            for the lifted and ignored version.
 * @param <S>
 *            the type of a java standard function interface to return a
 *            {@code CompletionStage}.
 */
public interface NoReturnExceptionHandlerSupport<F, S> extends ExceptionHandlerSupport<F, F> {

	/**
	 * Converts this functional interface to the corresponding one in java and wrap
	 * exception using {@link #exceptionMapper()}.
	 * <p>
	 * Conceptually, the exception encapsulation is done in the following way :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		// do the underlying functional interface action and return result if
	 * 		// applicable
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
	 * Converts this functional interface to a lifted one.
	 * <p>
	 * The concept is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		// do nothing
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #ignore()
	 */
	@Override
	default F lift() {
		return ignore();
	}

	/**
	 * Converts this functional interface to a lifted one.
	 * <p>
	 * The concept is :
	 *
	 * <pre>
	 * (xxx) -&gt; {
	 * 	try {
	 * 		realaction(xxx);
	 * 	} catch (Exception e) {
	 * 		// do nothing
	 * 	}
	 * }
	 * </pre>
	 *
	 * @return the lifted function
	 * @see #uncheck()
	 * @see #ignore()
	 */
	@Override
	F ignore();

	/**
	 * Converts this functional interface to a lifted one, using a
	 * {@code CompletionStage} as a return value.
	 *
	 * @return the lifted function
	 * @since 1.1.0
	 */
	S stage();

	/**
	 * Used internally to support the exception interception.
	 *
	 * @param internal
	 *            the call to be done
	 * @param exceptionhandler
	 *            the exception handler. May throw RuntimeException..
	 * @throws RuntimeException
	 *             in case of error
	 */
	static void unchecked(RunnableWithException<?> internal, Consumer<Exception> exceptionhandler) {
		try {
			internal.run();
		} catch (Exception e) {
			// exceptionhandler must throw the exception if needed
			exceptionhandler.accept(e);
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
	 */
	static CompletionStage<Void> staged(RunnableWithException<?> internal) {
		try {
			internal.run();
			return completedFuture(null);
		} catch (Exception e) {
			// failedStage only available since 9
			CompletableFuture<Void> result = new CompletableFuture<>();
			result.completeExceptionally(e);
			return result;
		}
	}

	/**
	 * Used internally to support the exception interception.
	 *
	 * @return exception handler to support exception control
	 */
	default Consumer<Exception> throwingHandler() {
		return e -> {
			throw exceptionMapper().apply(e);
		};
	}

	/**
	 * Used internally to support the exception interception.
	 *
	 * @return exception handler to ignore exception control
	 */
	default Consumer<Exception> notThrowingHandler() {
		return e -> {
		};
	}
}
