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

import static ch.powerunit.extensions.exceptions.ExceptionMapper.forException;
import static ch.powerunit.extensions.exceptions.SupplierWithException.ignored;

import java.sql.SQLException;

import javax.xml.transform.TransformerException;

/**
 * @author borettim
 *
 */
final class Constants {

	public static final String OPERATION_CANT_BE_NULL = "operation can't be null";

	public static final String FUNCTION_CANT_BE_NULL = "function can't be null";

	public static final String SUPPLIER_CANT_BE_NULL = "supplier can't be null";

	public static final String PREDICATE_CANT_BE_NULL = "predicate can't be null";

	public static final String CONSUMER_CANT_BE_NULL = "consumer can't be null";

	public static final String EXCEPTIONMAPPER_CANT_BE_NULL = "exceptionMapper can't be null";

	public static final ExceptionMapper SQL_EXCEPTION_MAPPER = ignored(Constants::buildSQLExceptionMapper).get();

	public static final ExceptionMapper JAXBEXCEPTION_EXCEPTION_MAPPER = ignored(Constants::buildJAXBExceptionMapper)
			.get();

	public static final ExceptionMapper SAXEXCEPTION_EXCEPTION_MAPPER = ignored(Constants::buildSAXExceptionMapper)
			.get();

	public static final ExceptionMapper TRANSFORMEREXCEPTION_EXCEPTION_MAPPER = ignored(
			Constants::buildTransformerExceptionMapper).get();

	@SuppressWarnings("unchecked")
	private static ExceptionMapper buildSQLExceptionMapper() throws ClassNotFoundException {
		return forException((Class<Exception>) Class.forName("java.sql.SQLException"),
				e -> new WrappedException(String.format("%s - ErrorCode=%s ; SQLState=%s", e.getMessage(),
						((SQLException) e).getErrorCode(), ((SQLException) e).getSQLState()), e));
	}

	@SuppressWarnings("unchecked")
	private static ExceptionMapper buildJAXBExceptionMapper() throws ClassNotFoundException {
		return forException((Class<Exception>) Class.forName("javax.xml.bind.JAXBException"),
				e -> new WrappedException(String.format("%s", e.toString()), e));
	}

	@SuppressWarnings("unchecked")
	private static ExceptionMapper buildSAXExceptionMapper() throws ClassNotFoundException {
		return forException((Class<Exception>) Class.forName("org.xml.sax.SAXException"),
				e -> new WrappedException(String.format("%s", e.toString()), e));
	}

	@SuppressWarnings("unchecked")
	private static ExceptionMapper buildTransformerExceptionMapper() throws ClassNotFoundException {
		return forException((Class<Exception>) Class.forName("javax.xml.transform.TransformerException"),
				e -> new WrappedException(String.format("%s", ((TransformerException) e).getMessageAndLocation()), e));
	}

	private Constants() {
	}

}
