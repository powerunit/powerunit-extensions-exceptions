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
public class LongUnaryOperatorWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> LongUnaryOperatorWithException.failing(Exception::new).applyAsLong(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testIdentity() throws Exception {
		assertThat(LongUnaryOperatorWithException.identity().applyAsLong(1)).is(1L);
	}

	@Test
	public void testCompose() throws Exception {
		LongUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		LongUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.compose(fct2).applyAsLong(3)).is(2L);
	}

	@Test
	public void testandThen() throws Exception {
		LongUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		LongUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.andThen(fct2).applyAsLong(1)).is(0L);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(LongUnaryOperatorWithException.unchecked(x -> x + 1).applyAsLong(2)).is(3L);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> LongUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsLong(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> LongUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsLong(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(LongUnaryOperatorWithException.lifted(x -> x + 1).applyAsLong(2)).is(3L);
	}

	@Test
	public void testLiftedException() {
		assertThat(LongUnaryOperatorWithException.lifted(x -> {
			throw new Exception();
		}).applyAsLong(1)).is(0L);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(LongUnaryOperatorWithException.ignored(x -> x + 1).applyAsLong(2)).is(3L);
	}

	@Test
	public void testIgnoredException() {
		assertThat(LongUnaryOperatorWithException.ignored(x -> {
			throw new Exception();
		}).applyAsLong(1)).is(0L);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		LongUnaryOperatorWithException.consumer(x -> x + 1).accept(2L);
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> LongUnaryOperatorWithException.consumer(y -> {
			throw new Exception();
		}).accept(2L)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsSupplierNoException() throws Exception {
		LongUnaryOperatorWithException<Exception> fct = x -> x + 1;
		assertThat(fct.asSupplier(2).get()).is(3L);
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> LongUnaryOperatorWithException.failing(Exception::new).asSupplier(1).get())
				.throwException(instanceOf(Exception.class));
	}

}
