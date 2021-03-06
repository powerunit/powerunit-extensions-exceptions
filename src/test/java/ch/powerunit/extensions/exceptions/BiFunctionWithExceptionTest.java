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
public class BiFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> BiFunctionWithException.failing(Exception::new).apply(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen() throws Exception {
		BiFunctionWithException<String, String, String, Exception> fct1 = (x, y) -> x + y;
		FunctionWithException<String, String, Exception> fct2 = x -> x + "2";
		assertThat(fct1.andThen(fct2).apply("3", "1")).is("312");
	}

	@Test
	public void testCheckedNoException() {
		assertThat(BiFunctionWithException.unchecked((x, y) -> "" + x + y).apply("2", "1")).is("21");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> BiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}).apply(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> BiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).apply(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(BiFunctionWithException.lifted((x, y) -> "" + x + y).apply("2", "1")).is(optionalIs("21"));
	}

	@Test
	public void testLiftedException() {
		assertThat(BiFunctionWithException.lifted((x, y) -> {
			throw new Exception();
		}).apply("x", "y")).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(BiFunctionWithException.ignored((x, y) -> "" + x + y).apply("2", "1")).is("21");
	}

	@Test
	public void testIgnoredException() {
		assertThat(BiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}).apply("x", "y")).isNull();
	}

	@Test
	public void testStagedNoException() {
		assertThat(BiFunctionWithException.staged((x, y) -> "" + x + y).apply("2", "1").toCompletableFuture().join())
				.is("21");
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> BiFunctionWithException.staged((y, z) -> {
			throw new Exception();
		}).apply("x", "x").toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		BiFunctionWithException.asBiConsumer((x, y) -> 1).accept("2", "3");
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> BiFunctionWithException.asBiConsumer((y, z) -> {
			throw new Exception();
		}).accept("2", "3")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsFunctionNoException() throws Exception {
		assertThat(BiFunctionWithException.asFunction((x, y) -> "" + x + y, 2).apply("3")).is("32");
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> BiFunctionWithException.asFunction((y, z) -> {
			throw new Exception();
		}, "").apply("2")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(BiFunctionWithException.ignored((x, y) -> "" + x + y, "x").apply("2", "1")).is("21");
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(BiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}, "x").apply("x", "y")).is("x");
	}

}
