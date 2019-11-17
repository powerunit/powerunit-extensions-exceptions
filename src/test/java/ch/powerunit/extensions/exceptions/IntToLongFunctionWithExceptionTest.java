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
public class IntToLongFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> IntToLongFunctionWithException.failing(Exception::new).applyAsLong(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(IntToLongFunctionWithException.unchecked(x -> 1).applyAsLong(2)).is(1L);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> IntToLongFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsLong(2)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> IntToLongFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsLong(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(IntToLongFunctionWithException.lifted(x -> 1).applyAsLong(2)).is(1L);
	}

	@Test
	public void testLiftedException() {
		assertThat(IntToLongFunctionWithException.lifted(y -> {
			throw new Exception();
		}).applyAsLong(1)).is(0L);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(IntToLongFunctionWithException.ignored(x -> 1).applyAsLong(2)).is(1L);
	}

	@Test
	public void testIgnoredException() {
		assertThat(IntToLongFunctionWithException.ignored(y -> {
			throw new Exception();
		}).applyAsLong(1)).is(0L);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(IntToLongFunctionWithException.ignored(x -> 1, 2L).applyAsLong(2)).is(1L);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(IntToLongFunctionWithException.ignored(y -> {
			throw new Exception();
		}, 1L).applyAsLong(1)).is(1L);
	}

}
