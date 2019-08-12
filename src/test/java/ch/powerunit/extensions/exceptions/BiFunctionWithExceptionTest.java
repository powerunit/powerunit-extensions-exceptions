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
		BiFunctionWithException.biConsumer((x, y) -> "" + x + y).accept("2", "1");
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> BiFunctionWithException.biConsumer((y, z) -> {
			throw new Exception();
		}).accept("2", "1")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsSupplierNoException() throws Exception {
		BiFunctionWithException<String, String, String, Exception> fct = (x, y) -> "" + x + y;
		assertThat(fct.asSupplier().get()).is("nullnull");
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> BiFunctionWithException.failing(Exception::new).asSupplier().get())
				.throwException(instanceOf(Exception.class));
	}

}
