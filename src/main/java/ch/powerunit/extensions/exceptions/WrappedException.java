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

	public WrappedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WrappedException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrappedException(String message) {
		super(message);
	}

	public WrappedException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
