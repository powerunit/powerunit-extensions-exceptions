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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface to help create mapper function for exception wrapping.
 * 
 * @since 2.0.0
 */
public interface ExceptionMapper extends Function<Exception, RuntimeException> {

	/**
	 * Exception wrapper, that may be used to copy error code and sql state to the
	 * {@code WrappedException} message.
	 * <p>
	 * <b>This mapper will only works correctly if the module <i>java.sql</i> is
	 * available.</b>
	 * <p>
	 * 
	 * @return the Mapper for {@code SQLException}.
	 * @throws NoClassDefFoundError
	 *             In case the {@code SQLException} is not available (java.sql
	 *             module missing).
	 */
	static ExceptionMapper sqlExceptionMapper() {
		return Optional.ofNullable(Constants.SQL_EXCEPTION_MAPPER)
				.orElseThrow(() -> new NoClassDefFoundError("Unable to find the sqlException"));
	}

	/**
	 * Exception wrapper, that may be used to copy jaxb information to the
	 * {@code WrappedException} message.
	 * <p>
	 * <b>This mapper will only works correctly if the class <i>JAXBException</i> is
	 * available.</b>
	 * <p>
	 * 
	 * @return the Mapper for {@code JAXBException}.
	 * @throws NoClassDefFoundError
	 *             In case the {@code JAXBException} is not available.
	 * @since 2.1.0
	 */
	static ExceptionMapper jaxbExceptionMapper() {
		return Optional.ofNullable(Constants.JAXBEXCEPTION_EXCEPTION_MAPPER)
				.orElseThrow(() -> new NoClassDefFoundError("Unable to find the JAXBException"));
	}

	/**
	 * Exception wrapper, that may be used to copy sax information to the
	 * {@code WrappedException} message.
	 * <p>
	 * <b>This mapper will only works correctly if the class <i>SAXException</i> is
	 * available.</b>
	 * <p>
	 * 
	 * @return the Mapper for {@code SAXException}.
	 * @throws NoClassDefFoundError
	 *             In case the {@code SAXException} is not available (java.xml
	 *             module missing).
	 * @since 2.1.0
	 */
	static ExceptionMapper saxExceptionMapper() {
		return Optional.ofNullable(Constants.SAXEXCEPTION_EXCEPTION_MAPPER)
				.orElseThrow(() -> new NoClassDefFoundError("Unable to find the SAXException"));
	}

	/**
	 * Exception wrapper, that may be used to copy transformer exception information
	 * to the {@code WrappedException} message.
	 * <p>
	 * <b>This mapper will only works correctly if the class <i>SAXException</i> is
	 * available.</b>
	 * <p>
	 * 
	 * @return the Mapper for {@code TransformerException}.
	 * @throws NoClassDefFoundError
	 *             In case the {@code TransformerException} is not available
	 *             (java.xml module missing).
	 * @since 2.1.0
	 */
	static ExceptionMapper transformerExceptionMapper() {
		return Optional.ofNullable(Constants.TRANSFORMEREXCEPTION_EXCEPTION_MAPPER)
				.orElseThrow(() -> new NoClassDefFoundError("Unable to find the TransformerException"));
	}

	Class<? extends Exception> targetException();

	/**
	 * Helper method to create exception wrapper that check the exception class.
	 * 
	 * @param clazz
	 *            the class of the exception to be wrapped.
	 * @param mapper
	 *            the exception mapper.
	 * @param <E>
	 *            the type of the exception.
	 * @return A new exception mapper, which use the one received as parameter or
	 *         for the other exception just create a {@code WrappedException}.
	 */
	static <E extends Exception> ExceptionMapper forException(Class<E> clazz, Function<E, RuntimeException> mapper) {
		return new ExceptionMapper() {

			@Override
			public RuntimeException apply(Exception t) {
				return clazz.isInstance(t) ? mapper.apply(clazz.cast(t)) : new WrappedException(t);
			}

			@Override
			public Class<E> targetException() {
				return clazz;
			}
		};
	}

	/**
	 * Helper method to create exception wrapper to use the first mapper if
	 * applicable or else the second.
	 * 
	 * @param mapper1
	 *            the first mapper to be used.
	 * @param mapper2
	 *            the second mapper to used.
	 * @return the mapping function.
	 */
	static Function<Exception, RuntimeException> forExceptions(ExceptionMapper mapper1, ExceptionMapper mapper2) {
		return e -> mapper1.targetException().isInstance(e) ? mapper1.apply(e) : mapper2.apply(e);
	}

	/**
	 * Helper method to create exception wrapper to use the first mapper if
	 * applicable or else the second or else the third
	 * 
	 * @param mapper1
	 *            the first mapper to be used.
	 * @param mapper2
	 *            the second mapper to used.
	 * @param mapper3
	 *            the third mapper to used.
	 * @return the mapping function.
	 */
	static Function<Exception, RuntimeException> forExceptions(ExceptionMapper mapper1, ExceptionMapper mapper2,
			ExceptionMapper mapper3) {
		Function<Exception, RuntimeException> last = forExceptions(mapper2, mapper3);
		return e -> mapper1.targetException().isInstance(e) ? mapper1.apply(e) : last.apply(e);
	}

	/**
	 * Helper method to create exception wrapper that use the first one that is
	 * applicable.
	 * 
	 * @param mappers
	 *            the mapper to be tried
	 * @return the mapping function.
	 */
	static Function<Exception, RuntimeException> forExceptions(ExceptionMapper... mappers) {
		return e -> Arrays.stream(mappers).sequential().filter(m -> m.targetException().isInstance(e)).limit(1)
				.map(m -> m.apply(m.targetException().cast(e))).findFirst().orElseGet(() -> new WrappedException(e));
	}

}
