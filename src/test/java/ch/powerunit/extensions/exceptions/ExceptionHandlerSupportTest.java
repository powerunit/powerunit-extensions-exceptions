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
public class ExceptionHandlerSupportTest implements TestSuite {

	@Test
	public void testExceptionMapper() {
		assertThatFunction(new ExceptionHandlerSupport<Object, Object>() {

			@Override
			public Object uncheck() {
				return null;
			}

			@Override
			public Object lift() {
				return null;
			}

			@Override
			public Object ignore() {
				return null;
			}
		}.exceptionMapper(), new IOException("test"))
				.is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testExceptionMapperForSameException() {
		assertThatFunction(
				ExceptionHandlerSupport.exceptionMapperFor(IOException.class, e -> new WrappedException("testme")),
				new IOException("test")).is(both(exceptionMessage("testme")).and(instanceOf(WrappedException.class)));
	}

	@Test
	public void testExceptionMapperForOtherException() {
		assertThatFunction(
				ExceptionHandlerSupport.exceptionMapperFor(IOException.class, e -> new WrappedException("testme")),
				new SQLException("test")).is(both(exceptionMessage("test")).and(instanceOf(WrappedException.class)));
	}

}
