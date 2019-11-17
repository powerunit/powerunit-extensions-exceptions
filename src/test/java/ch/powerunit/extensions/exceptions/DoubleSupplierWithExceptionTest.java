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
public class DoubleSupplierWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> DoubleSupplierWithException.failing(Exception::new).getAsDouble())
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(DoubleSupplierWithException.unchecked(() -> 1).getAsDouble()).is(1d);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> DoubleSupplierWithException.unchecked(() -> {
			throw new Exception();
		}).getAsDouble()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> DoubleSupplierWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).getAsDouble()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(DoubleSupplierWithException.lifted(() -> 1d).getAsDouble()).is(1d);
	}

	@Test
	public void testLiftedException() {
		assertThat(DoubleSupplierWithException.lifted(() -> {
			throw new Exception();
		}).getAsDouble()).is(0d);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(DoubleSupplierWithException.ignored(() -> 2).getAsDouble()).is(2d);
	}

	@Test
	public void testIgnoredException() {
		assertThat(DoubleSupplierWithException.ignored(() -> {
			throw new Exception();
		}).getAsDouble()).is(0d);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(DoubleSupplierWithException.ignored(() -> 2, 1d).getAsDouble()).is(2d);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(DoubleSupplierWithException.ignored(() -> {
			throw new Exception();
		}, 1d).getAsDouble()).is(1d);
	}

}
