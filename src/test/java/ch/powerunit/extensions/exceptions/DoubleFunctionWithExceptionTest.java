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
public class DoubleFunctionWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> DoubleFunctionWithException.failing(Exception::new).apply(1), 1d)
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(DoubleFunctionWithException.unchecked(x -> "1").apply(2)).is("1");
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> DoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}).apply(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> DoubleFunctionWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).apply(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(DoubleFunctionWithException.lifted(x -> "1").apply(2d)).is(optionalIs("1"));
	}

	@Test
	public void testLiftedException() {
		assertThat(DoubleFunctionWithException.lifted(y -> {
			throw new Exception();
		}).apply(1d)).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(DoubleFunctionWithException.ignored(x -> "1").apply(2d)).is("1");
	}

	@Test
	public void testIgnoredException() {
		assertThat(DoubleFunctionWithException.ignored(y -> {
			throw new Exception();
		}).apply(1)).isNull();
	}

	@Test
	public void testStagedNoException() {
		assertThat(DoubleFunctionWithException.staged(x -> "1").apply(2).toCompletableFuture().join()).is("1");
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> DoubleFunctionWithException.staged(y -> {
			throw new Exception();
		}).apply(2).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(DoubleFunctionWithException.ignored(x -> "1", "x").apply(2d)).is("1");
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(DoubleFunctionWithException.ignored(y -> {
			throw new Exception();
		}, "x").apply(1)).is("x");
	}

}
