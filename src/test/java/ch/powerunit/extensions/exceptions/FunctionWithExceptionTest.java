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
public class FunctionWithExceptionTest implements TestSuite {

	@Test
	public void testIdentity() throws Exception {
		assertThat(FunctionWithException.identity().apply("x")).is("x");
	}

	@Test
	public void testFailing() {
		assertWhen((x) -> FunctionWithException.failing(Exception::new).apply(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCompose() throws Exception {
		FunctionWithException<String, String, Exception> fct1 = x -> x + "1";
		FunctionWithException<String, String, Exception> fct2 = x -> x + "2";
		assertThat(fct1.compose(fct2).apply("3")).is("321");
	}

	@Test
	public void testandThen() throws Exception {
		FunctionWithException<String, String, Exception> fct1 = x -> x + "1";
		FunctionWithException<String, String, Exception> fct2 = x -> x + "2";
		assertThat(fct1.andThen(fct2).apply("3")).is("312");
	}

	@Test
	public void testCheckedNoException() {
		assertThat(FunctionWithException.unchecked(x -> x + "1").apply("2")).is("21");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> FunctionWithException.unchecked(y -> {
			throw new Exception();
		}).apply(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> FunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).apply(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(FunctionWithException.lifted(x -> x + "1").apply("2")).is(optionalIs("21"));
	}

	@Test
	public void testLiftedException() {
		assertThat(FunctionWithException.lifted(y -> {
			throw new Exception();
		}).apply("x")).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(FunctionWithException.ignored(x -> x + "1").apply("2")).is("21");
	}

	@Test
	public void testIgnoredException() {
		assertThat(FunctionWithException.ignored(y -> {
			throw new Exception();
		}).apply("x")).isNull();
	}

	@Test
	public void testStagedNoException() {
		assertThat(FunctionWithException.staged(x -> x + "1").apply("2").toCompletableFuture().join()).is("21");
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> FunctionWithException.staged(y -> {
			throw new Exception();
		}).apply("x").toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		FunctionWithException.consumer(x -> x).accept("2");
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> FunctionWithException.consumer(y -> {
			throw new Exception();
		}).accept("2")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsSupplierNoException() throws Exception {
		FunctionWithException<String, String, Exception> fct = x -> x;
		assertThat(fct.asSupplier().get()).isNull();
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> FunctionWithException.failing(Exception::new).asSupplier().get())
				.throwException(instanceOf(Exception.class));
	}

}
