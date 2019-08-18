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
import java.util.function.LongFunction;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.LongFunctionWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class LongFunctionSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		LongFunctionWithException<String, IOException> fonctionThrowingException = x -> "x";

		LongFunction<String> functionThrowingRuntimeException = LongFunctionWithException
				.unchecked(fonctionThrowingException);

		assertThat(functionThrowingRuntimeException.apply(2)).is("x");

	}

	@Test
	public void sample2() {

		LongFunctionWithException<String, IOException> fonctionThrowingException = LongFunctionWithException
				.failing(IOException::new);

		LongFunction<String> functionThrowingRuntimeException = LongFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.apply(2), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample3() {

		LongFunctionWithException<String, IOException> fonctionThrowingException = x -> "x";

		LongFunction<Optional<String>> functionWithOptionalResult = LongFunctionWithException
				.lifted(fonctionThrowingException);

		assertThat(functionWithOptionalResult.apply(1)).is(optionalIs("x"));

	}

	@Test
	public void sample4() {

		LongFunctionWithException<String, IOException> fonctionThrowingException = x -> "x";

		LongFunction<String> functionThrowingRuntimeException = LongFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThat(functionThrowingRuntimeException.apply(2)).is("x");

	}

}
