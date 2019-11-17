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
public class ToLongBiFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToLongBiFunctionWithException.failing(Exception::new).applyAsLong(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToLongBiFunctionWithException.unchecked((x, y) -> 12).applyAsLong("2", "3")).is(12L);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToLongBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}).applyAsLong(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToLongBiFunctionWithException.unchecked((x, y) -> 12, RuntimeException::new).applyAsLong(12, 48))
				.is(12L);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToLongBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).applyAsLong(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToLongBiFunctionWithException.lifted((x, y) -> 3).applyAsLong("2", "3")).is(3L);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToLongBiFunctionWithException.lifted((x, y) -> {
			throw new Exception();
		}).applyAsLong("x", "x")).is(0L);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToLongBiFunctionWithException.ignored((x, y) -> 1).applyAsLong("2", "3")).is(1L);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToLongBiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}).applyAsLong("x", "x")).is(0L);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(ToLongBiFunctionWithException.ignored((x, y) -> 1, 2L).applyAsLong("2", "3")).is(1L);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(ToLongBiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}, 1L).applyAsLong("x", "x")).is(1L);
	}

}
