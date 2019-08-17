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
public class BinaryOperatorWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> BinaryOperatorWithException.failing(Exception::new).apply(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(BinaryOperatorWithException.unchecked((x, y) -> "" + x + y).apply("2", "1")).is("21");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> BinaryOperatorWithException.unchecked((y, z) -> {
			throw new Exception();
		}).apply(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> BinaryOperatorWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).apply(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(BinaryOperatorWithException.ignored((x, y) -> "" + x + y).apply("2", "1")).is("21");
	}

	@Test
	public void testIgnoredException() {
		assertThat(BinaryOperatorWithException.ignored((x, y) -> {
			throw new Exception();
		}).apply("x", "x")).isNull();
	}

	@Test
	public void testLiftedNoException() {
		assertThat(BinaryOperatorWithException.lifted((x, y) -> "" + x + y).apply("2", "1")).is(optionalIs("21"));
	}

	@Test
	public void testLiftedException() {
		assertThat(BinaryOperatorWithException.lifted((x, y) -> {
			throw new Exception();
		}).apply("x", "x")).is(optionalIsNotPresent());
	}

}
