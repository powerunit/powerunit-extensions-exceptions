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
import java.util.function.Consumer;
import java.util.stream.Stream;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.ConsumerWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class ConsumerSamplesTest implements TestSuite {

	private class Handler<T> {
		public T object;
	}

	@Test
	public void sample1() {

		Handler<String> handler = new Handler<>();

		ConsumerWithException<String, IOException> consumerThrowingException = x -> {
			handler.object = "y" + x;
		};

		Consumer<String> consumerThrowingRuntimeException = ConsumerWithException.unchecked(consumerThrowingException);

		Stream.of("z").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yz");

	}

	@Test
	public void sample2() {

		ConsumerWithException<String, IOException> consumerThrowingException = ConsumerWithException
				.failing(IOException::new);

		Consumer<String> consumerThrowingRuntimeException = ConsumerWithException.unchecked(consumerThrowingException);

		assertWhen((x) -> {
			Stream.of("z").forEach(consumerThrowingRuntimeException);
		}).throwException(instanceOf(WrappedException.class));

	}

	@Test
	public void sample3() {

		Handler<String> handler = new Handler<>();

		ConsumerWithException<String, IOException> consumerThrowingException = x -> {
			handler.object = "y" + x;
		};

		Consumer<String> consumerThrowingRuntimeException = ConsumerWithException.unchecked(consumerThrowingException,
				IllegalArgumentException::new);

		Stream.of("z").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yz");

	}

	@Test
	public void sample4() {

		ConsumerWithException<String, IOException> consumerThrowingException = ConsumerWithException
				.failing(IOException::new);

		Consumer<String> consumerThrowingRuntimeException = ConsumerWithException.unchecked(consumerThrowingException,
				IllegalArgumentException::new);

		assertWhen((x) -> {
			Stream.of("z").forEach(consumerThrowingRuntimeException);
		}).throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample5() {

		Handler<String> handler = new Handler<>();

		ConsumerWithException<String, IOException> consumerThrowingException1 = x -> {
			handler.object = "y" + x;
		};

		ConsumerWithException<String, IOException> consumerThrowingException2 = x -> {
			handler.object = handler.object + "y" + x;
		};

		Consumer<String> consumerThrowingRuntimeException = ConsumerWithException
				.unchecked(consumerThrowingException1.andThen(consumerThrowingException2));

		Stream.of("z").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yzyz");

	}
}
