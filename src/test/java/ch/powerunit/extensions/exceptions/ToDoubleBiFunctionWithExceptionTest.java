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
public class ToDoubleBiFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToDoubleBiFunctionWithException.failing(Exception::new).applyAsDouble(x, x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToDoubleBiFunctionWithException.unchecked((x, y) -> 12).applyAsDouble("2", "3")).is(12d);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToDoubleBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}).applyAsDouble(x, x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToDoubleBiFunctionWithException.unchecked((x, y) -> 12, RuntimeException::new).applyAsDouble(12, 48))
				.is(12d);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToDoubleBiFunctionWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).applyAsDouble(x, x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToDoubleBiFunctionWithException.lifted((x, y) -> 3).applyAsDouble("2", "3")).is(3d);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToDoubleBiFunctionWithException.lifted((x, y) -> {
			throw new Exception();
		}).applyAsDouble("x", "x")).is(0d);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToDoubleBiFunctionWithException.ignored((x, y) -> 1).applyAsDouble("2", "3")).is(1d);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToDoubleBiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}).applyAsDouble("x", "x")).is(0d);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(ToDoubleBiFunctionWithException.ignored((x, y) -> 1, 2d).applyAsDouble("2", "3")).is(1d);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(ToDoubleBiFunctionWithException.ignored((x, y) -> {
			throw new Exception();
		}, 1d).applyAsDouble("x", "x")).is(1d);
	}

}
