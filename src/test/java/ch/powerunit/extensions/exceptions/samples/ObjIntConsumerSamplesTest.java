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
import java.util.function.ObjIntConsumer;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.ObjIntConsumerWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class ObjIntConsumerSamplesTest implements TestSuite {

	private class Handler<T> {
		public T object;
	}

	@Test
	public void sample1() {

		Handler<String> handler = new Handler<>();

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.unchecked(consumerThrowingException);

		consumerThrowingRuntimeException.accept("y", 12);

		assertThat(handler.object).is("yy12");

	}

	@Test
	public void sample2() {

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = ObjIntConsumerWithException
				.failing(IOException::new);

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.unchecked(consumerThrowingException);

		assertWhen((x) -> {
			consumerThrowingRuntimeException.accept("y", 12);
		}).throwException(instanceOf(WrappedException.class));

	}

	@Test
	public void sample3() {

		Handler<String> handler = new Handler<>();

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		consumerThrowingRuntimeException.accept("y", 12);

		assertThat(handler.object).is("yy12");

	}

	@Test
	public void sample4() {

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = ObjIntConsumerWithException
				.failing(IOException::new);

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.unchecked(consumerThrowingException, IllegalArgumentException::new);

		assertWhen((x) -> {
			consumerThrowingRuntimeException.accept("y", 12);
		}).throwException(instanceOf(IllegalArgumentException.class));

	}

	@Test
	public void sample5() {

		Handler<String> handler = new Handler<>();

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.lifted(consumerThrowingException);

		consumerThrowingRuntimeException.accept("y", 12);

		assertThat(handler.object).is("yy12");

	}

	@Test
	public void sample6() {

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = ObjIntConsumerWithException
				.failing(IOException::new);

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.lifted(consumerThrowingException);

		consumerThrowingRuntimeException.accept("y", 12);

	}

	@Test
	public void sample7() {

		Handler<String> handler = new Handler<>();

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = (x, y) -> {
			handler.object = "y" + x + y;
		};

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.ignored(consumerThrowingException);

		consumerThrowingRuntimeException.accept("y", 12);

		assertThat(handler.object).is("yy12");

	}

	@Test
	public void sample8() {

		ObjIntConsumerWithException<String, IOException> consumerThrowingException = ObjIntConsumerWithException
				.failing(IOException::new);

		ObjIntConsumer<String> consumerThrowingRuntimeException = ObjIntConsumerWithException
				.ignored(consumerThrowingException);

		consumerThrowingRuntimeException.accept("y", 12);

	}

}
