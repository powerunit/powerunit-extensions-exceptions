/**
 * Powerunit - A JDK1.8 matches framework
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

import java.nio.file.Paths;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a matches
public class PathMatcherWithExceptionTest implements TestSuite {

	@Test
	public void matchesFailing() {
		assertWhen((x) -> PathMatcherWithException.failing(Exception::new).matches(x), Paths.get("."))
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void matchesAnd1() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> true;
		PathMatcherWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesAnd2() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> false;
		PathMatcherWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesAnd3() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> false;
		PathMatcherWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesAnd4() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> true;
		PathMatcherWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesOr1() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> true;
		PathMatcherWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesOr2() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> true;
		PathMatcherWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesOr3() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> false;
		PathMatcherWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesOr4() throws Exception {
		PathMatcherWithException<Exception> fct1 = x -> false;
		PathMatcherWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesNegate1() throws Exception {
		assertThat(PathMatcherWithException.negate(x -> true).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesNegate2() throws Exception {
		assertThat(PathMatcherWithException.negate(x -> false).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesCheckedNoException() {
		assertThat(PathMatcherWithException.unchecked(x -> true).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesCheckedException() {
		assertWhen((x) -> PathMatcherWithException.unchecked(y -> {
			throw new Exception();
		}).matches(Paths.get("."))).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void matchesCheckedNoExceptionHandler() {
		assertThat(PathMatcherWithException.unchecked(x -> true, RuntimeException::new).matches(Paths.get(".")))
				.is(true);
	}

	@Test
	public void matchesCheckedExceptionHandler() {
		assertWhen((x) -> PathMatcherWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).matches(Paths.get("."))).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void matchesLiftedNoException() {
		assertThat(PathMatcherWithException.lifted(x -> true).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesLiftedException() {
		assertThat(PathMatcherWithException.lifted(y -> {
			throw new Exception();
		}).matches(Paths.get("."))).is(false);
	}

	@Test
	public void matchesIgnoredNoException() {
		assertThat(PathMatcherWithException.ignored(x -> true).matches(Paths.get("."))).is(true);
	}

	@Test
	public void matchesIgnoredException() {
		assertThat(PathMatcherWithException.ignored(y -> {
			throw new Exception();
		}).matches(Paths.get("."))).is(false);
	}

}
