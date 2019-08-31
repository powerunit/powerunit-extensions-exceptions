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

import java.io.ObjectInputFilter.Status;
import java.util.concurrent.CompletionException;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class ObjectInputFilterWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ObjectInputFilterWithException.failing(Exception::new).checkInput(null))
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		assertThat(ObjectInputFilterWithException.unchecked(x -> Status.ALLOWED).checkInput(null)).is(Status.ALLOWED);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ObjectInputFilterWithException.unchecked(y -> {
			throw new Exception();
		}).checkInput(null)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ObjectInputFilterWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).checkInput(null)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testCheckedExceptionHandlerNoException() {
		assertThat(
				ObjectInputFilterWithException.unchecked(x -> Status.ALLOWED, RuntimeException::new).checkInput(null))
						.is(Status.ALLOWED);
	}

	@Test
	public void testLiftedNoException() {
		assertThat(ObjectInputFilterWithException.lifted(x -> Status.ALLOWED).apply(null))
				.is(optionalIs(Status.ALLOWED));
	}

	@Test
	public void testLiftedException() {
		assertThat(ObjectInputFilterWithException.lifted(y -> {
			throw new Exception();
		}).apply(null)).is(optionalIsNotPresent());
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(ObjectInputFilterWithException.ignored(x -> Status.ALLOWED).checkInput(null)).is(Status.ALLOWED);
	}

	@Test
	public void testIgnoredException() {
		assertThat(ObjectInputFilterWithException.ignored(y -> {
			throw new Exception();
		}).checkInput(null)).is(Status.UNDECIDED);
	}

	@Test
	public void testStagedNoException() {
		assertThat(ObjectInputFilterWithException.staged(x -> Status.ALLOWED).apply(null).toCompletableFuture().join())
				.is(Status.ALLOWED);
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> ObjectInputFilterWithException.staged(y -> {
			throw new Exception();
		}).apply(null).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}
}
