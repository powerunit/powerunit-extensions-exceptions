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
public class LongConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> LongConsumerWithException.failing(Exception::new).accept(12), 12L)
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		LongConsumerWithException<Exception> fct1 = x -> {
		};
		LongConsumerWithException<Exception> fct2 = x -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).accept(12)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		LongConsumerWithException.unchecked(x -> {
		}).accept(12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> LongConsumerWithException.unchecked(y -> {
			throw new Exception();
		}).accept(12)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> LongConsumerWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).accept(12)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		LongConsumerWithException.lifted(x -> {
		}).accept(12);
	}

	@Test
	public void testLiftedException() {
		LongConsumerWithException.lifted(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testIgnoredNoException() {
		LongConsumerWithException.ignored(x -> {
		}).accept(12);
	}

	@Test
	public void testIgnoredException() {
		LongConsumerWithException.ignored(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		LongConsumerWithException.asConsumer(x -> {
		}).accept(2L);
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> LongConsumerWithException.asConsumer(y -> {
			throw new Exception();
		}).accept(3L)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testStagedNoException() {
		LongConsumerWithException.staged(x -> {
		}).apply(1).toCompletableFuture().join();
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> LongConsumerWithException.staged(y -> {
			throw new Exception();
		}).apply(1).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}
}
