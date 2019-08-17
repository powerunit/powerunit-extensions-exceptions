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

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.RunnableWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class RunnableSamplesTest implements TestSuite {

	private class Handler<T> {
		public T object;
	}

	@Test
	public void sample1() {

		Handler<String> data = new Handler<>();

		RunnableWithException<IOException> supplierThrowingException = () -> data.object = "12";

		Runnable supplierThrowingRuntimeException = RunnableWithException.unchecked(supplierThrowingException);

		supplierThrowingRuntimeException.run();

		assertThat(data.object).is("12");

	}

	@Test
	public void sample2() {

		RunnableWithException<IOException> supplierThrowingException = RunnableWithException.failing(IOException::new);

		Runnable supplierThrowingRuntimeException = RunnableWithException.unchecked(supplierThrowingException);

		assertWhen(x -> supplierThrowingRuntimeException.run()).throwException(instanceOf(RuntimeException.class));

	}

	@Test
	public void sample3() {

		Handler<String> data = new Handler<>();

		RunnableWithException<IOException> supplierThrowingException = () -> data.object = "12";

		Runnable supplierThrowingRuntimeException = RunnableWithException.unchecked(supplierThrowingException,
				IllegalArgumentException::new);

		supplierThrowingRuntimeException.run();
		assertThat(data.object).is("12");

	}

	@Test
	public void sample4() {

		RunnableWithException<IOException> supplierThrowingException = RunnableWithException.failing(IOException::new);

		Runnable supplierThrowingRuntimeException = RunnableWithException.unchecked(supplierThrowingException,
				IllegalArgumentException::new);

		assertWhen(x -> supplierThrowingRuntimeException.run())
				.throwException(instanceOf(IllegalArgumentException.class));

	}
}
