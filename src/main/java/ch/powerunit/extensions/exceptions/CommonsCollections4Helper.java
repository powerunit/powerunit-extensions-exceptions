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

import static ch.powerunit.extensions.exceptions.Constants.verifyPredicate;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.collections4.FunctorException;

/**
 * This class provides several helper methods for the functional interface of
 * the <a href=
 * "https://commons.apache.org/proper/commons-collections">commons-collections</a>.
 * <p>
 * <b>This class is only available if commons-collections is available</b>
 * 
 * @since 2.2.0
 *
 */
public final class CommonsCollections4Helper {
	private CommonsCollections4Helper() {
	}

	private static final Function<Exception, RuntimeException> DEFAULT_EXCEPTION_MAPPER = ExceptionMapper.forExceptions(
			ExceptionMapper.forException(ClassCastException.class, e -> e),
			ExceptionMapper.forException(IllegalArgumentException.class, e -> e),
			ExceptionMapper.forException(Exception.class, FunctorException::new));

	/**
	 * Transforms a {@link PredicateWithException} to the one from
	 * commons-collections.
	 * 
	 * @param predicate
	 *            the {@link PredicateWithException} to be transformed to the one
	 *            from commons-collections.
	 * @param <T>
	 *            the type of the input argument for the predicate
	 * @return the {@link org.apache.commons.collections4.Predicate predicate} from
	 *         commons-collections. The ClassCastException and
	 *         IllegalArgumentException are not wrapped and the other exception are
	 *         wrapped in a FunctorException.
	 * @throws NoClassDefFoundError
	 *             In case the commons-collections library is not available.
	 * @throws NullPointerException
	 *             if predicate is null.
	 * @see org.apache.commons.collections4.Predicate
	 */
	public static <T> org.apache.commons.collections4.Predicate<T> asPredicate(PredicateWithException<T, ?> predicate) {
		Predicate<T> internal = PredicateWithException.unchecked(verifyPredicate(predicate), DEFAULT_EXCEPTION_MAPPER);
		return new org.apache.commons.collections4.Predicate<T>() {

			@Override
			public boolean evaluate(T object) {
				return internal.test(object);
			}
		};
	}
}
