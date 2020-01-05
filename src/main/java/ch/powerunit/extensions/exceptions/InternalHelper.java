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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

final class InternalHelper {

	private InternalHelper() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends ExceptionHandlerSupport<?, ?, ?>> T documented(T target, Supplier<String> toString) {
		return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
				allInterfaces(target.getClass()).stream().distinct().toArray(Class[]::new), (proxy, method, args) -> {
					if (method.getName().equals("toString") && method.getParameterCount() == 0) {
						return toString.get();
					}
					try {
						return method.invoke(target, args);
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
				});
	}

	private static List<Class<?>> allInterfaces(Class<?> target) {
		var interfaces = new ArrayList<Class<?>>(Arrays.asList(target.getInterfaces()));
		if (target.getSuperclass() != null) {
			interfaces.addAll(allInterfaces(target.getSuperclass()));
		}
		return interfaces;
	}

}
