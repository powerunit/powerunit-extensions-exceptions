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
import java.util.function.DoubleConsumer;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.DoubleConsumerWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class DoubleConsumerSamplesTest implements TestSuite {

	private class Handler<T> {
		public T object;
	}

	@Test
	public void sample1() {

		Handler<String> handler = new Handler<>();

		DoubleConsumerWithException<IOException> consumerThrowingException = x -> {
			handler.object = "y" + x;
		};

		DoubleConsumer consumerThrowingRuntimeException = DoubleConsumerWithException
				.unchecked(consumerThrowingException);

		consumerThrowingRuntimeException.accept(12);

		assertThat(handler.object).is("y12.0");

	}

	@Test
	public void sample2() {

		DoubleConsumerWithException<IOException> consumerThrowingException = DoubleConsumerWithException
				.failing(IOException::new);

		DoubleConsumer consumerThrowingRuntimeException = DoubleConsumerWithException
				.unchecked(consumerThrowingException);

		assertWhen((x) -> {
			consumerThrowingRuntimeException.accept(12);
		}).throwException(instanceOf(WrappedException.class));

	}

	@Test
	public void sample3() {

		Handler<String> handler = new Handler<>();

		DoubleConsumerWithException<IOException> consumerThrowingException = x -> {
			handler.object = "y" + x;
		};

		DoubleConsumer consumerThrowingRuntimeException = DoubleConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		consumerThrowingRuntimeException.accept(12);

		assertThat(handler.object).is("y12.0");

	}

	@Test
	public void sample4() {

		DoubleConsumerWithException<IOException> consumerThrowingException = DoubleConsumerWithException
				.failing(IOException::new);

		DoubleConsumer consumerThrowingRuntimeException = DoubleConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		assertWhen((x) -> {
			consumerThrowingRuntimeException.accept(12);
		}).throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample5() {

		Handler<String> handler = new Handler<>();

		DoubleConsumerWithException<IOException> consumerThrowingException1 = x -> {
			handler.object = "y" + x;
		};

		DoubleConsumerWithException<IOException> consumerThrowingException2 = x -> {
			handler.object = handler.object + "y" + x;
		};

		DoubleConsumer consumerThrowingRuntimeException = DoubleConsumerWithException
				.unchecked(consumerThrowingException1.andThen(consumerThrowingException2));

		consumerThrowingRuntimeException.accept(23);

		assertThat(handler.object).is("y23.0y23.0");

	}
}
