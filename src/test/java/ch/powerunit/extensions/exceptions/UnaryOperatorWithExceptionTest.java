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
public class UnaryOperatorWithExceptionTest implements TestSuite {

	@Test
	public void testIdentity() throws Exception {
		assertThat(UnaryOperatorWithException.identity().apply("x")).is("x");
	}

	@Test
	public void testFailing() {
		assertWhen((x) -> UnaryOperatorWithException.failing(Exception::new).apply(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCompose() throws Exception {
		UnaryOperatorWithException<String, Exception> fct1 = x -> x + "1";
		UnaryOperatorWithException<String, Exception> fct2 = x -> x + "2";
		assertThat(fct1.compose(fct2).apply("3")).is("321");
	}

	@Test
	public void testandThen() throws Exception {
		UnaryOperatorWithException<String, Exception> fct1 = x -> x + "1";
		UnaryOperatorWithException<String, Exception> fct2 = x -> x + "2";
		assertThat(fct1.andThen(fct2).apply("3")).is("312");
	}

	@Test
	public void testCheckedNoException() {
		assertThat(UnaryOperatorWithException.unchecked(x -> x + "1").apply("2")).is("21");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> UnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}).apply(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> UnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).apply(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(UnaryOperatorWithException.ignored(x -> x + "1").apply("2")).is("21");
	}

	@Test
	public void testIgnoredException() {
		assertThat(UnaryOperatorWithException.ignored(y -> {
			throw new Exception();
		}).apply("x")).isNull();
	}
}
