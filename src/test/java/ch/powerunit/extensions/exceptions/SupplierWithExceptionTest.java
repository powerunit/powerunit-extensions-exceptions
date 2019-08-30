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

import java.util.concurrent.CompletionException;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class SupplierWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> SupplierWithException.failing(Exception::new).get())
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(SupplierWithException.unchecked(() -> "1").get()).is("1");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> SupplierWithException.unchecked(() -> {
			throw new Exception();
		}).get()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> SupplierWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).get()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(SupplierWithException.lifted(() -> "1").get()).is(optionalIs("1"));
	}

	@Test
	public void testLiftedException() {
		assertThat(SupplierWithException.lifted(() -> {
			throw new Exception();
		}).get()).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(SupplierWithException.ignored(() -> "1").get()).is("1");
	}

	@Test
	public void testIgnoredException() {
		assertThat(SupplierWithException.ignored(() -> {
			throw new Exception();
		}).get()).isNull();
	}

	@Test
	public void testStagedNoException() {
		assertThat(SupplierWithException.staged(() -> "1").get().toCompletableFuture().join()).is("1");
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> SupplierWithException.staged(() -> {
			throw new Exception();
		}).get().toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

	@Test
	public void testAsFunctionNoException() throws Exception {
		assertThat(SupplierWithException.asFunction(() -> 2).apply("2")).is(2);
	}

	@Test
	public void testAsFunctionException() {
		assertWhen((x) -> SupplierWithException.asFunction(() -> {
			throw new Exception();
		}).apply("2")).throwException(instanceOf(Exception.class));
	}

}
