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
package ch.powerunittest.samples;

import java.util.function.Function;

import java.io.IOException;
import java.sql.SQLException;

import ch.powerunit.extensions.exceptions.ExceptionHandlerSupport;
import ch.powerunit.extensions.exceptions.ExceptionMapper;
import ch.powerunit.extensions.exceptions.FunctionWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

public class FunctionSamplesTest {

	public static void sample1() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException);

		if (!"x".equals(functionThrowingRuntimeException.apply("x"))) {
			throw new IllegalArgumentException("The result is not correct");
		}

	}

	public static void sample2() {

		Function<Exception, RuntimeException> mapper = ExceptionMapper.SQL_EXCEPTION_MAPPER;

		FunctionWithException<String, String, SQLException> fonctionThrowingException = FunctionWithException
				.failing(SQLException::new);

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, mapper);

		try {
			functionThrowingRuntimeException.apply("x");
		} catch (WrappedException e) {
			if ("null - ErrorCode=0 ; SQLState=null".equals(e.getMessage())) {
				return;
			}
			throw new IllegalArgumentException("Wrong exception : " + e.getMessage(), e);

		}
		throw new IllegalArgumentException("No exception thrown");

	}

	public static void main(String[] args) {
		sample1();
		sample2();
	}

}