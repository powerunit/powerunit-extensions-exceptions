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

public class BiConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> BiConsumerWithException.failing(Exception::new).accept(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		BiConsumerWithException<String, String, Exception> fct1 = (x, y) -> {
		};
		BiConsumerWithException<String, String, Exception> fct2 = (x, y) -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).accept("3", "4")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		BiConsumerWithException.unchecked((x, y) -> {
		}).accept("2", "3");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> BiConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}).accept(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> BiConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).accept(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		BiConsumerWithException.lifted((x, y) -> {
		}).accept("2", "3");
	}

	@Test
	public void testLiftedException() {
		BiConsumerWithException.lifted((x, y) -> {
			throw new Exception();
		}).accept("2", "3");
	}

	@Test
	public void testIgnoredNoException() {
		BiConsumerWithException.ignored((x, y) -> {
		}).accept("2", "3");
	}

	@Test
	public void testIgnoredException() {
		BiConsumerWithException.ignored((x, y) -> {
			throw new Exception();
		}).accept("2", "3");
	}

	@Test
	public void testAsFunctionNoException() {
		assertThat(BiConsumerWithException.biFunction((x, y) -> {
		}).stage().apply("2", "3").toCompletableFuture().join()).isNull();
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> BiConsumerWithException.biFunction((y, z) -> {
			throw new Exception();
		}).stage().apply("x", "x").toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

}
