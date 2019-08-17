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
import java.util.function.DoubleToIntFunction;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.DoubleToIntFunctionWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class DoubleToIntFunctionSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		DoubleToIntFunctionWithException<IOException> fonctionThrowingException = x -> 1;

		DoubleToIntFunction functionThrowingRuntimeException = DoubleToIntFunctionWithException
				.unchecked(fonctionThrowingException);

		assertThat(functionThrowingRuntimeException.applyAsInt(2)).is(1);

	}

	@Test
	public void sample2() {

		DoubleToIntFunctionWithException<IOException> fonctionThrowingException = DoubleToIntFunctionWithException
				.failing(IOException::new);

		DoubleToIntFunction functionThrowingRuntimeException = DoubleToIntFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.applyAsInt(2), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample4() {

		DoubleToIntFunctionWithException<IOException> fonctionThrowingException = x -> 1;

		DoubleToIntFunction functionThrowingRuntimeException = DoubleToIntFunctionWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThat(functionThrowingRuntimeException.applyAsInt(2)).is(1);

	}

}
