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
 * function interface without return value.
 *
 * @author borettim
 * @param <F>
 *            the type of the java standard function interface
 * @param <L>
 *            the type of the lifted function interface
 * @param <T>
 *            the type of the return type
 */
public interface ObjectReturnExceptionHandlerSupport<F, L, T> extends ExceptionHandlerSupport<F, L> {

	/**
	 * Converts this function interface to the corresponding one in java and wrap
	 * exception.
	 *
	 * @return the unchecked operation
	 */
	@Override
	F uncheck();

	/**
	 * Converts this function interface to a lifted one.
	 * <p>
	 * This method is identical to {@link #ignore()}
	 *
	 * @return the lifted function
	 */
	@Override
	L lift();

	/**
	 * Converts this function interface to the corresponding <i>lifted</i> java
	 * standard interface ignoring exception.
	 *
	 * @return the operation that ignore error
	 */
	@Override
	F ignore();

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
