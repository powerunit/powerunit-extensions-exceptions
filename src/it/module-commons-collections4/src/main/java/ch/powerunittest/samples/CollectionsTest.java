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
package ch.powerunittest.samples;

import java.util.function.Function;

import java.io.IOException;

import ch.powerunit.extensions.exceptions.CommonsCollections4Helper;
import ch.powerunit.extensions.exceptions.ExceptionHandlerSupport;
import ch.powerunit.extensions.exceptions.ExceptionMapper;
import ch.powerunit.extensions.exceptions.FunctionWithException;
import ch.powerunit.extensions.exceptions.WrappedException;

public class CollectionsTest {

	public static void sample1() {

		FunctionWithException<String, String, IOException> fonctionThrowingException = x -> x;

		Function<String, String> functionThrowingRuntimeException = FunctionWithException
				.unchecked(fonctionThrowingException);

		if (!"x".equals(functionThrowingRuntimeException.apply("x"))) {
			throw new IllegalArgumentException("The result is not correct");
		}

	}

	public static void sample2() {

		try {
			CommonsCollections4Helper.asPredicate(x -> true);
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			return;
		}
		throw new IllegalArgumentException("No exception thrown");
	}

	public static void sample3() {
		
		if (!CommonsCollections4Helper.asPredicate(x -> true).evaluate("x")) {
			throw new IllegalArgumentException("Should be true");
		}
	}

	public static void main(String[] args) {
		sample1();
		if (args.length > 0 && "NO".equals(args[0])) {
			sample2();
		} else if (args.length > 0 && "YES".equals(args[0])) {
			sample3();
		}
	}

}