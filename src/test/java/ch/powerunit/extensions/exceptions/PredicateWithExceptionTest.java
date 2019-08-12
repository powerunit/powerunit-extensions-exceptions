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

public class PredicateWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> PredicateWithException.failing(Exception::new).test(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAnd() throws Exception {
		PredicateWithException<String, Exception> fct1 = x -> true;
		PredicateWithException<String, Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).test("3")).is(false);
	}

	@Test
	public void testOr() throws Exception {
		PredicateWithException<String, Exception> fct1 = x -> true;
		PredicateWithException<String, Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).test("3")).is(true);
	}

	@Test
	public void testNegate() throws Exception {
		PredicateWithException<String, Exception> fct1 = x -> true;
		assertThat(fct1.negate().test("3")).is(false);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(PredicateWithException.unchecked(x -> true).test("2")).is(true);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> PredicateWithException.unchecked(y -> {
			throw new Exception();
		}).test(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> PredicateWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).test(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(PredicateWithException.ignored(x -> true).test("2")).is(true);
	}

	@Test
	public void testIgnoredException() {
		assertThat(PredicateWithException.ignored(y -> {
			throw new Exception();
		}).test("x")).is(false);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		PredicateWithException.asConsumer(x -> true).accept("2");
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> PredicateWithException.asConsumer(y -> {
			throw new Exception();
		}).accept("2")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsFunctionNoException() throws Exception {
		PredicateWithException<String, Exception> fct = x -> true;
		assertThat(fct.asFunction().apply("x")).is(true);
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> PredicateWithException.failing(Exception::new).asFunction().apply("x"))
				.throwException(instanceOf(Exception.class));
	}

}
