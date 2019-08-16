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
public class ToIntBiFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToIntBiFunctionWithException.failing(Exception::new).applyAsInt(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToIntBiFunctionWithException.unchecked((x, y) -> 12).applyAsInt("2", "3")).is(12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToIntBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}).applyAsInt(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToIntBiFunctionWithException.unchecked((x, y) -> 12, RuntimeException::new).applyAsInt(12, 48))
				.is(12);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToIntBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).applyAsInt(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToIntBiFunctionWithException.lifted((x, y) -> 3).applyAsInt("2", "3")).is(3);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToIntBiFunctionWithException.lifted((x, y) -> {
			throw new Exception();
		}).applyAsInt("x", "x")).is(0);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToIntBiFunctionWithException.ignored((x, y) -> 1).applyAsInt("2", "3")).is(1);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToIntBiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}).applyAsInt("x", "x")).is(0);
	}

}
