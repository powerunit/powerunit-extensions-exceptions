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

import java.io.File;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class FilenameFilterWithExceptionTest implements TestSuite {

	@Test
	public void testFailing() {
		assertWhen((x) -> FilenameFilterWithException.failing(Exception::new).accept(new File("."), x), "x")
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void testAnd1() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> true;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> false;
		assertThat(fct1.and(fct2).accept(new File("."), "4")).is(false);
	}

	@Test
	public void testAnd2() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> false;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> true;
		assertThat(fct1.and(fct2).accept(new File("."), "4")).is(false);
	}

	@Test
	public void testAnd3() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> false;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> false;
		assertThat(fct1.and(fct2).accept(new File("."), "4")).is(false);
	}

	@Test
	public void testAnd4() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> true;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> true;
		assertThat(fct1.and(fct2).accept(new File("."), "4")).is(true);
	}

	@Test
	public void testOr1() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> true;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> false;
		assertThat(fct1.or(fct2).accept(new File("."), "4")).is(true);
	}

	@Test
	public void testOr2() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> false;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> true;
		assertThat(fct1.or(fct2).accept(new File("."), "4")).is(true);
	}

	@Test
	public void testOr3() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> true;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> true;
		assertThat(fct1.or(fct2).accept(new File("."), "4")).is(true);
	}

	@Test
	public void testOr4() throws Exception {
		FilenameFilterWithException<Exception> fct1 = (x, y) -> false;
		FilenameFilterWithException<Exception> fct2 = (x, y) -> false;
		assertThat(fct1.or(fct2).accept(new File("."), "4")).is(false);
	}

	@Test
	public void testNegate1() throws Exception {
		assertThat(FilenameFilterWithException.negate((x, y) -> true).accept(new File("."), "4")).is(false);
	}

	@Test
	public void testNegate2() throws Exception {
		assertThat(FilenameFilterWithException.negate((x, y) -> false).accept(new File("."), "4")).is(true);
	}

	@Test
	public void testCheckedNoException() {
		assertThat(FilenameFilterWithException.unchecked((x, y) -> true).accept(new File("."), "3")).is(true);
	}

	@Test
	public void testCheckedException() {
		assertWhen((x) -> FilenameFilterWithException.unchecked((y, z) -> {
			throw new Exception();
		}).accept(new File("."), "x")).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void testCheckedNoExceptionHandler() {
		assertThat(
				FilenameFilterWithException.unchecked((x, y) -> true, RuntimeException::new).accept(new File("."), "x"))
						.is(true);
	}

	@Test
	public void testCheckedExceptionHandler() {
		assertWhen((x) -> FilenameFilterWithException.unchecked((y, z) -> {
			throw new Exception();
		}, RuntimeException::new).accept(new File("."), "x")).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void testLiftedNoException() {
		assertThat(FilenameFilterWithException.lifted((x, y) -> true).accept(new File("."), "3")).is(true);
	}

	@Test
	public void testLiftedException() {
		assertThat(FilenameFilterWithException.lifted((x, y) -> {
			throw new Exception();
		}).accept(new File("."), "x")).is(false);
	}

	@Test
	public void testIgnoredNoException() {
		assertThat(FilenameFilterWithException.ignored((x, y) -> true).accept(new File("."), "3")).is(true);
	}

	@Test
	public void testIgnoredException() {
		assertThat(FilenameFilterWithException.ignored((x, y) -> {
			throw new Exception();
		}).accept(new File("."), "x")).is(false);
	}

	@Test
	public void testIgnoredDefaultNoException() {
		assertThat(FilenameFilterWithException.ignored((x, y) -> false, true).accept(new File("."), "3")).is(false);
	}

	@Test
	public void testIgnoredDefaultException() {
		assertThat(FilenameFilterWithException.ignored((x, y) -> {
			throw new Exception();
		}, true).accept(new File("."), "x")).is(true);
	}

}
