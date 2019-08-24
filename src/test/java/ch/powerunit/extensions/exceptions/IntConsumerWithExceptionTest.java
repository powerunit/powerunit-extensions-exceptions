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

import java.util.concurrent.CompletionException;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class IntConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> IntConsumerWithException.failing(Exception::new).accept(12), 12)
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		IntConsumerWithException<Exception> fct1 = x -> {
		};
		IntConsumerWithException<Exception> fct2 = x -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).accept(12)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		IntConsumerWithException.unchecked(x -> {
		}).accept(12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> IntConsumerWithException.unchecked(y -> {
			throw new Exception();
		}).accept(12)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> IntConsumerWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).accept(12)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		IntConsumerWithException.lifted(x -> {
		}).accept(12);
	}

	@Test
	public void testLiftedException() {
		IntConsumerWithException.lifted(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testIgnoredNoException() {
		IntConsumerWithException.ignored(x -> {
		}).accept(12);
	}

	@Test
	public void testIgnoredException() {
		IntConsumerWithException.ignored(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		IntConsumerWithException.asConsumer(x -> {
		}).accept(2);
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> IntConsumerWithException.asConsumer(y -> {
			throw new Exception();
		}).accept(3)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testStagedNoException() {
		IntConsumerWithException.staged(x -> {
		}).apply(1).toCompletableFuture().join();
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> IntConsumerWithException.staged(y -> {
			throw new Exception();
		}).apply(1).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}
}
