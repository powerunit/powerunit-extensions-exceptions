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
public class DoubleConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> DoubleConsumerWithException.failing(Exception::new).accept(12), 12d)
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		DoubleConsumerWithException<Exception> fct1 = x -> {
		};
		DoubleConsumerWithException<Exception> fct2 = x -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).accept(12)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		DoubleConsumerWithException.unchecked(x -> {
		}).accept(12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> DoubleConsumerWithException.unchecked(y -> {
			throw new Exception();
		}).accept(12d)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> DoubleConsumerWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).accept(12)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		DoubleConsumerWithException.lifted(x -> {
		}).accept(12);
	}

	@Test
	public void testLiftedException() {
		DoubleConsumerWithException.lifted(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testIgnoredNoException() {
		DoubleConsumerWithException.ignored(x -> {
		}).accept(12);
	}

	@Test
	public void testIgnoredException() {
		DoubleConsumerWithException.ignored(y -> {
			throw new Exception();
		}).accept(12);
	}

	@Test
	public void testAsFunctionNoException() {
		assertThat(DoubleConsumerWithException.function(x -> {
		}).stage().apply(12d).toCompletableFuture().join()).isNull();
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> DoubleConsumerWithException.function(y -> {
			throw new Exception();
		}).stage().apply(12d).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

}
