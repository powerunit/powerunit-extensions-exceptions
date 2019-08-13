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

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class BiPredicateWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> BiPredicateWithException.failing(Exception::new).test(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAnd1() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> true;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> false;
		assertThat(fct1.and(fct2).test("3", "4")).is(false);
	}

	@Test
	public void testAnd2() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> false;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> true;
		assertThat(fct1.and(fct2).test("3", "4")).is(false);
	}

	@Test
	public void testAnd3() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> false;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> false;
		assertThat(fct1.and(fct2).test("3", "4")).is(false);
	}

	@Test
	public void testAnd4() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> true;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> true;
		assertThat(fct1.and(fct2).test("3", "4")).is(true);
	}

	@Test
	public void testOr1() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> true;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> false;
		assertThat(fct1.or(fct2).test("3", "4")).is(true);
	}

	@Test
	public void testOr2() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> false;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> true;
		assertThat(fct1.or(fct2).test("3", "4")).is(true);
	}

	@Test
	public void testOr3() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> true;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> true;
		assertThat(fct1.or(fct2).test("3", "4")).is(true);
	}

	@Test
	public void testOr4() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> false;
		BiPredicateWithException<String, String, Exception> fct2 = (x, y) -> false;
		assertThat(fct1.or(fct2).test("3", "4")).is(false);
	}

	@Test
	public void testNegate() throws Exception {
		BiPredicateWithException<String, String, Exception> fct1 = (x, y) -> true;
		assertThat(fct1.negate().test("3", "4")).is(false);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(BiPredicateWithException.unchecked((x, y) -> true).test("2", "3")).is(true);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> BiPredicateWithException.unchecked((y, z) -> {
			throw new Exception();
		}).test(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> BiPredicateWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).test(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(BiPredicateWithException.lifted((x, y) -> true).test("2", "3")).is(true);
	}

	@Test
	public void testLiftedException() {
		assertThat(BiPredicateWithException.lifted((x, y) -> {
			throw new Exception();
		}).test("x", "x")).is(false);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(BiPredicateWithException.ignored((x, y) -> true).test("2", "3")).is(true);
	}

	@Test
	public void testIgnoredException() {
		assertThat(BiPredicateWithException.ignored((x, y) -> {
			throw new Exception();
		}).test("x", "x")).is(false);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		BiPredicateWithException.biConsumer((x, y) -> true).accept("2", "3");
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> BiPredicateWithException.biConsumer((y, z) -> {
			throw new Exception();
		}).accept("2", "3")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsFunctionNoException() throws Exception {
		BiPredicateWithException<String, String, Exception> fct = (x, y) -> true;
		assertThat(fct.asBiFunction().apply("x", "y")).is(true);
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> BiPredicateWithException.failing(Exception::new).asBiFunction().apply("x", "y"))
				.throwException(instanceOf(Exception.class));
	}

}
