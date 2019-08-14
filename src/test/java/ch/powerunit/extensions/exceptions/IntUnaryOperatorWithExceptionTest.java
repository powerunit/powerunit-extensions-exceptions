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
public class IntUnaryOperatorWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> IntUnaryOperatorWithException.failing(Exception::new).applyAsInt(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testIdentity() throws Exception {
		assertThat(IntUnaryOperatorWithException.identity().applyAsInt(1)).is(1);
	}

	@Test
	public void testCompose() throws Exception {
		IntUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		IntUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.compose(fct2).applyAsInt(3)).is(2);
	}

	@Test
	public void testandThen() throws Exception {
		IntUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		IntUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.andThen(fct2).applyAsInt(1)).is(0);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(IntUnaryOperatorWithException.unchecked(x -> x + 1).applyAsInt(2)).is(3);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> IntUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsInt(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> IntUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsInt(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(IntUnaryOperatorWithException.lifted(x -> x + 1).applyAsInt(2)).is(3);
	}

	@Test
	public void testLiftedException() {
		assertThat(IntUnaryOperatorWithException.lifted(x -> {
			throw new Exception();
		}).applyAsInt(1)).is(0);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(IntUnaryOperatorWithException.ignored(x -> x + 1).applyAsInt(2)).is(3);
	}

	@Test
	public void testIgnoredException() {
		assertThat(IntUnaryOperatorWithException.ignored(x -> {
			throw new Exception();
		}).applyAsInt(1)).is(0);
	}

	@Test
	public void testAsConsumerNoException() throws Exception {
		IntUnaryOperatorWithException.consumer(x -> x + 1).accept(2);
	}

	@Test
	public void testAsConsumerException() {
		assertWhen((x) -> IntUnaryOperatorWithException.consumer(y -> {
			throw new Exception();
		}).accept(2)).throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAsSupplierNoException() throws Exception {
		IntUnaryOperatorWithException<Exception> fct = x -> x + 1;
		assertThat(fct.asSupplier(2).get()).is(3);
	}

	@Test
	public void testAsSupplierException() {
		assertWhen((x) -> IntUnaryOperatorWithException.failing(Exception::new).asSupplier(1).get())
				.throwException(instanceOf(Exception.class));
	}

}
