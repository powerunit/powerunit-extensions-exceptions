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
public class BooleanSupplierWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> BooleanSupplierWithException.failing(Exception::new).getAsBoolean())
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(BooleanSupplierWithException.unchecked(() -> true).getAsBoolean()).is(true);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> BooleanSupplierWithException.unchecked(() -> {
			throw new Exception();
		}).getAsBoolean()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> BooleanSupplierWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).getAsBoolean()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(BooleanSupplierWithException.lifted(() -> true).getAsBoolean()).is(true);
	}

	@Test
	public void testLiftedException() {
		assertThat(BooleanSupplierWithException.lifted(() -> {
			throw new Exception();
		}).getAsBoolean()).is(false);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(BooleanSupplierWithException.ignored(() -> true).getAsBoolean()).is(true);
	}

	@Test
	public void testIgnoredException() {
		assertThat(BooleanSupplierWithException.ignored(() -> {
			throw new Exception();
		}).getAsBoolean()).is(false);
	}

	@Test
	public void testAsFunctionNoException() throws Exception {
		assertThat(BooleanSupplierWithException.function(() -> true).apply("1")).is(true);
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> BooleanSupplierWithException.function(() -> {
			throw new Exception();
		}).apply("1")).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsSupplierNoException() throws Exception {
		assertThat(BooleanSupplierWithException.supplier(() -> true).get()).is(true);
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> BooleanSupplierWithException.supplier(() -> {
			throw new Exception();
		}).get()).throwException(instanceOf(Exception.class));
	}

}
