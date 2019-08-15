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
public class LongToDoubleFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> LongToDoubleFunctionWithException.failing(Exception::new).applyAsDouble(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(LongToDoubleFunctionWithException.unchecked(x -> 1).applyAsDouble(2)).is(1d);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> LongToDoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsDouble(2)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> LongToDoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsDouble(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(LongToDoubleFunctionWithException.lifted(x -> 1).applyAsDouble(2)).is(1d);
	}

	@Test
	public void testLiftedException() {
		assertThat(LongToDoubleFunctionWithException.lifted(y -> {
			throw new Exception();
		}).applyAsDouble(1)).is(0d);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(LongToDoubleFunctionWithException.ignored(x -> 1).applyAsDouble(2)).is(1d);
	}

	@Test
	public void testIgnoredException() {
		assertThat(LongToDoubleFunctionWithException.ignored(y -> {
			throw new Exception();
		}).applyAsDouble(1)).is(0d);
	}

}
