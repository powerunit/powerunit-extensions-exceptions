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
public class IntFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> IntFunctionWithException.failing(Exception::new).apply(1), 1d)
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(IntFunctionWithException.unchecked(x -> "1").apply(2)).is("1");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> IntFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).apply(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> IntFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).apply(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(IntFunctionWithException.lifted(x -> "1").apply(2)).is(optionalIs("1"));
	}

	@Test
	public void testLiftedException() {
		assertThat(IntFunctionWithException.lifted(y -> {
			throw new Exception();
		}).apply(1)).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(IntFunctionWithException.ignored(x -> "1").apply(2)).is("1");
	}

	@Test
	public void testIgnoredException() {
		assertThat(IntFunctionWithException.ignored(y -> {
			throw new Exception();
		}).apply(1)).isNull();
	}

	@Test
	public void testStagedNoException() {
		assertThat(IntFunctionWithException.staged(x -> "1").apply(2).toCompletableFuture().join()).is("1");
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> IntFunctionWithException.staged(y -> {
			throw new Exception();
		}).apply(2).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

}