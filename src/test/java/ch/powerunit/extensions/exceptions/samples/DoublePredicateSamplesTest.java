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
import java.util.function.DoublePredicate;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.DoublePredicateWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class DoublePredicateSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		DoublePredicateWithException<IOException> fonctionThrowingException = x -> true;

		DoublePredicate functionThrowingRuntimeException = DoublePredicateWithException
				.unchecked(fonctionThrowingException);

		assertThat(functionThrowingRuntimeException.test(2)).is(true);

	}

	@Test
	public void sample2() {

		DoublePredicateWithException<IOException> fonctionThrowingException = DoublePredicateWithException
				.failing(IOException::new);

		DoublePredicate functionThrowingRuntimeException = DoublePredicateWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertWhen(x -> functionThrowingRuntimeException.test(2), "x")
				.throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample4() {

		DoublePredicateWithException<IOException> fonctionThrowingException = x -> true;

		DoublePredicate functionThrowingRuntimeException = DoublePredicateWithException
				.unchecked(fonctionThrowingException, IllegalArgumentException::new);

		assertThat(functionThrowingRuntimeException.test(2)).is(true);

	}

}
