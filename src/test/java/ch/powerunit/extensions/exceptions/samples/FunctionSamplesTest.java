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
import java.util.function.Function;

import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.FunctionWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class FunctionSamplesTest implements TestSuite {

	public void sample1() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException);

		assertThatFunction(functionThrowingRuntimeException, "x").is("x");

	}

	public void sample2() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.apply(x), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

}
