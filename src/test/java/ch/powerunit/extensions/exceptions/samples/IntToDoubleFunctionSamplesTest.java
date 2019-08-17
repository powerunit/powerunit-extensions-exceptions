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
import java.util.function.IntToDoubleFunction;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.IntToDoubleFunctionWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class IntToDoubleFunctionSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		IntToDoubleFunctionWithException<IOException> fonctionThrowingException = x -> 1;

		IntToDoubleFunction functionThrowingRuntimeException = IntToDoubleFunctionWithException
				.unchecked(fonctionThrowingException);

		assertThat(functionThrowingRuntimeException.applyAsDouble(2)).is(1d);

	}

	@Test
	public void sample2() {

		IntToDoubleFunctionWithException<IOException> fonctionThrowingException = IntToDoubleFunctionWithException
				.failing(IOException::new);

		IntToDoubleFunction functionThrowingRuntimeException = IntToDoubleFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.applyAsDouble(2), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample4() {

		IntToDoubleFunctionWithException<IOException> fonctionThrowingException = x -> 1;

		IntToDoubleFunction functionThrowingRuntimeException = IntToDoubleFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThat(functionThrowingRuntimeException.applyAsDouble(2)).is(1d);

	}

}
