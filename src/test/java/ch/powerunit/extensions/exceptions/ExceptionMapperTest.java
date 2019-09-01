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

import java.io.IOException;
import java.sql.SQLException;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class ExceptionMapperTest implements TestSuite {

	@Test
	public void testForExceptionSameException() {
		assertThatFunction(ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme")),
				new IOException("test")).is(both(exceptionMessage("testme")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionMapperForOtherException() {
		assertThatFunction(ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme")),
				new SQLException("test")).is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions2SameExceptionFirst() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2"))),
				new IOException("test")).is(both(exceptionMessage("testme1")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions2SameExceptionSecond() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2"))),
				new IllegalArgumentException("test"))
						.is(both(exceptionMessage("testme2")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions2MapperForOtherException() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2"))),
				new SQLException("test")).is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions3SameExceptionFirst() {
		assertThatFunction(
				ExceptionMapper.forExceptions(
						ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
						ExceptionMapper.forException(IllegalArgumentException.class,
								e -> new WrappedException("testme2")),
						ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3"))),
				new IOException("test")).is(both(exceptionMessage("testme1")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions3SameExceptionSecond() {
		assertThatFunction(
				ExceptionMapper.forExceptions(
						ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
						ExceptionMapper.forException(IllegalArgumentException.class,
								e -> new WrappedException("testme2")),
						ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3"))),
				new IllegalArgumentException("test"))
						.is(both(exceptionMessage("testme2")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions3SameExceptionThird() {
		assertThatFunction(
				ExceptionMapper.forExceptions(
						ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
						ExceptionMapper.forException(IllegalArgumentException.class,
								e -> new WrappedException("testme2")),
						ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3"))),
				new NullPointerException("test"))
						.is(both(exceptionMessage("testme3")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptions3MapperForOtherException() {
		assertThatFunction(
				ExceptionMapper.forExceptions(
						ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
						ExceptionMapper.forException(IllegalArgumentException.class,
								e -> new WrappedException("testme2")),
						ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3"))),
				new SQLException("test")).is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionsAnySameExceptionFirst() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2")),
				ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3")),
				ExceptionMapper.forException(IllegalStateException.class, e -> new WrappedException("testme4"))),
				new IOException("test")).is(both(exceptionMessage("testme1")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionsAnySameExceptionSecond() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2")),
				ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3")),
				ExceptionMapper.forException(IllegalStateException.class, e -> new WrappedException("testme4"))),
				new IllegalArgumentException("test"))
						.is(both(exceptionMessage("testme2")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionsAnySameExceptionThird() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2")),
				ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3")),
				ExceptionMapper.forException(IllegalStateException.class, e -> new WrappedException("testme4"))),
				new NullPointerException("test"))
						.is(both(exceptionMessage("testme3")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionsAnySameExceptionLast() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2")),
				ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3")),
				ExceptionMapper.forException(IllegalStateException.class, e -> new WrappedException("testme4"))),
				new IllegalStateException("test"))
						.is(both(exceptionMessage("testme4")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testForExceptionsAnyMapperForOtherException() {
		assertThatFunction(ExceptionMapper.forExceptions(
				ExceptionMapper.forException(IOException.class, e -> new WrappedException("testme1")),
				ExceptionMapper.forException(IllegalArgumentException.class, e -> new WrappedException("testme2")),
				ExceptionMapper.forException(NullPointerException.class, e -> new WrappedException("testme3")),
				ExceptionMapper.forException(IllegalStateException.class, e -> new WrappedException("testme4"))),
				new SQLException("test")).is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testJaxbException() {
		ExceptionMapper.jaxbExceptionMapper();
	}

	@Test
	public void testsaxException() {
		ExceptionMapper.saxExceptionMapper();
	}

}
