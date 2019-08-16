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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.BiFunctionWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class BiFunctionSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		BiFunctionWithException<String, String, String, IOException> fonctionThrowingException = String::concat;

		BiFunction<String, String, String> functionThrowingRuntimeException = BiFunctionWithException
				.unchecked(fonctionThrowingException);

		assertThatBiFunction(functionThrowingRuntimeException, "x", "y").is("xy");

	}

	@Test
	public void sample2() {

		BiFunctionWithException<String, String, String, IOException> fonctionThrowingException = (x, y) -> {
			throw new IOException();
		};

		BiFunction<String, String, String> functionThrowingRuntimeException = BiFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.apply(x, x), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample3() {

		BiFunctionWithException<String, String, String, IOException> fonctionThrowingException = String::concat;

		BiFunction<String, String, Optional<String>> functionWithOptionalResult = BiFunctionWithException
				.lifted(fonctionThrowingException);

		assertThatBiFunction(functionWithOptionalResult, "x", "z").is(optionalIs("xz"));

	}

	@Test
	public void sample4() {

		BiFunctionWithException<String, String, String, IOException> fonctionThrowingException = String::concat;

		BiFunction<String, String, String> functionThrowingRuntimeException = BiFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThatBiFunction(functionThrowingRuntimeException, "x", "y").is("xy");

	}

	@Test
	public void sample5() {

		BiFunctionWithException<String, String, String, IOException> fonctionThrowingException = String::concat;

		BiFunction<String, String, CompletionStage<String>> functionWithOptionalResult = BiFunctionWithException
				.staged(fonctionThrowingException);

		CompletionStage<String> result = functionWithOptionalResult.apply("x", "z");

		assertThat(result.whenComplete((r, t) -> assertThat(t).isNull()).toCompletableFuture().join()).is("xz");

	}

}
