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

public class ConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ConsumerWithException.failing(Exception::new).accept(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		ConsumerWithException<String, Exception> fct1 = x -> {
		};
		ConsumerWithException<String, Exception> fct2 = x -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).accept("3")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		ConsumerWithException.unchecked(x -> {
		}).accept("2");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ConsumerWithException.unchecked(y -> {
			throw new Exception();
		}).accept(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ConsumerWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).accept(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		ConsumerWithException.lifted(x -> {
		}).accept("2");
	}

	@Test
	public void testLiftedException() {
		ConsumerWithException.lifted(y -> {
			throw new Exception();
		}).accept("2");
	}

	@Test
	public void testIgnoredNoException() {
		ConsumerWithException.ignored(x -> {
		}).accept("2");
	}

	@Test
	public void testIgnoredException() {
		ConsumerWithException.ignored(y -> {
			throw new Exception();
		}).accept("2");
	}

	@Test
	public void testAsFunctionNoException() {
		assertThat(ConsumerWithException.function(x -> {
		}).stage().apply("2").toCompletableFuture().join()).isNull();
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> ConsumerWithException.function(y -> {
			throw new Exception();
		}).stage().apply("x").toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

}
