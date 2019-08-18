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
import java.util.Collections;
import java.util.function.BiConsumer;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.BiConsumerWithException;
import ch.powerunit.extensions.exceptions.BiFunctionWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class BiConsumerSamplesTest implements TestSuite {

	private class Handler<T> {
		public T object;
	}

	@Test
	public void sample1() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.unchecked(consumerThrowingException);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yxy");

	}

	@Test
	public void sample2() {

		BiConsumerWithException<String, String, IOException> consumerThrowingException = BiConsumerWithException
				.failing(IOException::new);

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.unchecked(consumerThrowingException);

		assertWhen((x) -> {
			Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);
		}).throwException(instanceOf(WrappedException.class));

	}

	@Test
	public void sample3() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yxy");

	}

	@Test
	public void sample4() {

		BiConsumerWithException<String, String, IOException> consumerThrowingException = BiConsumerWithException
				.failing(IOException::new);

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		assertWhen((x) -> {
			Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);
		}).throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample5() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.lifted(consumerThrowingException);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yxy");

	}

	@Test
	public void sample6() {

		BiConsumerWithException<String, String, IOException> consumerThrowingException = BiConsumerWithException
				.failing(IOException::new);

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.lifted(consumerThrowingException);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

	}

	@Test
	public void sample7() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.ignored(consumerThrowingException);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("yxy");

	}

	@Test
	public void sample8() {

		BiConsumerWithException<String, String, IOException> consumerThrowingException = BiConsumerWithException
				.failing(IOException::new);

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.ignored(consumerThrowingException);

		Collections.singletonMap("x", "y").forEach(consumerThrowingRuntimeException);

	}

	@Test
	public void sample9() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiFunctionWithException<String, String, String, IOException> consumerThrowingRuntimeException = BiConsumerWithException
				.asBiFunction(consumerThrowingException);

		assertThatBiFunction(consumerThrowingRuntimeException.uncheck(), "x", "z").isNull();

		assertThat(handler.object).is("yxz");

	}

	@Test
	public void sample10() {

		Handler<String> handler = new Handler<>();

		BiConsumerWithException<String, String, IOException> consumerThrowingException1 = (x, y) -> {
			handler.object = "y" + x + y;
		};

		BiConsumerWithException<String, String, IOException> consumerThrowingException2 = (x, y) -> {
			handler.object = "z" + y + x + handler.object;
		};

		BiConsumer<String, String> consumerThrowingRuntimeException = BiConsumerWithException
				.unchecked(consumerThrowingException1.andThen(consumerThrowingException2));

		Collections.singletonMap("a", "b").forEach(consumerThrowingRuntimeException);

		assertThat(handler.object).is("zbayab");

	}
}
