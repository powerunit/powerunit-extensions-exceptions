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
public class RunnableWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> RunnableWithException.failing(Exception::new).run(), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testandThen1() throws Exception {
		RunnableWithException<Exception> fct1 = () -> {
		};
		RunnableWithException<Exception> fct2 = () -> {
			throw new Exception();
		};
		assertWhen((x) -> fct1.andThen(fct2).run()).throwException(instanceOf(Exception.class));
	}
	
	@Test
	public void testandThen2() throws Exception {
		RunnableWithException<Exception> fct1 = () -> {
		};
		RunnableWithException<Exception> fct2 = () -> {
		};
		fct1.andThen(fct2).run();
	}

	@Test
	public void testCheckedNoException() {
		RunnableWithException.unchecked(() -> {
		}).run();
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> RunnableWithException.unchecked(() -> {
			throw new Exception();
		}).run()).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> RunnableWithException.unchecked(() -> {
			throw new Exception();
		}, RuntimeException::new).run()).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		RunnableWithException.lifted(() -> {
		}).run();
	}

	@Test
	public void testLiftedException() {
		RunnableWithException.lifted(() -> {
			throw new Exception();
		}).run();
	}

	@Test
	public void testIgnoredNoException() {
		RunnableWithException.ignored(() -> {
		}).run();
	}

	@Test
	public void testIgnoredException() {
		RunnableWithException.ignored(() -> {
			throw new Exception();
		}).run();
	}

	@Test
	public void testStagedNoException() {
		RunnableWithException.staged(() -> {
		}).get().toCompletableFuture().join();
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> RunnableWithException.staged(() -> {
			throw new Exception();
		}).get().toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}
}
