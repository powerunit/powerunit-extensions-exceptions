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
public class IntSupplierWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> IntSupplierWithException.failing(Exception::new).getAsInt())
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(IntSupplierWithException.unchecked(() -> 1).getAsInt()).is(1);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> IntSupplierWithException.unchecked(() -> {
			throw new Exception();
		}).getAsInt()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> IntSupplierWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).getAsInt()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(IntSupplierWithException.lifted(() -> 1).getAsInt()).is(1);
	}

	@Test
	public void testLiftedException() {
		assertThat(IntSupplierWithException.lifted(() -> {
			throw new Exception();
		}).getAsInt()).is(0);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(IntSupplierWithException.ignored(() -> 2).getAsInt()).is(2);
	}

	@Test
	public void testIgnoredException() {
		assertThat(IntSupplierWithException.ignored(() -> {
			throw new Exception();
		}).getAsInt()).is(0);
	}

}
