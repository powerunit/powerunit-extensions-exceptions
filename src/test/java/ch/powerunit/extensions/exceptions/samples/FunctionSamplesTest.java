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
package ch.powerunit.extensions.exceptions.samples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.ExceptionMapper;
import ch.powerunit.extensions.exceptions.FunctionWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class FunctionSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException);

		assertThatFunction(functionThrowingRuntimeException, "x").is("x");

	}

	@Test
	public void sample2() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = FunctionWithException
				.failing(IOException::new);

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.apply(x), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample3() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, Optional<String>> functionWithOptionalResult = FunctionWithException
				.lifted(fonctionThrowingException);

		assertThatFunction(functionWithOptionalResult, "x").is(optionalIs("x"));

	}

	@Test
	public void sample4() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThatFunction(functionThrowingRuntimeException, "x").is("x");

	}

	@Test
	public void sample5() throws FileNotFoundException {
		PrintStream sample = new PrintStream(new File("target/surefire-reports/sample1.txt"));

		FunctionWithException<String, String, IOException> fonctionThrowingException = FunctionWithException
				.failing(IOException::new);

		Function<String, String> functionThrowingRuntime1Exception = FunctionWithException
				.unchecked(fonctionThrowingException);

		Function<String, String> functionThrowingRuntime2Exception = FunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		try {
			fonctionThrowingException.apply("x");
		} catch (IOException e) {
			e.printStackTrace(sample);
		}

		try {
			functionThrowingRuntime1Exception.apply("x");
		} catch (RuntimeException e) {
			e.printStackTrace(sample);
		}

		try {
			functionThrowingRuntime2Exception.apply("x");
		} catch (RuntimeException e) {
			e.printStackTrace(sample);
		}

		sample.flush();
		sample.close();
	}

	@Test
	public void sample6() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> {
			if (x == null) {
				throw new IOException("test");
			}
			return x + "A";
		};

		Function<String, CompletionStage<String>> stage = FunctionWithException.staged(fonctionThrowingException);

		CompletionStage<String> result1 = stage.apply("a").exceptionally(Throwable::getMessage);

		CompletionStage<String> result2 = stage.apply(null).exceptionally(Throwable::getMessage);

		assertThat(result1.toCompletableFuture().join()).is("aA");

		assertThat(result2.toCompletableFuture().join()).is("test");

	}

	@Test
	public void sample7() {

		// Sample with SQLException

		Function<Exception, RuntimeException> mapper = ExceptionMapper.forException(SQLException.class,
				s -> new WrappedException(String.format("%s ; ErrorCode=%s ; SQLState=%s", s.getMessage(),
						s.getErrorCode(), s.getSQLState()), s));

		FunctionWithException<String, String, SQLException> fonctionThrowingException = FunctionWithException
				.failing(SQLException::new);

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, mapper);

		assertWhen(x -> functionThrowingRuntimeException.apply(x), "x").throwException(
				both(exceptionMessage("null ; ErrorCode=0 ; SQLState=null")).and(instanceOf(WrappedException.class)));

	}

	@Test
	public void sample8() {

		// Sample with SQLException

		Function<Exception, RuntimeException> mapper = ExceptionMapper.sqlExceptionMapper();

		FunctionWithException<String, String, SQLException> fonctionThrowingException = FunctionWithException
				.failing(SQLException::new);

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, mapper);

		assertWhen(x -> functionThrowingRuntimeException.apply(x), "x").throwException(
				both(exceptionMessage("null - ErrorCode=0 ; SQLState=null")).and(instanceOf(WrappedException.class)));

	}

}
