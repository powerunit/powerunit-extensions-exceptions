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
import java.util.function.Supplier;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.exceptions.SupplierWithException;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class SupplierSamplesTest implements TestSuite {

	@Test
	public void sample1() {

		SupplierWithException<String, IOException> supplierThrowingException = () -> "12";

		Supplier<String> supplierThrowingRuntimeException = SupplierWithException.unchecked(supplierThrowingException);

		assertThat(supplierThrowingRuntimeException.get()).is("12");

	}

	@Test
	public void sample2() {

		SupplierWithException<String, IOException> supplierThrowingException = SupplierWithException
				.failing(IOException::new);

		Supplier<String> supplierThrowingRuntimeException = SupplierWithException.unchecked(supplierThrowingException);

		assertWhen(x -> supplierThrowingRuntimeException.get()).throwException(instanceOf(RuntimeException.class));

	}

	@Test
	public void sample3() {

		SupplierWithException<String, IOException> supplierThrowingException = () -> "12";

		Supplier<String> supplierThrowingRuntimeException = SupplierWithException.unchecked(supplierThrowingException,
				IllegalArgumentException::new);

		assertThat(supplierThrowingRuntimeException.get()).is("12");

	}

	@Test
	public void sample4() {

		SupplierWithException<String, IOException> supplierThrowingException = SupplierWithException
				.failing(IOException::new);

		Supplier<String> supplierThrowingRuntimeException = SupplierWithException.unchecked(supplierThrowingException,
				IllegalArgumentException::new);

		assertWhen(x -> supplierThrowingRuntimeException.get())
				.throwException(instanceOf(IllegalArgumentException.class));

	}
}
