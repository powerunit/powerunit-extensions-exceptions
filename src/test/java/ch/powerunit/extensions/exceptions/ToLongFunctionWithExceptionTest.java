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
public class ToLongFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToLongFunctionWithException.failing(Exception::new).applyAsLong(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToLongFunctionWithException.unchecked(x -> 3).applyAsLong("2")).is(3L);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToLongFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsLong(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToLongFunctionWithException.unchecked(x -> 3, RuntimeException::new).applyAsLong(2)).is(3L);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToLongFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsLong(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToLongFunctionWithException.lifted(x -> 12).applyAsLong("2")).is(12L);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToLongFunctionWithException.lifted(y -> {
			throw new Exception();
		}).applyAsLong("x")).is(0L);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToLongFunctionWithException.ignored(x -> 11).applyAsLong("2")).is(11L);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToLongFunctionWithException.ignored(y -> {
			throw new Exception();
		}).applyAsLong("x")).is(0L);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(ToLongFunctionWithException.ignored(x -> 11, 1L).applyAsLong("2")).is(11L);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(ToLongFunctionWithException.ignored(y -> {
			throw new Exception();
		}, 1L).applyAsLong("x")).is(1L);
	}

}
