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
import java.util.function.LongUnaryOperator;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.LongUnaryOperatorWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class LongUnaryOperatorSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		LongUnaryOperatorWithException<IOException> fonctionThrowingException = x -> x + 1;

		LongUnaryOperator functionThrowingRuntimeException = LongUnaryOperatorWithException
				.unchecked(fonctionThrowingException);

		assertThat(functionThrowingRuntimeException.applyAsLong(1)).is(2L);

	}

	@Test
	public void sample2() {

		LongUnaryOperatorWithException<IOException> fonctionThrowingException = LongUnaryOperatorWithException
				.failing(IOException::new);

		LongUnaryOperator functionThrowingRuntimeException = LongUnaryOperatorWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.applyAsLong(x), 1)
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample3() {

		LongUnaryOperatorWithException<IOException> fonctionThrowingException = x -> x + 1;

		LongUnaryOperator functionWithOptionalResult = LongUnaryOperatorWithException.lifted(fonctionThrowingException);

		assertThat(functionWithOptionalResult.applyAsLong(1)).is(2L);

	}

	@Test
	public void sample4() {

		LongUnaryOperatorWithException<IOException> fonctionThrowingException = x -> x + 1;

		LongUnaryOperator functionThrowingRuntimeException = LongUnaryOperatorWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThat(functionThrowingRuntimeException.applyAsLong(1)).is(2L);

	}

}
