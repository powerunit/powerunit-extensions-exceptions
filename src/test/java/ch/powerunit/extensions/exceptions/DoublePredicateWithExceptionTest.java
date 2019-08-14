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

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class DoublePredicateWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> DoublePredicateWithException.failing(Exception::new).test(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAnd1() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> true;
		DoublePredicateWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).test(3)).is(false);
	}

	@Test
	public void testAnd2() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> false;
		DoublePredicateWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).test(2)).is(false);
	}

	@Test
	public void testAnd3() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> false;
		DoublePredicateWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).test(4)).is(false);
	}

	@Test
	public void testAnd4() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> true;
		DoublePredicateWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).test(5)).is(true);
	}

	@Test
	public void testOr1() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> true;
		DoublePredicateWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).test(1)).is(true);
	}

	@Test
	public void testOr2() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> true;
		DoublePredicateWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).test(1)).is(true);
	}

	@Test
	public void testOr3() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> false;
		DoublePredicateWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).test(1)).is(true);
	}

	@Test
	public void testOr4() throws Exception {
		DoublePredicateWithException<Exception> fct1 = x -> false;
		DoublePredicateWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).test(2)).is(false);
	}

	@Test
	public void testNegate1() throws Exception {
		assertThat(DoublePredicateWithException.negate(x -> true).test(4)).is(false);
	}

	@Test
	public void testNegate2() throws Exception {
		assertThat(DoublePredicateWithException.negate(x -> false).test(4)).is(true);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(DoublePredicateWithException.unchecked(x -> true).test(1)).is(true);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> DoublePredicateWithException.unchecked(y -> {
			throw new Exception();
		}).test(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(DoublePredicateWithException.unchecked(x -> true, RuntimeException::new).test(12)).is(true);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> DoublePredicateWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).test(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(DoublePredicateWithException.lifted(x -> true).test(1)).is(true);
	}

	@Test
	public void testLiftedException() {
		assertThat(DoublePredicateWithException.lifted(y -> {
			throw new Exception();
		}).test(1)).is(false);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(DoublePredicateWithException.ignored(x -> true).test(2)).is(true);
	}

	@Test
	public void testIgnoredException() {
		assertThat(DoublePredicateWithException.ignored(y -> {
			throw new Exception();
		}).test(3)).is(false);
	}

}
