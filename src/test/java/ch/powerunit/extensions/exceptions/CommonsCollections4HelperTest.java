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
package ch.powerunit.extensions.exceptions;

import org.apache.commons.collections4.FunctorException;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

@SuppressWarnings("squid:S2187") // Sonar doesn't under that it is really a test
public class CommonsCollections4HelperTest implements TestSuite {

	// AsPredicate

	@Test
	public void testAsPredicateNoException() {
		assertThat(CommonsCollections4Helper.asPredicate(x -> true).evaluate("x")).is(true);
	}

	@Test
	public void testAsPredicateClassCastException() {
		assertWhen(() -> CommonsCollections4Helper.asPredicate(PredicateWithException.failing(ClassCastException::new))
				.evaluate("x")).throwException(instanceOf(ClassCastException.class));
	}

	@Test
	public void testAsPredicateIllegalArgumentException() {
		assertWhen(() -> CommonsCollections4Helper
				.asPredicate(PredicateWithException.failing(IllegalArgumentException::new)).evaluate("x"))
						.throwException(instanceOf(IllegalArgumentException.class));
	}

	@Test
	public void testAsPredicateOtherException() {
		assertWhen(() -> CommonsCollections4Helper.asPredicate(PredicateWithException.failing(Exception::new))
				.evaluate("x")).throwException(instanceOf(FunctorException.class));
	}

	// AsFactory

	@Test
	public void testAsFactoryNoException() {
		assertThat(CommonsCollections4Helper.asFactory(() -> "").create()).is("");
	}

	@Test
	public void testAsFactoryOtherException() {
		assertWhen(() -> CommonsCollections4Helper.asFactory(SupplierWithException.failing(Exception::new)).create())
				.throwException(instanceOf(Exception.class));
	}

	// AsTransformer

	@Test
	public void testAsTransformerNoException() {
		assertThat(CommonsCollections4Helper.asTransformer(x -> "y").transform("x")).is("y");
	}

	@Test
	public void testAsTransformerClassCastException() {
		assertWhen(() -> CommonsCollections4Helper.asTransformer(FunctionWithException.failing(ClassCastException::new))
				.transform("x")).throwException(instanceOf(ClassCastException.class));
	}

	@Test
	public void testAsTransformerIllegalArgumentException() {
		assertWhen(() -> CommonsCollections4Helper
				.asTransformer(FunctionWithException.failing(IllegalArgumentException::new)).transform("x"))
						.throwException(instanceOf(IllegalArgumentException.class));
	}

	@Test
	public void testAsTransformerOtherException() {
		assertWhen(() -> CommonsCollections4Helper.asTransformer(FunctionWithException.failing(RuntimeException::new))
				.transform("x")).throwException(instanceOf(FunctorException.class));
	}

	// AsClosure

	@Test
	public void testAsClosureNoException() {
		CommonsCollections4Helper.asClosure(x -> {
		}).execute("x");
	}

	@Test
	public void testAsClosureClassCastException() {
		assertWhen((x) -> CommonsCollections4Helper.asClosure(ConsumerWithException.failing(ClassCastException::new))
				.execute("x")).throwException(instanceOf(ClassCastException.class));
	}

	@Test
	public void testAsClosureIllegalArgumentException() {
		assertWhen((x) -> CommonsCollections4Helper
				.asClosure(ConsumerWithException.failing(IllegalArgumentException::new)).execute("x"))
						.throwException(instanceOf(IllegalArgumentException.class));
	}

	@Test
	public void testAsClosureOtherException() {
		assertWhen((x) -> CommonsCollections4Helper.asClosure(ConsumerWithException.failing(RuntimeException::new))
				.execute("x")).throwException(instanceOf(FunctorException.class));
	}

}
