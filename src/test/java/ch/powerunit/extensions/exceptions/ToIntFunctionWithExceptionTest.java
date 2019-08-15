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
public class ToIntFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ToIntFunctionWithException.failing(Exception::new).applyAsInt(x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ToIntFunctionWithException.unchecked(x -> 3).applyAsInt("2")).is(3);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ToIntFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsInt(x)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(ToIntFunctionWithException.unchecked(x -> 3, RuntimeException::new).applyAsInt(2)).is(3);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ToIntFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsInt(x)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ToIntFunctionWithException.lifted(x -> 12).applyAsInt("2")).is(12);
	}

	@Test
	public void testLiftedException() {
		assertThat(ToIntFunctionWithException.lifted(y -> {
			throw new Exception();
		}).applyAsInt("x")).is(0);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ToIntFunctionWithException.ignored(x -> 11).applyAsInt("2")).is(11);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ToIntFunctionWithException.ignored(y -> {
			throw new Exception();
		}).applyAsInt("x")).is(0);
	}

}
