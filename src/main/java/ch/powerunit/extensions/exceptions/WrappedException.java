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

/**
 * RuntimeException to wrap the Exception.
 *
 * @author borettim
 *
 */
public final class WrappedException extends RuntimeException {

	private static final long serialVersionUID = -5914178098082781979L;

	/**
	 * Constructs a new wrapped exception with the specified detail message and
	 * cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this wrapped exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *            and indicates that the cause is nonexistent or unknown.)
	 */
	public WrappedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new wrapped exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public WrappedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new wrapped exception with the specified cause and a detail
	 * message of <tt>(cause.getMessage())</tt>
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method).
	 */
	public WrappedException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
