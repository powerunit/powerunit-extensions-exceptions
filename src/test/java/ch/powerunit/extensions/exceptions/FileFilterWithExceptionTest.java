/**
 * Powerunit - A JDK1.8 accept framework
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

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a accept
public class FileFilterWithExceptionTest implements TestSuite {

	@Test
	public void acceptFailing() {
		assertWhen((x) -> FileFilterWithException.failing(Exception::new).accept(x), new File("."))
				.throwException(instanceOf(Exception.class));
	}

	@Test
	public void acceptAnd1() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> true;
		FileFilterWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).accept(new File("."))).is(false);
	}

	@Test
	public void acceptAnd2() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> false;
		FileFilterWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).accept(new File("."))).is(false);
	}

	@Test
	public void acceptAnd3() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> false;
		FileFilterWithException<Exception> fct2 = x -> false;
		assertThat(fct1.and(fct2).accept(new File("."))).is(false);
	}

	@Test
	public void acceptAnd4() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> true;
		FileFilterWithException<Exception> fct2 = x -> true;
		assertThat(fct1.and(fct2).accept(new File("."))).is(true);
	}

	@Test
	public void acceptOr1() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> true;
		FileFilterWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).accept(new File("."))).is(true);
	}

	@Test
	public void acceptOr2() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> true;
		FileFilterWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).accept(new File("."))).is(true);
	}

	@Test
	public void acceptOr3() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> false;
		FileFilterWithException<Exception> fct2 = x -> true;
		assertThat(fct1.or(fct2).accept(new File("."))).is(true);
	}

	@Test
	public void acceptOr4() throws Exception {
		FileFilterWithException<Exception> fct1 = x -> false;
		FileFilterWithException<Exception> fct2 = x -> false;
		assertThat(fct1.or(fct2).accept(new File("."))).is(false);
	}

	@Test
	public void acceptNegate1() throws Exception {
		assertThat(FileFilterWithException.negate(x -> true).accept(new File("."))).is(false);
	}

	@Test
	public void acceptNegate2() throws Exception {
		assertThat(FileFilterWithException.negate(x -> false).accept(new File("."))).is(true);
	}

	@Test
	public void acceptCheckedNoException() {
		assertThat(FileFilterWithException.unchecked(x -> true).accept(new File("."))).is(true);
	}

	@Test
	public void acceptCheckedException() {
		assertWhen((x) -> FileFilterWithException.unchecked(y -> {
			throw new Exception();
		}).accept(new File("."))).throwException(instanceOf(WrappedException.class));
	}

	@Test
	public void acceptCheckedNoExceptionHandler() {
		assertThat(FileFilterWithException.unchecked(x -> true, RuntimeException::new).accept(new File("."))).is(true);
	}

	@Test
	public void acceptCheckedExceptionHandler() {
		assertWhen((x) -> FileFilterWithException.unchecked(y -> {
			throw new Exception();
		}, RuntimeException::new).accept(new File("."))).throwException(instanceOf(RuntimeException.class));
	}

	@Test
	public void acceptLiftedNoException() {
		assertThat(FileFilterWithException.lifted(x -> true).accept(new File("."))).is(true);
	}

	@Test
	public void acceptLiftedException() {
		assertThat(FileFilterWithException.lifted(y -> {
			throw new Exception();
		}).accept(new File("."))).is(false);
	}

	@Test
	public void acceptIgnoredNoException() {
		assertThat(FileFilterWithException.ignored(x -> true).accept(new File("."))).is(true);
	}

	@Test
	public void acceptIgnoredException() {
		assertThat(FileFilterWithException.ignored(y -> {
			throw new Exception();
		}).accept(new File("."))).is(false);
	}

	@Test
	public void acceptIgnoredDefaultNoException() {
		assertThat(FileFilterWithException.ignored(x -> false, true).accept(new File("."))).is(false);
	}

	@Test
	public void acceptIgnoredDefaultException() {
		assertThat(FileFilterWithException.ignored(y -> {
			throw new Exception();
		}, true).accept(new File("."))).is(true);
	}

}
