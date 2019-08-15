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
public class ObjDoubleConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ObjDoubleConsumerWithException.failing(Exception::new).accept(x, 12), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		ObjDoubleConsumerWithException.unchecked((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ObjDoubleConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}).accept(x, 12)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ObjDoubleConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).accept(x, 12)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		ObjDoubleConsumerWithException.lifted((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testLiftedException() {
		ObjDoubleConsumerWithException.lifted((x, y) -> {
			throw new Exception();
		}).accept("2", 12);
	}

	@Test
	public void testIgnoredNoException() {
		ObjDoubleConsumerWithException.ignored((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testIgnoredException() {
		ObjDoubleConsumerWithException.ignored((x, y) -> {
			throw new Exception();
		}).accept("2", 12);
	}

}
