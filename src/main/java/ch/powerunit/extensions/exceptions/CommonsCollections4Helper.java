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

import static ch.powerunit.extensions.exceptions.Constants.verifyConsumer;
import static ch.powerunit.extensions.exceptions.Constants.verifyFunction;
import static ch.powerunit.extensions.exceptions.Constants.verifyPredicate;
import static ch.powerunit.extensions.exceptions.Constants.verifySupplier;
import static ch.powerunit.extensions.exceptions.ExceptionMapper.forException;
import static ch.powerunit.extensions.exceptions.ExceptionMapper.forExceptions;

import java.util.function.Function;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

/**
 * This class provides several helper methods for the functional interface of
 * the <a href=
 * "https://commons.apache.org/proper/commons-collections">commons-collections4</a>.
 * <p>
 * <b>This class is only available if commons-collections4 is available</b>
 * <p>
 * The mapping between the interface from <i>commons-collections4</i> and this
 * library is the following :
 *
 * <table border="1">
 * <caption>Mapping between interfaces</caption>
 * <tr>
 * <th>commons-collections4</th>
 * <th>powerunit-extensions-exceptions</th>
 * </tr>
 * <tr>
 * <td>Predicate</td>
 * <td>PredicateWithException</td>
 * </tr>
 * <tr>
 * <td>Factory</td>
 * <td>SupplierWithException</td>
 * </tr>
 * <tr>
 * <td>Transformer</td>
 * <td>FunctionWithException</td>
 * </tr>
 * <tr>
 * <td>Closure</td>
 * <td>ConsumerWithException</td>
 * </tr>
 * </table>
 *
 * @since 2.2.0
 *
 */
public final class CommonsCollections4Helper {

	private static final Function<Exception, RuntimeException> DEFAULT_EXCEPTION_MAPPER = forExceptions(
			forException(ClassCastException.class, e -> e), forException(IllegalArgumentException.class, e -> e),
			forException(Exception.class, FunctorException::new));

	private CommonsCollections4Helper() {
	}

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
	 *             In case the commons-collections4 library is not available.
	 * @throws NullPointerException
	 *             if predicate is null.
	 * @see org.apache.commons.collections4.Predicate
	 */
	public static <T> org.apache.commons.collections4.Predicate<T> asPredicate(PredicateWithException<T, ?> predicate) {
		return PredicateWithException.unchecked(verifyPredicate(predicate), DEFAULT_EXCEPTION_MAPPER)::test;
	}

	/**
	 * Transforms a {@link SupplierWithException} to the one from
	 * commons-collections.
	 *
	 * @param supplier
	 *            the {@link SupplierWithException} to be transformed to the one
	 *            from commons-collections.
	 * @param <T>
	 *            the type of the result of the supplier
	 * @return the {@link Factory factory} from commons-collections. The exception
	 *         are wrapped in a FunctorException.
	 * @throws NoClassDefFoundError
	 *             In case the commons-collections4 library is not available.
	 * @throws NullPointerException
	 *             if supplier is null.
	 * @see org.apache.commons.collections4.Factory
	 */
	public static <T> Factory<T> asFactory(SupplierWithException<T, ?> supplier) {
		return SupplierWithException.unchecked(verifySupplier(supplier), FunctorException::new)::get;
	}

	/**
	 * Transforms a {@link FunctionWithException} to the one from
	 * commons-collections.
	 *
	 * @param function
	 *            the {@link FunctionWithException} to be transformed to the one
	 *            from commons-collections.
	 * @param <I>
	 *            the input argument type of the function
	 * @param <O>
	 *            the result type of the function
	 * @return the {@link Transformer transformer} from commons-collections. The
	 *         ClassCastException and IllegalArgumentException are not wrapped and
	 *         the other exception are wrapped in a FunctorException.
	 * @throws NoClassDefFoundError
	 *             In case the commons-collections4 library is not available.
	 * @throws NullPointerException
	 *             if function is null.
	 * @see org.apache.commons.collections4.Transformer
	 */
	public static <I, O> Transformer<I, O> asTransformer(FunctionWithException<I, O, ?> function) {
		return FunctionWithException.unchecked(verifyFunction(function), DEFAULT_EXCEPTION_MAPPER)::apply;
	}

	/**
	 * Transforms a {@link ConsumerWithException} to the one from
	 * commons-collections.
	 *
	 * @param consumer
	 *            the {@link ConsumerWithException} to be transformed to the one
	 *            from commons-collections.
	 * @param <T>
	 *            the type of the input argument for the consumer
	 * @return the {@link Closure closure} from commons-collections. The
	 *         ClassCastException and IllegalArgumentException are not wrapped and
	 *         the other exception are wrapped in a FunctorException.
	 * @throws NoClassDefFoundError
	 *             In case the commons-collections4 library is not available.
	 * @throws NullPointerException
	 *             if consumer is null.
	 * @see org.apache.commons.collections4.Closure
	 */
	public static <T> Closure<T> asClosure(ConsumerWithException<T, ?> consumer) {
		return ConsumerWithException.unchecked(verifyConsumer(consumer), DEFAULT_EXCEPTION_MAPPER)::accept;
	}
}
