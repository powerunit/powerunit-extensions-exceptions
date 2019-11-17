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
public class ToDoubleFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToDoubleFunctionWithException.failing(Exception::new).applyAsDouble(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToDoubleFunctionWithException.unchecked(x -> 3).applyAsDouble("2")).is(3d);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToDoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsDouble(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToDoubleFunctionWithException.unchecked(x -> 3, RuntimeException::new).applyAsDouble(2)).is(3d);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToDoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsDouble(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToDoubleFunctionWithException.lifted(x -> 12).applyAsDouble("2")).is(12d);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToDoubleFunctionWithException.lifted(y -> {
			throw new Exception();
		}).applyAsDouble("x")).is(0d);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToDoubleFunctionWithException.ignored(x -> 11).applyAsDouble("2")).is(11d);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToDoubleFunctionWithException.ignored(y -> {
			throw new Exception();
		}).applyAsDouble("x")).is(0d);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(ToDoubleFunctionWithException.ignored(x -> 11, 1d).applyAsDouble("2")).is(11d);
	}

	@Test
	public void testIgnoredDoubleException() {
		assertThat(ToDoubleFunctionWithException.ignored(y -> {
			throw new Exception();
		}, 1d).applyAsDouble("x")).is(1d);
	}

}
