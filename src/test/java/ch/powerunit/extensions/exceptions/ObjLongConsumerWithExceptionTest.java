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
public class ObjLongConsumerWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> ObjLongConsumerWithException.failing(Exception::new).accept(x, 12), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testCheckedNoException() {
		ObjLongConsumerWithException.unchecked((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> ObjLongConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}).accept(x, 12)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> ObjLongConsumerWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).accept(x, 12)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		ObjLongConsumerWithException.lifted((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testLiftedException() {
		ObjLongConsumerWithException.lifted((x, y) -> {
			throw new Exception();
		}).accept("2", 12);
	}

	@Test
	public void testIgnoredNoException() {
		ObjLongConsumerWithException.ignored((x, y) -> {
		}).accept("2", 12);
	}

	@Test
	public void testIgnoredException() {
		ObjLongConsumerWithException.ignored((x, y) -> {
			throw new Exception();
		}).accept("2", 12);
	}

	@Test
	public void testAsBiConsumerNoException() throws Exception {
		ObjLongConsumerWithException.asBiConsumer((x, y) -> {
		}).accept("2", 2L);
	}

	@Test
	public void testAsBiConsumerException() {
		assertWhen((x) -> ObjLongConsumerWithException.asBiConsumer((y, z) -> {
			throw new Exception();
		}).accept("2", 3L)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testStagedNoException() {
		ObjLongConsumerWithException.staged((x, y) -> {
		}).apply("x", 1L).toCompletableFuture().join();
	}

	@Test
	public void testStagedException() {
		assertWhen((x) -> ObjLongConsumerWithException.staged((y, z) -> {
			throw new Exception();
		}).apply("x", 1L).toCompletableFuture().join()).throwException(instanceOf(CompletionException.class));
	}

}
