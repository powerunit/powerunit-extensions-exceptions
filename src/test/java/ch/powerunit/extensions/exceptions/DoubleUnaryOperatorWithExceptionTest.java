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
public class DoubleUnaryOperatorWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> DoubleUnaryOperatorWithException.failing(Exception::new).applyAsDouble(1), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testIdentity() throws Exception {
		assertThat(DoubleUnaryOperatorWithException.identity().applyAsDouble(1)).is(1d);
	}

	@Test
	public void testCompose() throws Exception {
		DoubleUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		DoubleUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.compose(fct2).applyAsDouble(3)).is(2d);
	}

	@Test
	public void testandThen() throws Exception {
		DoubleUnaryOperatorWithException<Exception> fct1 = x -> x * 2;
		DoubleUnaryOperatorWithException<Exception> fct2 = x -> x - 2;
		assertThat(fct1.andThen(fct2).applyAsDouble(1)).is(0d);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(DoubleUnaryOperatorWithException.unchecked(x -> x + 1).applyAsDouble(2)).is(3d);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> DoubleUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}).applyAsDouble(1)).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> DoubleUnaryOperatorWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).applyAsDouble(1)).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(DoubleUnaryOperatorWithException.lifted(x -> x + 1).applyAsDouble(2)).is(3d);
	}

	@Test
	public void testLiftedException() {
		assertThat(DoubleUnaryOperatorWithException.lifted(x -> {
			throw new Exception();
		}).applyAsDouble(1)).is(0d);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(DoubleUnaryOperatorWithException.ignored(x -> x + 1).applyAsDouble(2)).is(3d);
	}

	@Test
	public void testIgnoredException() {
		assertThat(DoubleUnaryOperatorWithException.ignored(x -> {
			throw new Exception();
		}).applyAsDouble(1)).is(0d);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(DoubleUnaryOperatorWithException.ignored(x -> x + 1, 1d).applyAsDouble(2)).is(3d);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(DoubleUnaryOperatorWithException.ignored(x -> {
			throw new Exception();
		}, 1d).applyAsDouble(1)).is(1d);
	}

}
