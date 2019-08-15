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
public class LongSupplierWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> LongSupplierWithException.failing(Exception::new).getAsLong())
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(LongSupplierWithException.unchecked(() -> 1).getAsLong()).is(1L);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> LongSupplierWithException.unchecked(() -> {
			throw new Exception();
		}).getAsLong()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> LongSupplierWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).getAsLong()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(LongSupplierWithException.lifted(() -> 1).getAsLong()).is(1L);
	}

	@Test
	public void testLiftedException() {
		assertThat(LongSupplierWithException.lifted(() -> {
			throw new Exception();
		}).getAsLong()).is(0L);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(LongSupplierWithException.ignored(() -> 2).getAsLong()).is(2L);
	}

	@Test
	public void testIgnoredException() {
		assertThat(LongSupplierWithException.ignored(() -> {
			throw new Exception();
		}).getAsLong()).is(0L);
	}

}
